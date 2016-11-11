package io.github.phantamanta44.sonarous.player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.fge.lambdas.Throwing;

import io.github.phantamanta44.nomreflect.Reflect;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

public class SongResolver {

    private final List<ISongProvider> providers;
    
    public SongResolver() {
        providers = Reflect.types("io.github.phantamanta44.sonarous.player.impl")
                .extending(ISongProvider.class)
                .find().stream()
                .map(Throwing.function(type -> (ISongProvider)type.newInstance()))
                .collect(Collectors.toList());
    }
    
    public IPromise<ISong> resolve(String url) {
        Optional<ISongProvider> provider = providers.stream().filter(p -> p.canResolve(url)).findAny();
        if (provider.isPresent())
            return provider.get().resolve(url);
        throw new UnsupportedOperationException();
    }
    
}
