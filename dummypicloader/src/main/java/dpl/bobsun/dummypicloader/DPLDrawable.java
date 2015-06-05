package dpl.bobsun.dummypicloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by bobsun on 15-5-26.
 */
public class DPLDrawable extends BitmapDrawable {
    private WeakReference DPLTaskRef;
    public DPLDrawable(Resources res, String fileName,DPLTask task){
        DPLTaskRef = new WeakReference(task);
    }
    public DPLDrawable(Resources res, Bitmap bitmap,DPLTask task){
        DPLTaskRef = new WeakReference(task);
    }
    public DPLTask getTask(){
        return (DPLTask) DPLTaskRef.get();
    }

}
