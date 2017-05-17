package bob.sun.bender.utils;

import android.os.Environment;

/**
 * Created by bob.sun on 09/02/2017.
 */

public class AppConstants {

    public static final int RepeatAll       =   0x233;
    public static final int RepeatOne       =   0x234;
    public static final int RepeatNone      =   0x235;


    public static final String pref_tag = "mpod_app_settings";
    public static final String kShuffle = "mpod_app_settings.key.shuffle";
    public static final String kRepeat = "mpod_app_settings.key.repeat";

    public static final String broadcastSongChange = "sun.bob.bender.songchanged";
    public static final String broadcastPermission = "sun.bob.bender.allow_broadcast";

    private static final String playlistfile = "/data/data/bob.sun.bender/playlistobject";
    private static final String packageFolder = "/data/data/bob.sun.bender/";

    public static final String themeFolder = "/data/Prodigal/Themes/";

    public static String getPlayistfile() {
        return Environment.getExternalStorageDirectory().getPath() + playlistfile;
    }

    public static String getExtFolder() {
        return Environment.getExternalStorageDirectory().getPath() + packageFolder;
    }

}
