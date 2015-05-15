package bob.sun.mpod.model;

import java.util.ArrayList;

/**
 * Created by sunkuan on 15/5/1.
 */
public class SelectionDetail {

    public static final int MENU_TPYE_MAIN = 1;
    public static final int MENU_TYPE_ARTIST = 2;
    public static final int MENU_TYPE_ALBUM = 3;
    public static final int MENU_TYPE_SONGS = 4;
    public static final int MENU_TYPE_GENRES = 5;

    public static final int DATA_TYPE_STRING = 1;
    public static final int DATA_TYPE_SONG = 2;


    private int menuType;
    private int dataType;
    private Object data;
    private int superType;
    private int subType;
    private ArrayList<SongBean> playlist;
    private int indexOfList;

    public int getMenuType() {
        return menuType;
    }

    public void setMenuType(int menuType) {
        this.menuType = menuType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getSuperType() {
        return superType;
    }

    public void setSuperType(int superType) {
        this.superType = superType;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public ArrayList<SongBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<SongBean> playlist) {
        this.playlist = playlist;
    }

    public int getIndexOfList() {
        return indexOfList;
    }

    public void setIndexOfList(int indexOfList) {
        this.indexOfList = indexOfList;
    }
}
