package io.github.phantamanta44.sonarous.player.impl;

import io.github.phantamanta44.sonarous.player.ISong;
import io.github.phantamanta44.sonarous.player.ISongProvider;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

public class SoundcloudProvider implements ISongProvider {

    @Override
    public IPromise<ISong> resolve(String url) {
        // TODO Implement
        return null;
    }

    @Override
    public boolean canResolve(String url) {
        // TODO Implement
        return false;
    }

}
