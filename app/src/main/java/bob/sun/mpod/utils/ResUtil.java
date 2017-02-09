package bob.sun.mpod.utils;

import android.content.Context;

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
}
