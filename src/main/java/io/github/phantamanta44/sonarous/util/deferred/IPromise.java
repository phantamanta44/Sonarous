package io.github.phantamanta44.sonarous.util.deferred;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IPromise<A> {

    IPromise<A> done(Consumer<A> callback);

    IPromise<A> fail(Consumer<Throwable> callback);

    IPromise<A> always(Consumer<A> callback);

    IPromise<A> progress(Runnable callback);

    <B> IPromise<B> map(Function<A, B> mapper);

    <B> IPromise<B> then(Function<A, IPromise<B>> mapper);

    PromiseState state();

}