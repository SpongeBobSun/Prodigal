package dpl.bobsun.dummypicloader;

import android.content.Context;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;

import dpl.bobsun.dummypicloader.cache.DPLDiskCache;
import dpl.bobsun.dummypicloader.cache.DPLRamCache;

/**
 * Created by bobsun on 15-5-26.
 */
public class DummyPicLoader {
    Context context;
    private int width, height;
    private boolean bmpSet;
    private boolean resized;
    private Bitmap defaultBitmap = null;
    private BitmapFactory.Options options;
    private String cacheKey;
    private DPLRamCache ramCache;
    private DummyPicLoader(Context context){
        this.context = context;
        width = 0;
        height = 0;
        options = new BitmapFactory.Options();
        ramCache = DPLRamCache.getStaticInstance();
    }
    private Context getContext(){
        return context;
    }

    public static DummyPicLoader getInstance(Context context){
        return new DummyPicLoader(context);
    }

    public void loadImageFromFile(String fileName,
                                            ImageView imageView){
        bmpSet = true;

        if (resized){
            cacheKey = fileName + options.outWidth + options.outHeight;
        }else{
            cacheKey = fileName;
        }

        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof DPLDrawable) {
            ((DPLDrawable) imageView.getDrawable()).getTask().cancel(true);
        }

        DPLTask task = new DPLTask(imageView,DPLTask.TASK_TYPE_FILE);

        Bitmap ramCacheBmp = ramCache.get(cacheKey);
        if (ramCacheBmp != null){
            Log.e("DummyPicLoader","found from ram cache");
            imageView.setImageDrawable(new DPLDrawable(getContext().getResources(),ramCacheBmp,task));
            imageView.setImageBitmap(ramCacheBmp);
            return;
        }

        task.setOptions(options,resized);
        DPLDrawable drawable;
        if (defaultBitmap == null){
            drawable = new DPLDrawable(getContext().getResources(),fileName,task);
        }else {
            drawable = new DPLDrawable(getContext().getResources(),defaultBitmap,task);
        }
        imageView.setImageDrawable(drawable);
        task.execute(fileName);
        return;
    }

    public void loadImageFromUrl(String urlAddr,ImageView imageView){
        bmpSet = true;

        if (resized){
            cacheKey = urlAddr + options.outWidth + options.outHeight;
        }else{
            cacheKey = urlAddr;
        }

        if (DPLDiskCache.getStaticInstance().isCached(cacheKey)){
            //WARNING
            //When calling loadImageFromFile, it will automatically append size info to 'urlAddr' and will find it from
            loadImageFromFile(DPLDiskCache.getStaticInstance().get(urlAddr), imageView);
            return;
        }

        DPLTask task = new DPLTask(imageView,DPLTask.TASK_TYPE_URL);

        task.setOptions(options,resized);

        DPLDrawable drawable;
        if (defaultBitmap == null){
            drawable = new DPLDrawable(getContext().getResources(),urlAddr,task);
        }else {
            drawable = new DPLDrawable(getContext().getResources(),defaultBitmap,task);
        }
        imageView.setImageDrawable(drawable);
        task.execute(urlAddr);
        return;
    }

    public void loadImageFromUri(String uri, ImageView imageView){
        bmpSet = true;

        if (imageView.getDrawable() != null && imageView.getDrawable() instanceof DPLDrawable) {
            ((DPLDrawable) imageView.getDrawable()).getTask().cancel(true);
        }

        DPLTask task = new DPLTask(imageView,DPLTask.TASK_TYPE_URI);
        task.setContext(context);

        Bitmap ramCacheBmp = ramCache.get(uri);
        if (ramCache.get(uri) != null){
            imageView.setImageDrawable(new DPLDrawable(getContext().getResources(),ramCache.get(uri),task));
            imageView.setImageBitmap(ramCacheBmp);
            return;
        }

        task.setOptions(options,resized);
        DPLDrawable drawable;
        if (defaultBitmap == null){
            drawable = new DPLDrawable(getContext().getResources(),uri,task);
        }else {
            drawable = new DPLDrawable(getContext().getResources(),defaultBitmap,task);
        }
        imageView.setImageDrawable(drawable);
        task.execute(uri);
        return;
    }

    public DummyPicLoader setQuality(){
        checkBitmapState();
        return this;
    }
    public DummyPicLoader resize(int width, int height){
        checkBitmapState();
        options.outWidth = width;
        options.outHeight = height;
        this.resized = true;
        return this;
    }


    public DummyPicLoader roundCorner(float radius){
        checkBitmapState();
        return this;
    }

    private void checkBitmapState(){
        if (bmpSet){
            throw new IllegalStateException("Bitmap alread set!");
        }
    }

    public DummyPicLoader setDefaultImage(int defaultResId){
        checkBitmapState();
        if (defaultBitmap == null)
            defaultBitmap = BitmapFactory.decodeResource(context.getResources(),defaultResId);
        return this;
    }
}
