package bob.sun.mpod.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static bob.sun.mpod.utils.AppConstants.kRepeat;
import static bob.sun.mpod.utils.AppConstants.kShuffle;
import static bob.sun.mpod.utils.AppConstants.pref_tag;
import static bob.sun.mpod.utils.AppConstants.RepeatMode;

/**
 * Created by sunkuan on 15/5/12.
 */
public class UserDefaults {
    private static UserDefaults staticInstance;
    private SharedPreferences preferences;

    private UserDefaults(Context context){
        preferences = context.getSharedPreferences(pref_tag,0);
    }

    public static UserDefaults getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new UserDefaults(context);
        return staticInstance;
    }

    public SharedPreferences getPreferences(){
        return preferences;
    }

    public int getRepeat() {
        return preferences.getInt(kRepeat, AppConstants.RepeatMode.All.getValue());
    }

    public void setRepeate(RepeatMode mode) {
        preferences.edit().putInt(kRepeat, mode.getValue()).commit();
    }

    public boolean isShuffle(){
        boolean ret = preferences.getBoolean(kShuffle, false);
        return ret;
    }

    public void setShuffle() {
        preferences.edit().putBoolean(kShuffle, true).commit();
    }
}
