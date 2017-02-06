package bob.sun.mpod.model;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by bob.sun on 06/02/2017.
 */

public class AlbumBean implements Serializable {
    private String name;
    private String artist;
    private String cover;
    private String id;

    public void populateBean(Cursor cursor) {

        name = cursor.getString(cursor.getColumnIndexOrThrow("album"));
        artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
        id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        cover = "content://media/external/audio/albumart/"+id;
    }

    public String getName() {
        return name;
    }

    public AlbumBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public AlbumBean setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public AlbumBean setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public String getId() {
        return id;
    }

    public AlbumBean setId(String id) {
        this.id = id;
        return this;
    }
}
