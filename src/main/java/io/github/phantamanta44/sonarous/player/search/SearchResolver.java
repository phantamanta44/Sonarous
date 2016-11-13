package io.github.phantamanta44.sonarous.player.search;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.nomreflect.Reflect;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.Lambdas;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchResolver {

    private Map<String, ISearchProvider> providers;

    public SearchResolver(String pkg, SongResolver resolver) {
        try {
            providers = Reflect.types(pkg)
                    .extending(ISearchProvider.class)
                    .find().stream()
                    .filter(c -> !c.isInterface())
                    .map(Throwing.function(type -> (ISearchProvider)type.newInstance()))
                    .filter(p -> p.initialize(resolver))
                    .collect(Collectors.toMap(ISearchProvider::getIdentifier, Lambdas.identity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IPromise<List<? extends ISearchResult>> resolve(String providerId, String query) {
        ISearchProvider provider = providers.get(providerId.toLowerCase());
        if (provider != null) {
            BotMain.log().info("RSE: {} / {}", provider.getIdentifier(), query);
            return provider.search(query);
        }
        throw new UnsupportedOperationException();
    }

    public Stream<ISearchProvider> providers() {
        return providers.values().stream();
    }
    
}
