package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;

/**
 * Created by sunkuan on 15/5/4.
 */
public class NowPlayingFragment extends Fragment implements OnTickListener{
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

    }

    @Override
    public void onPreviousTick() {

    }

    @Override
    public SelectionDetail getCurrentSelection() {
        return null;
    }
}
