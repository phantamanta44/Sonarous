package io.github.phantamanta44.sonarous.util.deferred;

import io.github.phantamanta44.sonarous.util.Lambdas;
import io.github.phantamanta44.sonarous.util.concurrent.NullaryEventStream;
import io.github.phantamanta44.sonarous.util.concurrent.UnaryEventStream;

import java.util.function.Consumer;
import java.util.function.Function;

public class Deferred<A> {

    private A result;
    private Throwable exception;
    private PromiseState state;
    
    private NullaryEventStream onProgress;
    private UnaryEventStream<A> onResolve;
    private UnaryEventStream<Throwable> onReject;

    public Deferred() {
        onProgress = new NullaryEventStream();
        onResolve = new UnaryEventStream<A>();
        onReject = new UnaryEventStream<>();
        state = PromiseState.PENDING;
    }

    public void reject(Throwable e) {
        this.exception = e;
        state = PromiseState.REJECTED;
        onReject.accept(e);
    }

    public void notifyProgress() {
        onProgress.run();
    }

    public void resolve(A result) {
        this.result = result;
        state = PromiseState.RESOLVED;
        onResolve.accept(result);
    }

    public IPromise<A> promise() {
        return new IPromise<A>() {
            @Override
            public IPromise<A> done(Consumer<A> callback) {
                if (state() == PromiseState.PENDING)
                    onResolve.addHandler(callback);
                else if (state() == PromiseState.RESOLVED)
                    callback.accept(result);
                return this;
            }

            @Override
            public IPromise<A> fail(Consumer<Throwable> callback) {
                if (state() == PromiseState.PENDING)
                    onReject.addHandler(callback);
                else if (state() == PromiseState.REJECTED)
                    callback.accept(exception);
                return this;
            }

            @Override
            public IPromise<A> always(Consumer<A> callback) {
                if (state() == PromiseState.PENDING) {
                    onResolve.addHandler(callback);
                    onReject.addHandler(e -> callback.accept(null));
                }
                else
                    callback.accept(result);
                return this;
            }

            @Override
            public IPromise<A> progress(Runnable callback) {
                onProgress.addHandler(callback);
                return this;
            }

            @Override
            public <B> IPromise<B> map(Function<A, B> mapper) {
                Deferred<B> def = new Deferred<>();
                done(r -> def.resolve(mapper.apply(r)));
                fail(def::reject);
                progress(def::notifyProgress);
                return def.promise();
            }

            @Override
            public PromiseState state() {
                return state;
            }
        };
    }

}