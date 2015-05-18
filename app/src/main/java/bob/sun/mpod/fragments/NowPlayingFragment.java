package bob.sun.mpod.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.PlayingListener;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.utils.VolumeUtil;

/**
 * Created by sunkuan on 15/5/4.
 */
public class NowPlayingFragment extends Fragment implements OnTickListener,PlayingListener{
    SongBean song;
    View view;
    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup parent,
                             Bundle savedInstanceState
                             ){
        View ret = layoutInflater.inflate(R.layout.layout_now_playing,parent,false);
        view = ret;
        return ret;
    }

    public void setSong(SongBean songBean){
        song = songBean;
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_title)).setText(song.getTitle());
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_artist)).setText(song.getArtist());
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_album)).setText(song.getAlbum());
    }

    @Override
    public void onNextTick() {
        VolumeUtil.getStaticInstance(getActivity()).raiseVolume();
    }

    @Override
    public void onPreviousTick() {
        VolumeUtil.getStaticInstance(getActivity()).reduceVolume();
    }

    @Override
    public SelectionDetail getCurrentSelection() {
        return null;
    }

    @Override
    public void onSongChanged(SongBean bean) {
        setSong(bean);
    }

    @Override
    public void onProcessChanged(int current, int total) {

    }
}
