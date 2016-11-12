package io.github.phantamanta44.sonarous.util.concurrent;

public class ExitCodeException extends RuntimeException {

    private final int exitCode;

    public ExitCodeException(int exitCode) {
        super(String.format("Process failed with exit code %d.", exitCode));
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

}
