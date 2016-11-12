package io.github.phantamanta44.sonarous.util;

import com.google.gson.JsonElement;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.File;
import java.io.IOException;

public class FFmpegUtils {

    private static FFmpeg ffmpeg;
    private static FFprobe ffprobe;
    private static FFmpegExecutor exec;

    public static IPromise<Void> extractAudio(File in, File out) {
        if (!ffmpegExists())
            throw new UnsupportedOperationException();
        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in.getAbsolutePath())
                .addOutput(out.getAbsolutePath())
                .setFormat("mp3")
                .done();
         return Deferreds.call(() -> exec.createJob(builder).run()).promise();
    }

    public static boolean ffmpegExists() {
        if (ffmpeg == null || ffprobe == null) {
            JsonElement ffmpegPath = BotMain.client().getConfigValue("ffmpeg.ffmpegPath");
            JsonElement ffprobePath = BotMain.client().getConfigValue("ffmpeg.ffprobePath");
            if (ffmpegPath == null || ffprobePath == null)
                return false;
            try {
                ffmpeg = new FFmpeg(ffmpegPath.getAsString());
                ffprobe = new FFprobe(ffprobePath.getAsString());
                exec = new FFmpegExecutor(ffmpeg, ffprobe);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
