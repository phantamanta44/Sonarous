package io.github.phantamanta44.sonarous.player.search.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.search.ISearchProvider;
import io.github.phantamanta44.sonarous.player.search.ISearchResult;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import io.github.phantamanta44.sonarous.util.io.PromiseIO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SoundcloudSearchProvider implements ISearchProvider {

    private static final String SEARCH_URL = "https://api.soundcloud.com/tracks?q=%s&filter=public&limit=5&client_id=%s";

    private String apiKey;
    private SongResolver resolver;

    @Override
    public String getIdentifier() {
        return "soundcloud";
    }

    @Override
    public boolean initialize(SongResolver resolver) {
        this.resolver = resolver;
        JsonElement keyElem = BotMain.client().getConfigValue("providers.soundcloud.apiKey");
        if (keyElem == null)
            return false;
        this.apiKey = keyElem.getAsString();
        return true;
    }

    @Override
    public IPromise<List<? extends ISearchResult>> search(String query) {
        return PromiseIO.requestJson(searchUrl(query)).map(resp -> parseResults(resp.getAsJsonArray()));
    }

    private List<? extends ISearchResult> parseResults(JsonArray resp) {
        return StreamSupport.stream(resp.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(dto -> new SCSearchResult(dto.get("title").getAsString(), dto.get("permalink_url").getAsString(), resolver))
                .collect(Collectors.toList());
    }

    private String searchUrl(String query) {
        try {
            return String.format(SEARCH_URL, URLEncoder.encode(query, "UTF-8"), apiKey);
        } catch (UnsupportedEncodingException e) {
            return String.format(SEARCH_URL, query, apiKey);
        }
    }

    private static class SCSearchResult implements ISearchResult {

        private final String name, url;
        private final SongResolver resolver;

        public SCSearchResult(String name, String url, SongResolver resolver) {
            this.name = name;
            this.url = url;
            this.resolver = resolver;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public IPromise<? extends ISong> resolve() {
            return resolver.resolve(url);
        }

    }

}
