package coderunner.client;

import coderunner.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Client {
    private static final String DEFAULT_MANAGER_HOST = "127.0.0.1";
    private static final int DEFAULT_MANAGER_PORT = 5000;
    private final ManagedChannel managerChannel;
    private final ServerDirectoryGrpc.ServerDirectoryBlockingStub directory;

    private static void log(String message) {
        System.out.printf("[%s] [Client] %s%n", Instant.now(), message);
    }

    public Client(String managerHost, int managerPort) {
        log("connecting ManagerServer=" + managerHost + ":" + managerPort);
        managerChannel = ManagedChannelBuilder.forAddress(managerHost, managerPort).usePlaintext().build();
        directory = ServerDirectoryGrpc.newBlockingStub(managerChannel);
    }

    public String submit(Path program, Path input) throws Exception {
        log("getServer request managerChannel active; program=" + program
                + " programBytes=" + Files.size(program)
                + " input=" + (input == null ? "<none>" : input)
                + " inputBytes=" + (input == null ? 0 : Files.size(input)));
        ServerAddress address = directory.getServer(Empty.getDefaultInstance());
        log("getServer response RunServer=" + address.getHost() + ":" + address.getPort());
        ManagedChannel channel = channel(address);
        try {
            RunnerGrpc.RunnerStub runner = RunnerGrpc.newStub(channel);
            CountDownLatch done = new CountDownLatch(1);
            String[] id = new String[1];
            AtomicReference<Throwable> failure = new AtomicReference<>();
            StreamObserver<SubmitChunk> request = runner.submit(new StreamObserver<>() {
                public void onNext(SubmitResponse response) { id[0] = response.getId(); }
                public void onError(Throwable error) { failure.set(error); done.countDown(); }
                public void onCompleted() { done.countDown(); }
            });

            //Envio do programa (vindo do input do cliente) para o RunServer
            request.onNext(SubmitChunk.newBuilder().setProgram(com.google.protobuf.ByteString.copyFrom(Files.readAllBytes(program))).build());
            log("submit sent program chunk bytes=" + Files.size(program)
                    + " to RunServer=" + address.getHost() + ":" + address.getPort());
            if (input != null) request.onNext(SubmitChunk.newBuilder().setStdin(com.google.protobuf.ByteString.copyFrom(Files.readAllBytes(input))).build());
            if (input != null) log("submit sent stdin chunk bytes=" + Files.size(input));
            request.onCompleted();
            if (!done.await(30, TimeUnit.SECONDS) || id[0] == null) {
                throw new IllegalStateException("Submission failed", failure.get());
            }
            log("submit response requestId=" + id[0]);
            return id[0];
        } finally {
            channel.shutdownNow();
        }
    }

    public Result getResult(String id) {
        log("locate request requestId=" + id);
        ServerAddress address = directory.locate(RequestId.newBuilder().setId(id).build());
        log("locate response requestId=" + id + " RunServer=" + address.getHost() + ":" + address.getPort());
        ManagedChannel channel = channel(address);
        try {
            Iterator<Result> results = RunnerGrpc.newBlockingStub(channel)
                    .getResult(RequestId.newBuilder().setId(id).build());
            Result.Builder combined = Result.newBuilder().setId(id);
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            while (results.hasNext()) {
                Result result = results.next();
                log("getResult chunk requestId=" + id + " state=" + result.getState()
                        + " stdoutBytes=" + result.getStdout().size()
                        + " stderrBytes=" + result.getStderr().size());
                combined.setState(result.getState())
                        .setOwnerHost(result.getOwnerHost())
                        .setOwnerPort(result.getOwnerPort());
                stdout.writeBytes(result.getStdout().toByteArray());
                stderr.writeBytes(result.getStderr().toByteArray());
            }
            return combined.setStdout(com.google.protobuf.ByteString.copyFrom(stdout.toByteArray()))
                    .setStderr(com.google.protobuf.ByteString.copyFrom(stderr.toByteArray()))
                    .build();
        } finally {
            channel.shutdownNow();
        }
    }

    private static ManagedChannel channel(ServerAddress address) {
        return ManagedChannelBuilder.forAddress(address.getHost(), (int) address.getPort()).usePlaintext().build();
    }

    public void close() {
        log("closing ManagerServer channel");
        managerChannel.shutdown();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) throw new IllegalArgumentException("Usage: Client <program.py> [input.txt] [managerHost] [managerPort]");
        String managerHost = args.length > 2 ? args[2] : DEFAULT_MANAGER_HOST;
        int managerPort = args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_MANAGER_PORT;
        Client client = new Client(managerHost, managerPort);
        try {
            String id = client.submit(Path.of(args[0]), args.length > 1 ? Path.of(args[1]) : null);
            System.out.println("Pedido: " + id);
            while (true) {
                Result result = client.getResult(id);
                System.out.println("Estado: " + result.getState());
                if (result.getState() == RequestState.FINISHED
                        || result.getState() == RequestState.FAILED
                        || result.getState() == RequestState.TIMEOUT) {
                    System.out.write(result.getStdout().toByteArray());
                    System.err.write(result.getStderr().toByteArray());
                    break;
                }
                Thread.sleep(500);
            }
        } finally { client.close(); }
    }
}
