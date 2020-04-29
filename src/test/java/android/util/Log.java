package android.util;

/**
 * Log class for testing only. The regular class won't be loaded
 * for testing. And we can't mock it easily, since methods are static.
 */
public class Log {

    public static int v(String tag, String msg) {
        System.out.println("VERBOSE: " + tag + " - " + msg);
        return 0;
    }

    public static int d(String tag, String msg) {
        System.out.println("DEBUG: " + tag + " - " + msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.println("INFO: " + tag + " - " + msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.println("WARN: " + tag + " - " + msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.println("ERROR: " + tag + " - " + msg);
        return 0;
    }

    public static int f(String tag, String msg) {
        System.out.println("FATAL: " + tag + " - " + msg);
        return 0;
    }
}