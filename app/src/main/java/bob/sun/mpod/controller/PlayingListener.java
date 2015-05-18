package bob.sun.mpod.controller;

import bob.sun.mpod.model.SongBean;

/**
 * Created by sunkuan on 15/5/10.
 */
public interface PlayingListener {
    public void onSongChanged(SongBean bean);
    public void onProcessChanged(int current, int total);
}
