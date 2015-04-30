package bob.sun.mpod.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by sunkuan on 15/4/30.
 */
public class MediaLibrary {
    private static MediaLibrary staticInstance;
    private Context appContext;
    private ContentResolver contentResolver;
    private MediaLibrary(Context context){
        appContext = context;
        contentResolver = appContext.getContentResolver();
    }

    public static MediaLibrary getStaticInstance(Context context){
        if(staticInstance == null)
            staticInstance = new MediaLibrary(context);
        return staticInstance;
    }

    public ArrayList<SongBean> getSongsByArtist(String artist){
        ArrayList ret = new ArrayList<SongBean>();
        return ret;
    }

    public ArrayList<SongBean> getSongsByAlbum(String album){
        ArrayList ret = new ArrayList<SongBean>();
        return ret;
    }

    public ArrayList<SongBean> getAllSongs(){
        ArrayList ret = new ArrayList<SongBean>();
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                );
        SongBean addBean;
        while (cursor.moveToNext()){
            addBean = new SongBean();
            addBean.populateBean(cursor);
            ret.add(addBean);
        }
        addBean = null;
        cursor.close();
        return ret;
    }

    public ArrayList<SongBean> getAllAlbums(){
        ArrayList ret = new ArrayList<SongBean>();
        return ret;
    }

    public ArrayList<SongBean> getAllArtists(){
        ArrayList ret = new ArrayList<SongBean>();
        return ret;
    }

}
