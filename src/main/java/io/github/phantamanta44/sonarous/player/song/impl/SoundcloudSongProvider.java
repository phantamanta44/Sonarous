package io.github.phantamanta44.sonarous.player.song.impl;

import com.github.fge.lambdas.Throwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.player.song.ISongProvider;
import io.github.phantamanta44.sonarous.util.SafeJsonWrapper;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import io.github.phantamanta44.sonarous.util.io.PromiseIO;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.api.internal.DiscordUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.net.URI;
import java.util.regex.Pattern;

public class SoundcloudSongProvider implements ISongProvider {

    private static final Pattern URL_PAT = Pattern.compile("^https?://.*soundcloud\\.com/.+$", Pattern.CASE_INSENSITIVE);
    private static final String RESOLVE_URL = "https://api.soundcloud.com/resolve?url=%s&client_id=%s";

    private String apiKey;

    @Override
    public boolean initialize() {
        JsonElement keyElem = BotMain.client().getConfigValue("providers.soundcloud.apiKey");
        if (keyElem == null)
            return false;
        this.apiKey = keyElem.getAsString();
        return true;
    }

    @Override
    public IPromise<SoundcloudSong> resolve(String url) {
        return PromiseIO.requestJson(resolveUrl(url)).map(JsonElement::getAsJsonObject).then(this::parseSoundcloud);
    }

    @Override
    public boolean canResolve(String url) {
        return URL_PAT.matcher(url).matches();
    }

    @Override
    public String getName() {
        return "SoundCloud";
    }

    private IPromise<SoundcloudSong> parseSoundcloud(JsonObject dto) {
        return Deferreds.call(Throwing.supplier(() -> {
            if (!dto.get("kind").getAsString().equalsIgnoreCase("track"))
                throw new IllegalArgumentException("Provided URL isn't a track!");
            String streamUrl = dto.get("stream_url").getAsString() + "?client_id=" + apiKey;
            try (AudioInputStream in = AudioSystem.getAudioInputStream(URI.create(streamUrl).toURL())) {
                try (AudioInputStream converted = DiscordUtils.getPCMStream(in)) {
                    byte[] audio = IOUtils.toByteArray(converted);
                    System.out.println(audio.length);
                    return new SoundcloudSong(dto, audio, this, converted.getFormat());
                }
            }
        })).promise();
    }

    private String resolveUrl(String url) {
        return String.format(RESOLVE_URL, url, apiKey);
    }

    private static class SoundcloudSong implements ISong {

        private final String name, author, url, artUrl;
        private final byte[] audio;
        private final ISongProvider provider;
        private final int frameSize;

        public SoundcloudSong(JsonObject dto, byte[] audio, ISongProvider provider, AudioFormat format) {
            SafeJsonWrapper wrapper = new SafeJsonWrapper(dto);
            this.name = wrapper.getString("title");
            this.author = wrapper.getJsonObject("user").getString("full_name");
            this.url = wrapper.getString("permalink_url");
            this.artUrl = wrapper.getString("artwork_url").replaceAll("large.jpg", "t500x500.jpg");
            this.audio = audio;
            this.provider = provider;
            this.frameSize = format.getFrameSize();
        }

        @Override
        public ISongProvider getProvider() {
            return provider;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getAuthor() {
            return author;
        }

        @Override
        public String getAlbum() {
            return null;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getArtUrl() {
            return artUrl;
        }

        @Override
        public byte[] getAudio() {
            return audio;
        }

        @Override
        public int getFrameSize() {
            return frameSize;
        }

    }

}
