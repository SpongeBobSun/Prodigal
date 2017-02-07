package bob.sun.mpod.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.squareup.picasso.Picasso;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.PlayingListener;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.utils.VolumeUtil;
import bob.sun.mpod.view.ProgressView;


/**
 * Created by sunkuan on 15/5/4.
 */
public class NowPlayingFragment extends Fragment implements OnTickListener,PlayingListener{
    SongBean song;
    View view;
    NumberProgressBar progressView;
    TextView currentTime, totalTime;
    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup parent,
                             Bundle savedInstanceState
                             ){
        View ret = layoutInflater.inflate(R.layout.layout_now_playing,parent,false);
        view = ret;
        currentTime = (TextView) view.findViewById(R.id.current_time);
        totalTime = (TextView) view.findViewById(R.id.total_time);
        return ret;
    }

    public void setSong(SongBean songBean){
        song = songBean;
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_title)).setText(song.getTitle());
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_artist)).setText(song.getArtist());
        ((TextView) view.findViewById(R.id.id_now_playing_text_view_album)).setText(song.getAlbum());
        progressView = (NumberProgressBar) view.findViewById(R.id.id_progress_view);
        progressView.setMax(100);
        progressView.setProgress(0);
        String img = MediaLibrary.getStaticInstance(view.getContext())
                .getCoverUriByAlbumId(songBean.getAlbumId());
        Picasso.with(view.getContext())
                .load(Uri.parse(img))
                .placeholder(R.drawable.album)
                .config(Bitmap.Config.RGB_565)
                .into((ImageView) view.findViewById(R.id.id_nowplaying_image_view_cover));
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
    public void onProcessChanged(final int current, final int total) {
        if (progressView != null )
        view.post(new Runnable() {
            @Override
            public void run() {
                progressView.setProgress((int) (((float)current / (float)total) * 100));
                currentTime.setText(String.format("%02d:%02d", (current / 1000 / 60), (current / 1000 % 60)));
                totalTime.setText(String.format("%02d:%02d", (total / 1000 / 60), (total / 1000 % 60)));
            }
        });

    }
}
