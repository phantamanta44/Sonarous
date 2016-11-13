package io.github.phantamanta44.sonarous.util;

import com.google.gson.JsonElement;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.concurrent.ChildProcess;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;

import java.io.File;

public class FFmpegUtils {

    private static File ffmpegExec;

    public static IPromise<byte[]> audioFrom(byte[] input) {
        if (!ffmpegExists())
            throw new UnsupportedOperationException();
        return ChildProcess.runWithStdin(
                new ProcessBuilder(ffmpegExec.getAbsolutePath(), "-y", "-v", "-8", "-i", "pipe:0", "-f", "mp3", "-"),
                input
        );
    }

    public static boolean ffmpegExists() {
        if (ffmpegExec == null) {
            JsonElement ffmpegPath = BotMain.client().getConfigValue("ffmpeg.ffmpegPath");
            if (ffmpegPath == null)
                return false;
            ffmpegExec = new File(ffmpegPath.getAsString());
            if (!ffmpegExec.exists()) {
                ffmpegExec = null;
                return false;
            }
        }
        return true;
    }

}
