package io.github.phantamanta44.sonarous.util.concurrent;


import io.github.phantamanta44.sonarous.util.Lambdas;

import java.util.function.Consumer;

public class UnaryEventStream<T> implements Consumer<T> {

    private Consumer<T> handler = Lambdas.noopUnary();
    
    public UnaryEventStream<T> addHandler(Consumer<T> handler) {
        this.handler = this.handler.andThen(handler);
        return this;
    }
    
    @Override
    public void accept(T event) {
        handler.accept(event);
    }

}