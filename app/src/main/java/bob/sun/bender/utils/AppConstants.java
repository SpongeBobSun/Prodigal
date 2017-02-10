package bob.sun.bender.utils;

/**
 * Created by bob.sun on 09/02/2017.
 */

public class AppConstants {

    public enum RepeatMode {
        All(0x233), One(0x234), None(0x235);

        private final int value;

        RepeatMode(int value) {
            this.value = value;
        }

        public int getValue(){
            return value;
        }
    }

    public static final String pref_tag = "mpod_app_settings";
    public static final String kShuffle = "mpod_app_settings.key.shuffle";
    public static final String kRepeat = "mpod_app_settings.key.repeat";

    public static final String broadcastSongChange = "sun.bob.bender.songchanged";
    public static final String broadcastPermission = "sun.bob.bender.allow_broadcast";

}
