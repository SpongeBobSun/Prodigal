package bob.sun.bender.utils;

import bob.sun.bender.BuildConfig;

/**
 * Created by bob.sun on 05/07/2017.
 */

public class Logger {
    public static void dExp(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
