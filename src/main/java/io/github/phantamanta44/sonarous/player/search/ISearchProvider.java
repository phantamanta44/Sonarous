package io.github.phantamanta44.sonarous.player.search;

import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

import java.util.List;

public interface ISearchProvider {

    String getIdentifier();

    boolean initialize(SongResolver resolver);

    IPromise<List<? extends ISearchResult>> search(String query);

}
