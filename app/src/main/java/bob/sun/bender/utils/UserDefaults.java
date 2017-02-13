package bob.sun.bender.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static bob.sun.bender.utils.AppConstants.RepeatAll;
import static bob.sun.bender.utils.AppConstants.RepeatNone;
import static bob.sun.bender.utils.AppConstants.kRepeat;
import static bob.sun.bender.utils.AppConstants.kShuffle;
import static bob.sun.bender.utils.AppConstants.pref_tag;

/**
 * Created by sunkuan on 15/5/12.
 */
public class UserDefaults {
    private static UserDefaults staticInstance;
    private SharedPreferences preferences;

    private UserDefaults(Context context){
        preferences = context.getSharedPreferences(pref_tag,0);

        if (!preferences.contains(kRepeat)) {
            setRepeat(RepeatAll);
        }
        if (!preferences.contains(kShuffle)) {
            setShuffle(false);
        }
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
        return preferences.getInt(kRepeat, AppConstants.RepeatAll);
    }

    private void setRepeat(int mode) {
        preferences.edit().putInt(kRepeat, mode).commit();
    }

    public boolean isShuffle(){
        boolean ret = preferences.getBoolean(kShuffle, false);
        return ret;
    }

    private void setShuffle(boolean shuffle) {
        preferences.edit().putBoolean(kShuffle, shuffle).commit();
    }

    public void rollShuffle() {
        boolean current = isShuffle();
        setShuffle(!current);
    }

    public void rollRepeat() {
        int current = getRepeat();
        if (current == RepeatNone) {
            setRepeat(RepeatAll);
        } else {
            setRepeat(current + 1);
        }
    }
}
