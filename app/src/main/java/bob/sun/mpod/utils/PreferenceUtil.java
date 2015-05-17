package bob.sun.mpod.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sunkuan on 15/5/12.
 */
public class PreferenceUtil {
    private PreferenceUtil staticInstance;
    private SharedPreferences preferences;
    private static final String PREF_TAG = "app_settings";

    public static final int LOOPING_FLAG_SINGLE = 0;
    public static final int LOOPING_FLAG_LIST = 1;
    public static final int LOOPING_FLAG_ONCE = 2;


    private PreferenceUtil(Context context){
        preferences = context.getSharedPreferences(PREF_TAG,0);
    }

    public PreferenceUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new PreferenceUtil(context);
        return staticInstance;
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
