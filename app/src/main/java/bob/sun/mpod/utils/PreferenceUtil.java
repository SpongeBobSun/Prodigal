package bob.sun.mpod.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static bob.sun.mpod.utils.AppConstants.pref_tag;

/**
 * Created by sunkuan on 15/5/12.
 */
public class PreferenceUtil {
    private static PreferenceUtil staticInstance;
    private SharedPreferences preferences;

    private PreferenceUtil(Context context){
        preferences = context.getSharedPreferences(pref_tag,0);
    }

    public static PreferenceUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new PreferenceUtil(context);
        return staticInstance;
    }

    public SharedPreferences getPreferences(){
        return preferences;
    }

    public int getLoopingFlag(){
        int ret = 0;
        return ret;
    }

    public boolean isShuffle(){
        boolean ret = false;
        return ret;
    }

}
