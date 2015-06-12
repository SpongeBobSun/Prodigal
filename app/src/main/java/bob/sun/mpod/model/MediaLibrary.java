package bob.sun.mpod.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import bob.sun.mpod.R;

/**
 * Created by sunkuan on 15/4/30.
 */
public class MediaLibrary {
    private static MediaLibrary staticInstance;
    private Context appContext;
    private ContentResolver contentResolver;
    public static final int ORDER_BY_ARTIST = 1;
    public static final int ORDER_BY_ALBUM = 2;
    public static final int ORDER_BY_GENERES = 3;
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
        Cursor cursor;
        String artistId;
        cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "artist=?",
                new String[]{artist},
                null);
        if(!cursor.moveToNext()){
            return ret;
        }
        artistId = ""+(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        cursor.close();

        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                "artist_id=?",
                new String[]{artistId},
                "artist_id asc");
        SongBean addBean;
        while(cursor.moveToNext()){
            addBean = new SongBean();
            addBean.populateBean(cursor);
            ret.add(addBean);
        }
        cursor.close();
        return ret;
    }

    public ArrayList<SongBean> getSongsByAlbum(String album){
        ArrayList ret = new ArrayList<SongBean>();
        Cursor cursor;
        String albumId;
        cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "album=?",
                new String[]{album},
                null);
        if(!cursor.moveToNext()){
            return ret;
        }
        albumId = ""+(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        cursor.close();

        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                "album_id=?",
                new String[]{albumId},
                null);
        SongBean addBean;
        while(cursor.moveToNext()){
            addBean = new SongBean();
            addBean.populateBean(cursor);
            ret.add(addBean);
        }
        cursor.close();
        return ret;
    }

    public ArrayList<String> getAlbumsByArtist(String artist){

        ArrayList ret = new ArrayList<String>();
        Cursor cursor;
        String artistId;
        cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "artist=?",
                new String[]{artist},
                null);
        if(!cursor.moveToNext()){
            return ret;
        }
        artistId = ""+(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        cursor.close();

        cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                null,
                "artist_id=?",
                new String[]{artistId},
                "artist_id asc");

        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AlbumColumns.ALBUM)));
        }
        cursor.close();
        return ret;

    }
    public ArrayList<SongBean> getAllSongs(int order){
        ArrayList ret = new ArrayList<SongBean>();
        Cursor cursor;
        switch (order){
            case ORDER_BY_ALBUM:
                cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        "album_id asc"
                );
                break;
            case ORDER_BY_ARTIST:
                cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        "artist_id asc"
                );
                break;
            default:
                cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER
                );
                break;
        }

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

    public ArrayList<String> getAllAlbums(){
        ArrayList ret = new ArrayList<String>();
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndexOrThrow("album")));
        }
        cursor.close();
        return ret;
    }

    public ArrayList<String> getAllArtists(){
        ArrayList ret = new ArrayList<String>();
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                    );
        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndexOrThrow("artist")));
        }
        cursor.close();
        return ret;
    }

    public ArrayList<String> getAllGenre(){
        ArrayList ret = new ArrayList<String>();
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.GenresColumns.NAME)));
        }
        cursor.close();
        return ret;
    }

    public ArrayList<String> getArtistsByGenre(String genre){
        ArrayList ret = new ArrayList<String>();
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "name=?",
                new String[]{genre},
                null);
        if(!cursor.moveToNext()){
            return ret;
        }
        Long genreId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        cursor.close();
        cursor = contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external",genreId),
                new String[]{"distinct "+MediaStore.Audio.Genres.Members.ARTIST},
                null,
                null,
                null
        );
        while(cursor.moveToNext()){
            ret.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.ARTIST)));
        }
        cursor.close();
        return ret;
    }

    public ArrayList<SongBean> shuffleList(ArrayList<SongBean> originalList){
        ArrayList<SongBean> ret = new ArrayList<>();
        while(originalList.size() >0 ){
            ret.add(originalList.remove(new Random().nextInt(originalList.size())));
        }
        return ret;
    }

    public Bitmap getCoverImageBySong(long songId){
        Uri sArtworkUri = Uri.parse("content://media/external/audio/media/" + songId + "/albumart");
        ContentResolver res = appContext.getContentResolver();
        InputStream in = null;
        try {
            in = res.openInputStream(sArtworkUri);
            return BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_launcher);
        }
    }

    public String getCoverUriBySong(long songId){
        return "content://media/external/audio/media/" + songId + "/albumart";
    }

    public Bitmap getCoverImageByAlbum(long albumId){
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        ContentResolver res = appContext.getContentResolver();
        InputStream in = null;
        try {
            in = res.openInputStream(uri);
            return BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_launcher);
        }
    }

    public Bitmap getCoverImageByAlbum(String albumName){
        Cursor cursor;
        long albumId;
        cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "album=?",
                new String[]{albumName},
                null);
        if(!cursor.moveToNext()){
            return BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_launcher);
        }
        albumId = (cursor.getLong(cursor.getColumnIndexOrThrow("_id")));

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart/" + albumId);
        ContentResolver res = appContext.getContentResolver();
        InputStream in = null;
        try {
            in = res.openInputStream(sArtworkUri);
            return BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_launcher);
        }
    }

    public String getCoverUriByAlbum(String albumName){
        Cursor cursor;
        long albumId;
        cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{"_id"},
                "album=?",
                new String[]{albumName},
                null);
        if(!cursor.moveToNext()){
            return "content://media/external/audio/albumart/0";
        }
        albumId = (cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
        return "content://media/external/audio/albumart/"+albumId;
    }

    public SongBean getSongById(String id){
        SongBean ret = null;
        Cursor cursor;
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                "_id=?",
                new String[]{id},
                null
                );
        while (cursor.moveToNext()){
            ret = new SongBean();
            ret.populateBean(cursor);
        }
        return ret;
    }
}
