package io.github.phantamanta44.sonarous.util.concurrent;

import com.github.fge.lambdas.Throwing;
import io.github.phantamanta44.sonarous.util.deferred.Deferreds;
import io.github.phantamanta44.sonarous.util.deferred.IPromise;
import org.apache.commons.io.IOUtils;

public class ChildProcess {

    public static IPromise<String> run(ProcessBuilder builder) {
        return Deferreds.call(Throwing.supplier(() -> {
            Process p = builder.start();
            p.waitFor();
            if (p.exitValue() != 0)
                throw new ExitCodeException(p.exitValue());
            return new String(IOUtils.toByteArray(p.getInputStream()));
        })).promise();
    }

}
