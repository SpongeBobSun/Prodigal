// PlayerServiceAIDL.aidl
package bob.sun.mpod;

// Declare any non-default types here with import statements
import java.util.List;
import bob.sun.mpod.model.SongBean;

interface PlayerServiceAIDL {

    boolean isPlaying();
    List getPlayList();
    void setPlayList(in List list);
    long getDuration();
    long getCurrent();
    SongBean getCurrentSong();
}
