package coderunner.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 *ManagerServer
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class ManagerRegistrationGrpc {

  private ManagerRegistrationGrpc() {}

  public static final java.lang.String SERVICE_NAME = "coderunner.ManagerRegistration";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<coderunner.proto.RegisterRequest,
      coderunner.proto.RegisterResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "register",
      requestType = coderunner.proto.RegisterRequest.class,
      responseType = coderunner.proto.RegisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<coderunner.proto.RegisterRequest,
      coderunner.proto.RegisterResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<coderunner.proto.RegisterRequest, coderunner.proto.RegisterResponse> getRegisterMethod;
    if ((getRegisterMethod = ManagerRegistrationGrpc.getRegisterMethod) == null) {
      synchronized (ManagerRegistrationGrpc.class) {
        if ((getRegisterMethod = ManagerRegistrationGrpc.getRegisterMethod) == null) {
          ManagerRegistrationGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<coderunner.proto.RegisterRequest, coderunner.proto.RegisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  coderunner.proto.RegisterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ManagerRegistrationMethodDescriptorSupplier("register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ManagerRegistrationStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationStub>() {
        @java.lang.Override
        public ManagerRegistrationStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManagerRegistrationStub(channel, callOptions);
        }
      };
    return ManagerRegistrationStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static ManagerRegistrationBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationBlockingV2Stub>() {
        @java.lang.Override
        public ManagerRegistrationBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManagerRegistrationBlockingV2Stub(channel, callOptions);
        }
      };
    return ManagerRegistrationBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ManagerRegistrationBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationBlockingStub>() {
        @java.lang.Override
        public ManagerRegistrationBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManagerRegistrationBlockingStub(channel, callOptions);
        }
      };
    return ManagerRegistrationBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ManagerRegistrationFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ManagerRegistrationFutureStub>() {
        @java.lang.Override
        public ManagerRegistrationFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ManagerRegistrationFutureStub(channel, callOptions);
        }
      };
    return ManagerRegistrationFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   *ManagerServer
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * a - Registo
     * </pre>
     */
    default void register(coderunner.proto.RegisterRequest request,
        io.grpc.stub.StreamObserver<coderunner.proto.RegisterResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ManagerRegistration.
   * <pre>
   *ManagerServer
   * </pre>
   */
  public static abstract class ManagerRegistrationImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ManagerRegistrationGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ManagerRegistration.
   * <pre>
   *ManagerServer
   * </pre>
   */
  public static final class ManagerRegistrationStub
      extends io.grpc.stub.AbstractAsyncStub<ManagerRegistrationStub> {
    private ManagerRegistrationStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ManagerRegistrationStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManagerRegistrationStub(channel, callOptions);
    }

    /**
     * <pre>
     * a - Registo
     * </pre>
     */
    public void register(coderunner.proto.RegisterRequest request,
        io.grpc.stub.StreamObserver<coderunner.proto.RegisterResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ManagerRegistration.
   * <pre>
   *ManagerServer
   * </pre>
   */
  public static final class ManagerRegistrationBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<ManagerRegistrationBlockingV2Stub> {
    private ManagerRegistrationBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ManagerRegistrationBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManagerRegistrationBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * a - Registo
     * </pre>
     */
    public coderunner.proto.RegisterResponse register(coderunner.proto.RegisterRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service ManagerRegistration.
   * <pre>
   *ManagerServer
   * </pre>
   */
  public static final class ManagerRegistrationBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ManagerRegistrationBlockingStub> {
    private ManagerRegistrationBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ManagerRegistrationBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManagerRegistrationBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * a - Registo
     * </pre>
     */
    public coderunner.proto.RegisterResponse register(coderunner.proto.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ManagerRegistration.
   * <pre>
   *ManagerServer
   * </pre>
   */
  public static final class ManagerRegistrationFutureStub
      extends io.grpc.stub.AbstractFutureStub<ManagerRegistrationFutureStub> {
    private ManagerRegistrationFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ManagerRegistrationFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ManagerRegistrationFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * a - Registo
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<coderunner.proto.RegisterResponse> register(
        coderunner.proto.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;

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
        case METHODID_REGISTER:
          serviceImpl.register((coderunner.proto.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<coderunner.proto.RegisterResponse>) responseObserver);
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
          getRegisterMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              coderunner.proto.RegisterRequest,
              coderunner.proto.RegisterResponse>(
                service, METHODID_REGISTER)))
        .build();
  }

  private static abstract class ManagerRegistrationBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ManagerRegistrationBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return coderunner.proto.Coderunner.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ManagerRegistration");
    }
  }

  private static final class ManagerRegistrationFileDescriptorSupplier
      extends ManagerRegistrationBaseDescriptorSupplier {
    ManagerRegistrationFileDescriptorSupplier() {}
  }

  private static final class ManagerRegistrationMethodDescriptorSupplier
      extends ManagerRegistrationBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ManagerRegistrationMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (ManagerRegistrationGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ManagerRegistrationFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
