package bob.sun.mpod.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by sunkuan on 15/5/6.
 */
public class VibrateUtil {
    private static VibrateUtil staticInstance;
    private Context context;
    private Vibrator vibrator;
    private VibrateUtil(Context context){
        this.context = context;
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    public static VibrateUtil getStaticInstance(Context context){
        if(staticInstance == null)
            staticInstance = new VibrateUtil(context);
        return staticInstance;
    }

    public void TickVibrate(){
        vibrator.vibrate(20);
    }

}
