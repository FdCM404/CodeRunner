package coderunner.client;

import coderunner.proto.SubmitResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ClientStreamObserver implements StreamObserver<SubmitResponse> {

    private String id;
    private final AtomicReference<Throwable> failure;
    private final CountDownLatch done;

    public ClientStreamObserver() {
        this.failure = new AtomicReference<>();
        this.done =  new CountDownLatch(1);
    }

    @Override
    public void onNext(SubmitResponse response) {
        id = response.getId();
    }

    @Override
    public void onError(Throwable t) {
        failure.set(t); done.countDown();
    }

    @Override
    public void onCompleted() {
        done.countDown();
    }

    public String getId() {
        return id;
    }

    public AtomicReference<Throwable> getFailure() {
        return failure;
    }

    public CountDownLatch getDone() {
        return done;
    }
}
