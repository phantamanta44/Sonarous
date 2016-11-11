package io.github.phantamanta44.sonarous.util.concurrent;

import io.github.phantamanta44.sonarous.util.Lambdas;

public class NullaryEventStream implements Runnable {

    private Runnable handler = Lambdas.noopNullary();

    public NullaryEventStream addHandler(Runnable handler) {
        this.handler = Lambdas.compose(this.handler, handler);
        return this;
    }

    @Override
    public void run() {
        handler.run();
    }

}