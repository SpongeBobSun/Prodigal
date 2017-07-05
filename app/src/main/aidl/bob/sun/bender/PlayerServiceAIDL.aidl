// PlayerServiceAIDL.aidl
package bob.sun.bender;

// Declare any non-default types here with import statements
import java.util.List;
import bob.sun.bender.model.SongBean;

interface PlayerServiceAIDL {

    boolean isPlaying();
    List getPlayList();
    void setPlayList(in List list);
    long getDuration();
    long getCurrent();
    SongBean getCurrentSong();
    void updateSettings(int repeatMode, boolean shuffle);
    SongBean getPrevSong();
    SongBean getNextSong();
    void play(in SongBean song, int index);
    void next();
    void previous();
    void pause();
    void resume(in SongBean song);
    void prepare(int index);
    void seek(int position);
}
