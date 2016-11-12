package io.github.phantamanta44.sonarous.player.song;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.nomreflect.Reflect;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SongResolver {

    private List<ISongProvider> providers;
    
    public SongResolver(String pkg) {
        try {
            providers = Reflect.types(pkg)
                    .extending(ISongProvider.class)
                    .find().stream()
                    .filter(c -> !c.isInterface())
                    .map(Throwing.function(type -> (ISongProvider)type.newInstance()))
                    .filter(ISongProvider::initialize)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public IPromise<? extends ISong> resolve(String url) {
        Optional<ISongProvider> provider = providers.stream().filter(p -> p.canResolve(url)).findAny();
        if (provider.isPresent())
            return provider.get().resolve(url);
        throw new UnsupportedOperationException();
    }

    public Stream<ISongProvider> providers() {
        return providers.stream();
    }
    
}
