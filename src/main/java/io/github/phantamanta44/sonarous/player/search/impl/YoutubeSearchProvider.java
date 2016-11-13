package io.github.phantamanta44.sonarous.player.search.impl;

import com.github.fge.lambdas.Throwing;
import com.google.gson.JsonElement;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.search.ISearchProvider;
import io.github.phantamanta44.sonarous.player.search.ISearchResult;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.player.song.SongResolver;
import io.github.phantamanta44.sonarous.util.FFmpegUtils;
import io.github.phantamanta44.sonarous.util.deferred.Deferred;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import io.github.phantamanta44.sonarous.util.io.PromiseIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Deferred<List<? extends ISearchResult>> def = new Deferred<>();
        BotMain.client().executorPool().submit(() -> {
            List<YoutubeSearchResult> results = new ArrayList<>();
            try {
                Document doc = Jsoup.connect("https://youtube.com/results?search_query=" + query.replaceAll("\\s+", "+")).get();
                Elements sel = doc.select("div.yt-lockup-video .yt-lockup-title > a");
                for (int i = 0; i < 5 && i < sel.size(); i++)
                    results.add(new YoutubeSearchResult(sel.get(i)));
            } catch (Exception e) {
                def.reject(e);
            }
            def.resolve(results);
        });
        return def.promise();
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
