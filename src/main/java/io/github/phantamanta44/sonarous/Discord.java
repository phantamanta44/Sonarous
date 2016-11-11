package io.github.phantamanta44.sonarous;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.phantamanta44.sonarous.util.concurrent.ThreadPoolFactory;
import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class Discord {

    private static final File CFG_FILE = new File("sonarous_cfg.json");

    private final ScheduledExecutorService threadPool;
    private final Map<String, JsonElement> config;

    private IDiscordClient api;

    Discord() {
        this.threadPool = new ThreadPoolFactory()
                .withPool(ThreadPoolFactory.PoolType.SCHEDULED)
                .withQueue(ThreadPoolFactory.QueueType.CACHED)
                .construct();
        this.config = new HashMap<>();
    }

    public IPromise<ReadyEvent> init() {
        Deferred<ReadyEvent> def = new Deferred<>();
        try {
            api = new ClientBuilder().withToken(getConfigValue("token").getAsString()).build();
            api.getDispatcher().registerTemporaryListener((ReadyEvent event) -> def.resolve(event));
            api.login(false);
        } catch (Throwable e) {
            def.reject(e);
        }
        return def.promise();
    }

    public IDiscordClient api() {
        return api;
    }

    public ScheduledExecutorService executorPool() {
        return threadPool;
    }

    public JsonElement getConfigValue(String key) {
        return config.get(key);
    }

    boolean readConfig() {
        JsonParser parser = new JsonParser();
        try (FileReader in = new FileReader(CFG_FILE)) {
            parseConfigTree(parser.parse(in).getAsJsonObject(), "");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void parseConfigTree(JsonObject tree, String prefix) {
        tree.entrySet().forEach(e -> {
            config.put(prefix + e.getKey(), e.getValue());
            if (e.getValue().isJsonObject())
                parseConfigTree(e.getValue().getAsJsonObject(), prefix + e.getKey() + ".");
        });
    }

}
