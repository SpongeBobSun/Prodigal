package bob.sun.mpod.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by sunkuan on 15/4/30.
 */
public class SongBean implements Serializable, Parcelable {
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private String genre;
    private String fileName;
    private String filePath;
    private int duration;
    private long size;
    private long id;

    public SongBean() {

    }

    public SongBean(Parcel in) {
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        albumId = in.readLong();
        genre = in.readString();
        fileName = in.readString();
        filePath = in.readString();
        duration = in.readInt();
        size = in.readLong();
        id = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(artist);
        out.writeString(album);
        out.writeLong(albumId);
        out.writeString(genre);
        out.writeString(fileName);
        out.writeString(filePath);
        out.writeInt(duration);
        out.writeLong(size);
        out.writeLong(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public SongBean setAlbumId(long albumId) {
        this.albumId = albumId;
        return this;
    }

    public void populateBean(Cursor cursor){
        setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
        setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
        setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
//        setGenre(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.)));
        setFileName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
        setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
        setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
        setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
    }

    public static final Creator<SongBean> CREATOR = new Creator<SongBean>() {

        @Override
        public SongBean createFromParcel(Parcel source) {
            return new SongBean(source);
        }

        @Override
        public SongBean[] newArray(int size) {
            return new SongBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
