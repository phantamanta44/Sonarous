package io.github.phantamanta44.sonarous.player.search.impl;

import com.google.gson.JsonElement;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.search.ISearchProvider;
import io.github.phantamanta44.sonarous.player.search.ISearchResult;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.FFmpegUtils;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import io.github.phantamanta44.sonarous.util.io.PromiseIO;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class YoutubeSearchProvider implements ISearchProvider {

    private SongResolver resolver;

    @Override
    public String getIdentifier() {
        return "youtube";
    }

    @Override
    public boolean initialize(SongResolver resolver) {
        this.resolver = resolver;
        if (!FFmpegUtils.ffmpegExists())
            return false;
        JsonElement ytdlPath = BotMain.client().getConfigValue("providers.youtube.ytdlPath");
        return ytdlPath != null && new File(ytdlPath.getAsString()).exists();
    }

    @Override
    public IPromise<List<? extends ISearchResult>> search(String query) {
        return PromiseIO.requestHtml("https://youtube.com/results?search_query=" + query.replaceAll("\\s+", "+"))
                .map(doc -> doc.select("div.yt-lockup-video .yt-lockup-title > a").stream()
                        .filter(e -> e.parent().parent().select("span.yt-badge-ad").isEmpty())
                        .limit(5)
                        .map(YoutubeSearchResult::new)
                        .collect(Collectors.toList())
                );
    }

    public class YoutubeSearchResult implements ISearchResult {

        private final String name, url;

        public YoutubeSearchResult(Element e) {
            this.name = e.attr("title");
            this.url = "http://youtube.com" + e.attr("href");
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
