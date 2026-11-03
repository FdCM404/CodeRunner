package coderunner.manager;

import coderunner.proto.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import redis.clients.jedis.JedisPooled;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ManagerServer {

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 5000;
    public static final String REDIS_HOST = System.getenv().getOrDefault("REDIS_HOST", "127.0.0.1");
    public static final String REDIS_ADVERTISE_HOST =
            System.getenv().getOrDefault("REDIS_ADVERTISE_HOST", REDIS_HOST);
    public static final int REDIS_PORT = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));

    private static void log(String message) {
        System.out.printf("[%s] [ManagerServer] %s%n", Instant.now(), message);
    }

    static final class Registry {
        private final List<ServerAddress> runners = new ArrayList<>();
        private final AtomicInteger next = new AtomicInteger();

        synchronized void register(ServerAddress address) {
            runners.removeIf(a -> a.getHost().equals(address.getHost()) && a.getPort() == address.getPort());
            runners.add(address);
            log("registry.register runner=" + address.getHost() + ":" + address.getPort()
                    + " registeredRunners=" + runners.size());
        }

        synchronized ServerAddress next() {
            if (runners.isEmpty()) throw new IllegalStateException("No RunServer is registered");
            ServerAddress selected = runners.get(Math.floorMod(next.getAndIncrement(), runners.size()));
            log("registry.next selected=" + selected.getHost() + ":" + selected.getPort()
                    + " registeredRunners=" + runners.size());
            return selected;
        }

    }

    static final class RegistrationService extends ManagerRegistrationGrpc.ManagerRegistrationImplBase {
        private final Registry registry;

        RegistrationService(Registry registry) {
            this.registry = registry;
        }

        @Override public void register(RegisterRequest request, StreamObserver<RegisterResponse> response) {
            log("gRPC register request from RunServer=" + request.getHost() + ":" + request.getPort()
                    + " -> Redis=" + REDIS_HOST + ":" + REDIS_PORT
                    + " advertisedRedis=" + REDIS_ADVERTISE_HOST + ":" + REDIS_PORT);
            ServerAddress address = ServerAddress.newBuilder().setHost(request.getHost()).setPort(request.getPort()).build();
            registry.register(address);
            response.onNext(RegisterResponse.newBuilder()
                    .setRedisHost(REDIS_ADVERTISE_HOST).setRedisPort(REDIS_PORT).build());
            response.onCompleted();
            log("gRPC register completed runner=" + request.getHost() + ":" + request.getPort());
        }
    }

    static final class DirectoryService extends ServerDirectoryGrpc.ServerDirectoryImplBase {
        private final Registry registry;
        private final JedisPooled redis;

        DirectoryService(Registry registry) {
            this.registry = registry;
            this.redis = new JedisPooled(REDIS_HOST, REDIS_PORT);
            log("Redis client configured address=" + REDIS_HOST + ":" + REDIS_PORT
                    + " ping=" + redis.ping());
        }

        @Override public void getServer(Empty request, StreamObserver<ServerAddress> response) {
            log("gRPC getServer request");
            try {
                ServerAddress selected = registry.next();
                response.onNext(selected);
                response.onCompleted();
                log("gRPC getServer response=" + selected.getHost() + ":" + selected.getPort());
            } catch (IllegalStateException ex) {
                log("gRPC getServer failed: " + ex.getMessage());
                response.onError(io.grpc.Status.UNAVAILABLE.withDescription(ex.getMessage()).asRuntimeException());
            }
        }

        @Override public void locate(RequestId request, StreamObserver<ServerAddress> response) {
            String key = "coderunner:request:" + request.getId();
            log("Redis HGET key=" + key + " field=owner");
            String value = redis.hget(key, "owner");
            if (value == null) {
                log("Redis HGET result key=" + key + " owner=<missing>");
                response.onError(io.grpc.Status.NOT_FOUND.asRuntimeException());
                return;
            }
            log("Redis HGET result key=" + key + " owner=" + value);
            String[] parts = value.split(":", 2);
            if (parts.length != 2) {
                response.onError(io.grpc.Status.INTERNAL.withDescription("Invalid request owner").asRuntimeException());
                return;
            }
            try {
                response.onNext(ServerAddress.newBuilder().setHost(parts[0])
                        .setPort(Integer.parseInt(parts[1])).build());
                response.onCompleted();
                log("gRPC locate response requestId=" + request.getId() + " owner=" + value);
            } catch (NumberFormatException ex) {
                log("gRPC locate failed requestId=" + request.getId() + " owner=" + value);
                response.onError(io.grpc.Status.INTERNAL.withDescription("Invalid request owner port")
                        .withCause(ex).asRuntimeException());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        Registry registry = new Registry();
        Server server = ServerBuilder.forPort(port)
                .addService(new RegistrationService(registry))
                .addService(new DirectoryService(registry))
                .build().start();
        log("listening address=0.0.0.0:" + port + " Redis=" + REDIS_HOST + ":" + REDIS_PORT
                + " advertisedRedis=" + REDIS_ADVERTISE_HOST + ":" + REDIS_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}
