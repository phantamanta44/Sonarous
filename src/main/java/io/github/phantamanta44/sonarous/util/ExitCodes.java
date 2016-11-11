package io.github.phantamanta44.sonarous.util;

public class ExitCodes {

    public static final int SUCCESS = 0, ERROR = 1, REBOOT = 32, UPDATE = 33, LOGIN_FAIL = 34, CONNECT_FAIL = 35;

    public static void exit(int code) {
        Runtime.getRuntime().exit(code);
    }

}
