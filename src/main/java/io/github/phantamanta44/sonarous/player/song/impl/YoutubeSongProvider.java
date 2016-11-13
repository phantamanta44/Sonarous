package io.github.phantamanta44.sonarous.player.song.impl;

import com.github.fge.lambdas.Throwing;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.player.song.ISong;
import io.github.phantamanta44.sonarous.player.song.ISongProvider;
import io.github.phantamanta44.sonarous.util.FFmpegUtils;
import io.github.phantamanta44.sonarous.util.concurrent.ChildProcess;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.api.internal.DiscordUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

public class YoutubeSongProvider implements ISongProvider {

    private static final Pattern URL_PAT = Pattern.compile("^https?://(?:(?:www\\.)?(?:m\\.)?youtube\\.com|youtu\\.be)/.+$", Pattern.CASE_INSENSITIVE);

    private File ytdlExec;

    @Override
    public boolean initialize() {
        if (!FFmpegUtils.ffmpegExists())
            return false;
        JsonElement ytdlPath = BotMain.client().getConfigValue("providers.youtube.ytdlPath");
        if (ytdlPath == null)
            return false;
        ytdlExec = new File(ytdlPath.getAsString());
        return ytdlExec.exists();
    }

    @Override
    public IPromise<? extends ISong> resolve(String url) {
        final String ytUrl = url.replaceAll("youtu\\.be/", "youtube.com/watch?v=");
        String fName = String.format("%s-%d.temp", ytUrl.replaceAll("\\S", ""), System.currentTimeMillis());
        File videoFile = new File(fName + ".v"), audioFile = new File(fName + ".a");
        return ChildProcess.run(spawnInfoProcess(ytUrl))
                .then(info -> ChildProcess.run(spawnDownloadProcess(ytUrl, videoFile))
                .then(ignored -> resolveVideo(videoFile, audioFile, new YoutubeInfo(info))));
    }

    @Override
    public boolean canResolve(String url) {
        return URL_PAT.matcher(url).matches();
    }

    @Override
    public String getName() {
        return "YouTube";
    }

    private ProcessBuilder spawnDownloadProcess(String url, File output) {
        return new ProcessBuilder(ytdlExec.getAbsolutePath(), "-o", output.getAbsolutePath(), "-s", url);
    }

    private ProcessBuilder spawnInfoProcess(String url) {
        return new ProcessBuilder(ytdlExec.getAbsolutePath(), "-j", "-s", url);
    }

    private IPromise<YoutubeSong> resolveVideo(File in, File out, YoutubeInfo info) {
        return FFmpegUtils.extractAudio(in, out)
                .then(ignored -> Deferreds.call(Throwing.supplier(() -> {
                        try (FileInputStream rawStream = new FileInputStream(out)) {
                            AudioInputStream raw = AudioSystem.getAudioInputStream(rawStream);
                            AudioInputStream converted = DiscordUtils.getPCMStream(raw);
                            byte[] bytes = IOUtils.toByteArray(converted);
                            return new YoutubeSong(info, bytes, converted.getFormat().getFrameSize(), this);
                        }
                })).promise())
                .always(ignored -> {
                    in.delete();
                    out.delete();
                });
    }

    private static class YoutubeInfo {

        public final String videoName, channelName, videoUrl;

        public YoutubeInfo(String raw) {
            JsonObject dto = new JsonParser().parse(raw).getAsJsonObject();
            this.videoName = dto.get("title").getAsString();
            this.channelName = dto.get("author").getAsString();
            this.videoUrl = "https://youtu.be/" + dto.get("id").getAsString();
        }

    }

    private static class YoutubeSong implements ISong {

        private final YoutubeInfo info;
        private final byte[] data;
        private final int frameSize;
        private final ISongProvider provider;

        public <B> YoutubeSong(YoutubeInfo info, byte[] data, int frameSize, ISongProvider provider) {
            this.info = info;
            this.data = data;
            this.frameSize = frameSize;
            this.provider = provider;
        }

        @Override
        public ISongProvider getProvider() {
            return provider;
        }

        @Override
        public String getName() {
            return info.videoName;
        }

        @Override
        public String getAuthor() {
            return info.channelName;
        }

        @Override
        public String getAlbum() {
            return null;
        }

        @Override
        public String getUrl() {
            return info.videoUrl;
        }

        @Override
        public String getArtUrl() {
            return null;
        }

        @Override
        public byte[] getAudio() {
            return data;
        }

        @Override
        public int getFrameSize() {
            return frameSize;
        }

    }

}
