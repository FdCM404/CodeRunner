package coderunner.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class ServerDirectoryGrpc {

  private ServerDirectoryGrpc() {}

  public static final java.lang.String SERVICE_NAME = "coderunner.ServerDirectory";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<coderunner.proto.Empty,
      coderunner.proto.ServerAddress> getGetServerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getServer",
      requestType = coderunner.proto.Empty.class,
      responseType = coderunner.proto.ServerAddress.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<coderunner.proto.Empty,
      coderunner.proto.ServerAddress> getGetServerMethod() {
    io.grpc.MethodDescriptor<coderunner.proto.Empty, coderunner.proto.ServerAddress> getGetServerMethod;
    if ((getGetServerMethod = ServerDirectoryGrpc.getGetServerMethod) == null) {
      synchronized (ServerDirectoryGrpc.class) {
        if ((getGetServerMethod = ServerDirectoryGrpc.getGetServerMethod) == null) {
          ServerDirectoryGrpc.getGetServerMethod = getGetServerMethod =
              io.grpc.MethodDescriptor.<coderunner.proto.Empty, coderunner.proto.ServerAddress>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getServer"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.ServerAddress.getDefaultInstance()))
              .setSchemaDescriptor(new ServerDirectoryMethodDescriptorSupplier("getServer"))
              .build();
        }
      }
    }
    return getGetServerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<coderunner.proto.RequestId,
      coderunner.proto.ServerAddress> getLocateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "locate",
      requestType = coderunner.proto.RequestId.class,
      responseType = coderunner.proto.ServerAddress.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<coderunner.proto.RequestId,
      coderunner.proto.ServerAddress> getLocateMethod() {
    io.grpc.MethodDescriptor<coderunner.proto.RequestId, coderunner.proto.ServerAddress> getLocateMethod;
    if ((getLocateMethod = ServerDirectoryGrpc.getLocateMethod) == null) {
      synchronized (ServerDirectoryGrpc.class) {
        if ((getLocateMethod = ServerDirectoryGrpc.getLocateMethod) == null) {
          ServerDirectoryGrpc.getLocateMethod = getLocateMethod =
              io.grpc.MethodDescriptor.<coderunner.proto.RequestId, coderunner.proto.ServerAddress>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "locate"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.RequestId.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.ServerAddress.getDefaultInstance()))
              .setSchemaDescriptor(new ServerDirectoryMethodDescriptorSupplier("locate"))
              .build();
        }
      }
    }
    return getLocateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ServerDirectoryStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryStub>() {
        @java.lang.Override
        public ServerDirectoryStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerDirectoryStub(channel, callOptions);
        }
      };
    return ServerDirectoryStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static ServerDirectoryBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryBlockingV2Stub>() {
        @java.lang.Override
        public ServerDirectoryBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerDirectoryBlockingV2Stub(channel, callOptions);
        }
      };
    return ServerDirectoryBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ServerDirectoryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryBlockingStub>() {
        @java.lang.Override
        public ServerDirectoryBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerDirectoryBlockingStub(channel, callOptions);
        }
      };
    return ServerDirectoryBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ServerDirectoryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerDirectoryFutureStub>() {
        @java.lang.Override
        public ServerDirectoryFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerDirectoryFutureStub(channel, callOptions);
        }
      };
    return ServerDirectoryFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * b - Obter Servidor
     * </pre>
     */
    default void getServer(coderunner.proto.Empty request,
        io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetServerMethod(), responseObserver);
    }

    /**
     */
    default void locate(coderunner.proto.RequestId request,
        io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLocateMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ServerDirectory.
   */
  public static abstract class ServerDirectoryImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ServerDirectoryGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ServerDirectory.
   */
  public static final class ServerDirectoryStub
      extends io.grpc.stub.AbstractAsyncStub<ServerDirectoryStub> {
    private ServerDirectoryStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerDirectoryStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerDirectoryStub(channel, callOptions);
    }

    /**
     * <pre>
     * b - Obter Servidor
     * </pre>
     */
    public void getServer(coderunner.proto.Empty request,
        io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetServerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void locate(coderunner.proto.RequestId request,
        io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLocateMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ServerDirectory.
   */
  public static final class ServerDirectoryBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<ServerDirectoryBlockingV2Stub> {
    private ServerDirectoryBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerDirectoryBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerDirectoryBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * b - Obter Servidor
     * </pre>
     */
    public coderunner.proto.ServerAddress getServer(coderunner.proto.Empty request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetServerMethod(), getCallOptions(), request);
    }

    /**
     */
    public coderunner.proto.ServerAddress locate(coderunner.proto.RequestId request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getLocateMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service ServerDirectory.
   */
  public static final class ServerDirectoryBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ServerDirectoryBlockingStub> {
    private ServerDirectoryBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerDirectoryBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerDirectoryBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * b - Obter Servidor
     * </pre>
     */
    public coderunner.proto.ServerAddress getServer(coderunner.proto.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetServerMethod(), getCallOptions(), request);
    }

    /**
     */
    public coderunner.proto.ServerAddress locate(coderunner.proto.RequestId request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLocateMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ServerDirectory.
   */
  public static final class ServerDirectoryFutureStub
      extends io.grpc.stub.AbstractFutureStub<ServerDirectoryFutureStub> {
    private ServerDirectoryFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerDirectoryFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerDirectoryFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * b - Obter Servidor
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<coderunner.proto.ServerAddress> getServer(
        coderunner.proto.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetServerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<coderunner.proto.ServerAddress> locate(
        coderunner.proto.RequestId request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLocateMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SERVER = 0;
  private static final int METHODID_LOCATE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SERVER:
          serviceImpl.getServer((coderunner.proto.Empty) request,
              (io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress>) responseObserver);
          break;
        case METHODID_LOCATE:
          serviceImpl.locate((coderunner.proto.RequestId) request,
              (io.grpc.stub.StreamObserver<coderunner.proto.ServerAddress>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetServerMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              coderunner.proto.Empty,
              coderunner.proto.ServerAddress>(
                service, METHODID_GET_SERVER)))
        .addMethod(
          getLocateMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              coderunner.proto.RequestId,
              coderunner.proto.ServerAddress>(
                service, METHODID_LOCATE)))
        .build();
  }

  private static abstract class ServerDirectoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ServerDirectoryBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return coderunner.proto.Coderunner.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ServerDirectory");
    }
  }

  private static final class ServerDirectoryFileDescriptorSupplier
      extends ServerDirectoryBaseDescriptorSupplier {
    ServerDirectoryFileDescriptorSupplier() {}
  }

  private static final class ServerDirectoryMethodDescriptorSupplier
      extends ServerDirectoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ServerDirectoryMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ServerDirectoryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ServerDirectoryFileDescriptorSupplier())
              .addMethod(getGetServerMethod())
              .addMethod(getLocateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
