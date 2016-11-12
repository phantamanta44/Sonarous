package io.github.phantamanta44.sonarous.player.search;

import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

public interface ISearchResult {

    String getName();

    IPromise<? extends ISong> resolve();

}
