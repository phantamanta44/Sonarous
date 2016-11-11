package io.github.phantamanta44.sonarous.player;

import io.github.phantamanta44.sonarous.util.deferred.IPromise;

public interface ISongProvider {

    IPromise<ISong> resolve(String url);
    
    boolean canResolve(String url);
    
}
