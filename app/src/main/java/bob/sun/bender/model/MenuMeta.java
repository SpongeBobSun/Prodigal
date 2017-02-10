package bob.sun.bender.model;

/**
 * Created by bobsun on 15-5-22.
 */
public class MenuMeta {
    public enum MenuType {
        Artists,
        Albums,
        Coverflow,
        Songs,
        Playlist,
        Genres,
        ShuffleSongs,
        Settings,
        NowPlaying,
        //Settings
        About,
        ShuffleSettings,
        RepeatSettings,
    }
    public String itemName;
    public boolean highlight;
    public MenuType menuType;

    public MenuMeta(String arg1,boolean arg2, MenuType type){
        itemName = arg1;
        highlight = arg2;
        menuType = type;
    }
}