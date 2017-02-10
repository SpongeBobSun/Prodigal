package bob.sun.bender.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by sunkuan on 15/5/10.
 */
public class VolumeUtil {
    private static VolumeUtil staticInstance;
    private AudioManager audioManager;
    private VolumeUtil(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
    public static VolumeUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new VolumeUtil(context);
        return staticInstance;
    }
    public void raiseVolume(){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                0);
    }
    public void reduceVolume(){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                0);
    }

    public int getMax() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getCurrent() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
}
