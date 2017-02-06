package bob.sun.mpod.controller;

import bob.sun.mpod.model.SongBean;

/**
 * Created by sunkuan on 15/5/10.
 */
public interface PlayingListener {
    void onSongChanged(SongBean bean);
    void onProcessChanged(int current, int total);
}
