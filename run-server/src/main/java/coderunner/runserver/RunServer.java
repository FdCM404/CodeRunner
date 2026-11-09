package coderunner.runserver;

import coderunner.proto.*;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RunServer {
    
    private static final String DOCKER_IMAGE = "python";
    private static final String DOCKER_IMAGE_TAG = "3.12-slim";
    private static final long DOCKER_MEMORY = 256L * 1024 * 1024;
    private static final String DOCKER_COMMAND =
            "timeout 10 python /work/program.py < /work/input.txt > /work/out.txt 2> /work/err.txt;"
                    + " echo $? > /work/exit.txt";
    private static final String DEFAULT_MANAGER_HOST = "127.0.0.1";
    private static final int DEFAULT_MANAGER_PORT = 5000;
    private static final String DEFAULT_REDIS_HOST = "127.0.0.1";
    private static final int DEFAULT_REDIS_PORT = 6379;
    private static final String WORK_VOLUME_ENV = "RUNSERVER_WORK_VOLUME";
    private static final String REDIS_KEY_PREFIX = "coderunner:request:";
    private static final int MAX_PROGRAM_BYTES = 1_048_576;
    private static final int MAX_INPUT_BYTES = 1_048_576;


    private final String host;
    private final int port;
    private final String managerHost;
    private final int managerPort;
    private JedisPooled redis;
    private final Path workRoot;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private void log(String message) {
        System.out.printf("[%s] [RunServer %s:%d] %s%n", Instant.now(), host, port, message);
    }

    RunServer(String host, int port, String managerHost, int managerPort, String redisHost, int redisPort, Path workRoot) {
        this.host = host; this.port = port; this.managerHost = managerHost; this.managerPort = managerPort;
        this.redis = new JedisPooled(redisHost, redisPort);
        this.workRoot = workRoot;
        log("Redis client configured address=" + redisHost + ":" + redisPort
                + " ping=" + redis.ping() + " workRoot=" + workRoot.toAbsolutePath());
    }

    private final class Service extends ServiceGrpc.ServiceImplBase {
        
        @Override
        public StreamObserver<SubmitChunk> submit(StreamObserver<SubmitResponse> response) {
            String id = UUID.randomUUID().toString();
            Path dir = workRoot.resolve(id);
            log("gRPC submit opened requestId=" + id + " workDir=" + dir.toAbsolutePath());
            try { 
                Files.createDirectories(dir); 
            } catch (IOException e) {
                log("submit failed creating workDir requestId=" + id + " error=" + e.getMessage());
                response.onError(io.grpc.Status.INTERNAL.withCause(e).asRuntimeException()); return cancelled();
            }
            return new StreamObserver<>() 
            {
                private final Path program = dir.resolve("program.py");
                private final Path input = dir.resolve("input.txt");
                private boolean receivedProgram;
                private long programBytes;
                private long inputBytes;
                private boolean failed;
                

                @Override
                public void onNext(SubmitChunk chunk) {
                    int chunkBytes = chunk.hasProgram() ? chunk.getProgram().size()
                            : chunk.hasStdin() ? chunk.getStdin().size() : 0;
                    log("submit chunk requestId=" + id + " type="
                            + (chunk.hasProgram() ? "program" : chunk.hasStdin() ? "stdin" : "invalid")
                            + " bytes=" + chunkBytes);
                    if (failed || (!chunk.hasProgram() && !chunk.hasStdin())) {
                        fail(response, dir, "Each upload chunk must contain program or stdin");
                        return;
                    }
                    try {
                        if (chunk.hasProgram()) {
                            programBytes += chunk.getProgram().size();
                            if (programBytes > MAX_PROGRAM_BYTES) {
                                fail(response, dir, "Program is missing or exceeds its size limit");
                                return;
                            }
                            receivedProgram = true;
                            Files.write(program, chunk.getProgram().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        } else {
                            inputBytes += chunk.getStdin().size();
                            if (inputBytes > MAX_INPUT_BYTES) {
                                fail(response, dir, "Input exceeds its size limit");
                                return;
                            }
                            Files.write(input, chunk.getStdin().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        }
                    } catch (IOException e) {
                        log("submit failed storing chunk requestId=" + id + " error=" + e.getMessage());
                        fail(response, dir, "Unable to store uploaded data: " + e.getMessage());
                    }
                }
                @Override
                public void onError(Throwable t) {
                    log("submit stream error requestId=" + id + " error=" + t.getMessage());
                    deleteTree(dir);
                }
                @Override
                public void onCompleted() {
                    log("submit stream completed requestId=" + id + " programBytes=" + programBytes
                            + " inputBytes=" + inputBytes);
                    if (failed || !receivedProgram) {
                        if (!failed) fail(response, dir, "A program is required");
                        return;
                    }
                    try { if (!Files.exists(input)) Files.createFile(input); } catch (IOException e) {
                        fail(response, dir, "Unable to create input file: " + e.getMessage()); return;
                    }
                    try {
                        String key = REDIS_KEY_PREFIX + id;
                        log("Redis HSET key=" + key + " state=PENDING owner=" + host + ":" + port);
                        redis.hset(key, java.util.Map.of(
                                "state", "PENDING", "owner", host + ":" + port));
                    } catch (RuntimeException e) {
                        fail(response, dir, "Unable to register request: " + e.getMessage()); return;
                    }
                    response.onNext(SubmitResponse.newBuilder().setId(id).build()); response.onCompleted();
                    log("submit response requestId=" + id + " state=PENDING");
                    executor.submit(() -> execute(id, dir));
                }

                private void fail(StreamObserver<SubmitResponse> observer, Path requestDir, String message) {
                    if (failed) return;
                    failed = true;
                    log("submit rejected requestId=" + id + " reason=" + message);
                    deleteTree(requestDir);
                    observer.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException());
                }
            };
        }

        @Override
        public void getResult(RequestId request, StreamObserver<Result> response)
        {
            String key = REDIS_KEY_PREFIX + request.getId();
            log("gRPC getResult requestId=" + request.getId() + " Redis HGET key=" + key + " field=state");

            String state = redis.hget(key, "state");
            log("Redis HGET result key=" + key + " state=" + (state == null ? "<missing>" : state));

            if (state == null) { response.onError(io.grpc.Status.NOT_FOUND.asRuntimeException()); return; }

            Result.Builder result = Result.newBuilder().setId(request.getId()).setState(RequestState.valueOf(state));
            result.setOwnerHost(host).setOwnerPort(port);

            if ("FINISHED".equals(state) || "FAILED".equals(state) || "TIMEOUT".equals(state)) {
                Path dir = workRoot.resolve(request.getId());
                try {
                    Result.Builder builder = result.setStdout(ByteString.copyFrom(readRequired(dir.resolve("out.txt"))))
                            .setStderr(ByteString.copyFrom(readRequired(dir.resolve("err.txt"))));
                    response.onNext(result.build());
                    response.onCompleted();
                    log("getResult completed requestId=" + request.getId()
                            + " state=" + state + " stdoutBytes=" + result.getStdout().size()
                            + " stderrBytes=" + result.getStderr().size());
                    deleteTree(dir);
                    log("Redis DEL key=" + key + " and deleting " + dir.toAbsolutePath());
                    redis.del(key);
                } catch (IOException e) {
                    response.onError(io.grpc.Status.INTERNAL.withDescription("Unable to read result").withCause(e).asRuntimeException());
                }
                return;
            }
            response.onNext(result.build()); response.onCompleted();
        }
    }

    private void execute(String id, Path dir) {
        String key = REDIS_KEY_PREFIX + id;
        log("execution started requestId=" + id + " Redis HSET key=" + key + " state=RUNNING");
        redis.hset(key, "state", "RUNNING");
        String containerId = null;
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        ApacheDockerHttpClient http = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        DockerClient docker = DockerClientImpl.getInstance(config, http);
        try {
            pullIfMissing(docker, DOCKER_IMAGE, DOCKER_IMAGE_TAG);
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withNetworkMode("none")
                    .withMemory(DOCKER_MEMORY)
                    .withMemorySwap(DOCKER_MEMORY);
            String workPath = "/work";
            String workVolume = System.getenv(WORK_VOLUME_ENV);
            if (workVolume == null || workVolume.isBlank()) 
            {
                hostConfig.withBinds(new Bind(dir.toAbsolutePath().toString(), new Volume("/work")));
            } else {
                hostConfig.withMounts(List.of(new Mount()
                        .withType(MountType.VOLUME)
                        .withSource(workVolume)
                        .withTarget("/work")));
                workPath += "/" + id;
            }
            String dockerCommand = workPath.equals("/work")
                    ? DOCKER_COMMAND
                    : "timeout 10 python " + workPath + "/program.py < " + workPath
                            + "/input.txt > " + workPath + "/out.txt 2> " + workPath
                            + "/err.txt; echo $? > " + workPath + "/exit.txt";
            CreateContainerCmd create = docker.createContainerCmd(DOCKER_IMAGE + ":" + DOCKER_IMAGE_TAG)
                    .withName("run-" + id)
                    .withHostConfig(hostConfig)
                    .withCmd("sh", "-c", dockerCommand);
            String user = currentUser();
            if (user != null) create.withUser(user);
            containerId = create.exec().getId();
            docker.startContainerCmd(containerId).exec();
            log("docker started requestId=" + id + " containerId=" + containerId
                    + " containerImage=python:3.12-slim network=none memory=256m");
            docker.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitStatusCode();
            int exitCode = readExitCode(dir.resolve("exit.txt"));
            String state = exitCode == 0 ? "FINISHED" : exitCode == 124 ? "TIMEOUT" : "FAILED";
            log("docker finished requestId=" + id + " exitCode=" + exitCode + " Redis HSET state=" + state);
            redis.hset(key, "state", state);
        } catch (Exception ex) {
            try {
                Files.writeString(dir.resolve("err.txt"),
                        ex.getMessage() == null ? ex.toString() : ex.getMessage());
            } catch (IOException writeError) {
                log("unable to store execution error requestId=" + id + " error=" + writeError.getMessage());
            }
            redis.hset(key, "state", "FAILED");
            log("execution failed requestId=" + id + " Redis HSET state=FAILED error=" + ex.getMessage());
        } finally {
            if (containerId != null) {
                try {
                    docker.removeContainerCmd(containerId).withForce(true).exec();
                    log("docker removed requestId=" + id + " containerId=" + containerId);
                } catch (NotFoundException ignored) {
                    log("docker container already removed requestId=" + id + " containerId=" + containerId);
                } catch (RuntimeException cleanupError) {
                    log("unable to remove docker container requestId=" + id
                            + " containerId=" + containerId + " error=" + cleanupError.getMessage());
                }
            }
            try {
                docker.close();
            } catch (IOException closeError) {
                log("unable to close docker client requestId=" + id
                        + " error=" + closeError.getMessage());
            }
        }
    }
    
    
    
    //TP1/.../ContainerRunner.java
    private static int readExitCode(Path file) throws IOException {
        return Files.exists(file) ? Integer.parseInt(Files.readString(file).trim()) : -1;
    }

    //TP1/.../ContainerRunner.java
    private static String currentUser() {
        if (System.getProperty("os.name").startsWith("Windows")) return null;
        com.sun.security.auth.module.UnixSystem unix = new com.sun.security.auth.module.UnixSystem();
        return unix.getUid() + ":" + unix.getGid();
    }

    //TP1/.../ContainerRunner.java
    private static void pullIfMissing(DockerClient docker, String image, String tag)
            throws InterruptedException {
        try {
            docker.inspectImageCmd(image + ":" + tag).exec();
        } catch (NotFoundException e) {
            System.out.println("Pulling " + image + ":" + tag + " ...");
            docker.pullImageCmd(image).withTag(tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        }
    }

    private static byte[] readRequired(Path path) throws IOException {
        return Files.exists(path) ? Files.readAllBytes(path) : new byte[0];
    }
    private static void deleteTree(Path root) {
        try (var stream = Files.walk(root)) {
            stream.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); }
                catch (IOException e) { System.err.println("Unable to delete " + p + ": " + e.getMessage()); }
            });
        } catch (IOException e) {
            System.err.println("Unable to clean request directory " + root + ": " + e.getMessage());
        }
    }
    private static StreamObserver<SubmitChunk> cancelled() {
        return new StreamObserver<>() {
            public void onNext(SubmitChunk ignored) {}
            public void onError(Throwable ignored) {}
            public void onCompleted() {}
        };
    }

    public void start() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(managerHost, managerPort).usePlaintext().build();
        try {
            ManagerRegistrationGrpc.ManagerRegistrationBlockingStub manager =
                    ManagerRegistrationGrpc.newBlockingStub(channel);
            RegisterResponse registered = manager.register(RegisterRequest.newBuilder().setHost(host).setPort(port).build());
            redis.close();
            redis = new JedisPooled(registered.getRedisHost(), registered.getRedisPort());
            log("registered with ManagerServer=" + managerHost + ":" + managerPort
                    + " Redis=" + registered.getRedisHost() + ":" + registered.getRedisPort());
        } finally {
            channel.shutdownNow();
        }
        Server server = ServerBuilder.forPort(port).addService(new Service()).build().start();
        log("listening address=0.0.0.0:" + port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) throw new IllegalArgumentException("Usage: RunServer <host> <port> [managerHost] [managerPort] [redisHost] [redisPort]");
        String managerHost = args.length > 2 ? args[2] : DEFAULT_MANAGER_HOST;
        int managerPort = args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_MANAGER_PORT;
        String redisHost = args.length > 4 ? args[4] : DEFAULT_REDIS_HOST;
        int redisPort = args.length > 5 ? Integer.parseInt(args[5]) : DEFAULT_REDIS_PORT;
        new RunServer(args[0], Integer.parseInt(args[1]), managerHost, managerPort, redisHost, redisPort, Paths.get("requests")).start();
    }
}



/* 
Blocking Stub. A chamada bloqueia ate receber resposta

RegisterResponse registered =
        manager.register(RegisterRequest.newBuilder()
                .setHost(host)
                .setPort(port)
                .build());

É usado para o RunServer se registar no ManagerServer e receber o endereço do Redis.

*/ 