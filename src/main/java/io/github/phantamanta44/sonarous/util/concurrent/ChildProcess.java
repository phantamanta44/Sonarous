package io.github.phantamanta44.sonarous.util.concurrent;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.sonarous.BotMain;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ChildProcess {

    public static IPromise<byte[]> runWithStdin(ProcessBuilder builder, byte[] stdin) {
        return Deferreds.call(Throwing.supplier(() -> {
            BotMain.log().info("EXE: {} / {}", builder.hashCode(), builder.command().stream().collect(Collectors.joining(" ")));
            builder.redirectErrorStream(true);
            Process p = builder.start();
            Future<?> stdinThread = BotMain.client().executorPool().submit(Throwing.runnable(() -> {
                try (BufferedOutputStream stdinStream = new BufferedOutputStream(p.getOutputStream())) {
                    stdinStream.write(stdin);
                    stdinStream.flush();
                    stdinStream.close();
                }
            }));
            byte[] stdout = IOUtils.toByteArray(p.getInputStream());
            stdinThread.cancel(true);
            BotMain.log().info("EXE: done: {} (exit {})", builder.hashCode(), p.exitValue());
            if (p.exitValue() != 0)
                throw new ExitCodeException(p.exitValue());
            return stdout;
        })).promise();
    }

    public static IPromise<byte[]> runWithStdin(ProcessBuilder builder, String stdin) {
        return runWithStdin(builder, stdin.getBytes());
    }

    public static IPromise<String> runWithStdinParseStr(ProcessBuilder builder, byte[] stdin) {
        return runWithStdin(builder, stdin).map(String::new);
    }

    public static IPromise<String> runWithStdinParseStr(ProcessBuilder builder, String stdin) {
        return runWithStdin(builder, stdin).map(String::new);
    }

    public static IPromise<byte[]> run(ProcessBuilder builder) {
        return runWithStdin(builder, new byte[0]);
    }

    public static IPromise<String> runParseStr(ProcessBuilder builder) {
        return runWithStdinParseStr(builder, new byte[0]).map(String::new);
    }

}
