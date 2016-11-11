package io.github.phantamanta44.sonarous.util.deferred;


import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import io.github.phantamanta44.sonarous.BotMain;

public class Deferreds {

    public static Deferred<Void> call(Runnable func) {
        return call(func, BotMain.client().executorPool());
    }

    public static <A> Deferred<A> call(Supplier<A> func) {
        return call(func, BotMain.client().executorPool());
    }

    public static Deferred<Void> call(Runnable func, ExecutorService execServ) {
        Deferred<Void> def = new Deferred<>();
        execServ.submit(() -> {
            try {
                func.run();
                def.resolve(null);
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def;
    }

    public static <A> Deferred<A> call(Supplier<A> func, ExecutorService execServ) {
        Deferred<A> def = new Deferred<>();
        execServ.submit(() -> {
            try {
                def.resolve(func.get());
            } catch (Exception e) {
                def.reject(e);
            }
        });
        return def;
    }

}