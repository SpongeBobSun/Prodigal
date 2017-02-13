package bob.sun.bender.utils;

import android.content.Context;

import bob.sun.bender.R;
import bob.sun.bender.utils.AppConstants;

/**
 * Created by bob.sun on 09/02/2017.
 */
public class ResUtil {
    private static ResUtil ourInstance;
    private Context context;

    public static ResUtil getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new ResUtil(context);
        return ourInstance;
    }

    private ResUtil(Context context) {
        this.context = context;
    }

    public String getLocalized(int resId) {
        return context.getResources().getString(resId);
    }

    public String getSettingsString(int settings) {
        String ret = "";
        switch (settings) {
            case AppConstants.RepeatAll:
                ret = context.getResources().getString(R.string.repeat_all);
                break;
            case AppConstants.RepeatNone:
                ret = context.getResources().getString(R.string.repeat_none);
                break;
            case AppConstants.RepeatOne:
                ret = context.getResources().getString(R.string.repeat_one);
                break;
        }
        return ret;
    }

    public String getBoolString(boolean b) {
        return b ? context.getResources().getString(R.string.str_true) : context.getResources().getString(R.string.str_false);
    }
}
