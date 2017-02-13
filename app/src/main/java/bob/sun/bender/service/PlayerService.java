package bob.sun.bender.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bob.sun.bender.PlayerServiceAIDL;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.utils.AppConstants;
import bob.sun.bender.utils.NotificationUtil;
import io.fabric.sdk.android.Fabric;

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private ArrayList<SongBean> playlist;
    private SongBean currentSong;
    private int index, repeatMode;
    private boolean shuffle;
    private AudioManager audioManager;
    public static final int CMD_PLAY = 1;
    public static final int CMD_PAUSE = 2;
    public static final int CMD_RESUME = 3;
    public static final int CMD_STOP = 4;
    public static final int CMD_NEXT = 5;
    public static final int CMD_PREVIOUS = 6;
    public static final int CMD_VOLUMN_UP = 7;
    public static final int CMD_VOLUMN_DOWN = 8;
    public static final int CMD_SEEK = 10;
    public static final int CMD_PREPARE = 11;

    @Override
    public void onCreate(){
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        repeatMode = AppConstants.RepeatAll;
        shuffle = false;

        if(mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        if (intent == null){
            return START_REDELIVER_INTENT;
        }
        SongBean song;
        switch (intent.getIntExtra("CMD",-1)){
            case CMD_PLAY:
                song = (SongBean) intent.getSerializableExtra("DATA");
                index = playlist.indexOf(song);
                if (index == -1)
                    index = intent.getIntExtra("INDEX",0);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(song.getFilePath());
                    mediaPlayer.prepare();
                    currentSong = song;
                    Intent msg = new Intent(AppConstants.broadcastSongChange);
                    msg.setPackage(this.getPackageName());
                    sendBroadcast(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                NotificationUtil.getStaticInstance(getApplicationContext()).sendPlayNotification(song);
                break;
            case CMD_PAUSE:
                if (mediaPlayer.isPlaying()){
                    NotificationUtil.getStaticInstance(getApplicationContext()).dismiss();
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                break;
            case CMD_RESUME:
                song = (SongBean) intent.getSerializableExtra("DATA");
                if (song != null) {
                    //Song from caller, means current not playing anything.
                    if (!mediaPlayer.isPlaying()) {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.prepare();
                            mediaPlayer.setDataSource(song.getFilePath());
                            currentSong = song;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }
                } else {
                    //Current is playing something
                    mediaPlayer.start();
                }
                song = song == null ? currentSong : song;
                if (song != null)
                    NotificationUtil
                            .getStaticInstance(getApplicationContext())
                            .sendPlayNotification(song);
                break;
            case CMD_NEXT:
                onNext();
                break;
            case CMD_PREVIOUS:
                onPrevious();
                break;
            case CMD_SEEK:
                int position = intent.getIntExtra("DATA", -1);
                onSeek(position);
                break;
            case CMD_PREPARE:
                index = intent.getIntExtra("INDEX", 0);
                break;
            default:
                break;
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (repeatMode == AppConstants.RepeatOne) {
            if (currentSong == null)
                return;
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentSong.getFilePath());
                mediaPlayer.prepare();
                Intent msg = new Intent(AppConstants.broadcastSongChange);
                msg.setPackage(this.getPackageName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (repeatMode == AppConstants.RepeatNone) {
            if (playlist != null && playlist.size() > 0) {
                if (index < playlist.size() - 1) {
                    onNext();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        } else {
            onNext();
        }
    }

    private void onNext(){
        if (playlist == null || index >= playlist.size() - 1){
            if (playlist.size() == 0)
                return;
            if (index >= playlist.size() - 1) {
                //We will increase this later.
                index = -1;
            } else {
                return;
            }
        }
        index++;
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playlist.get(index).getFilePath());
            mediaPlayer.prepare();
            currentSong = playlist.get(index);
            Intent msg = new Intent(AppConstants.broadcastSongChange);
            msg.setPackage(this.getPackageName());
            sendBroadcast(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        NotificationUtil.getStaticInstance(getApplicationContext()).changeSong(playlist.get(index));
    }
    private void onPrevious(){
        if (playlist == null || index <= 0){
            return;
        }
        index--;
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playlist.get(index).getFilePath());
            mediaPlayer.prepare();
            currentSong = playlist.get(index);
            Intent msg = new Intent(AppConstants.broadcastSongChange);
            msg.setPackage(this.getPackageName());
            sendBroadcast(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        NotificationUtil.getStaticInstance(getApplicationContext()).changeSong(playlist.get(index));
    }

    private void onSeek(int position) {
        if (position == -1) {
            return;
        }
        int duration = mediaPlayer.getDuration();
        int seek = (int) (duration * (position / 100f));
        mediaPlayer.seekTo(seek);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerServiceAIDL.Stub() {

            @Override
            public boolean isPlaying() throws RemoteException {
                return mediaPlayer.isPlaying();
            }

            @Override
            public List getPlayList() throws RemoteException {
                return playlist;
            }

            @Override
            public void setPlayList(List list) throws RemoteException {
                playlist = (ArrayList<SongBean>) list;
            }

            @Override
            public long getDuration() throws RemoteException {
                if (mediaPlayer.isPlaying())
                    return mediaPlayer.getDuration();
                if (playlist != null && playlist.size() > 0)
                    return playlist.get(index).getDuration();
                return -1;
            }

            @Override
            public long getCurrent() throws RemoteException {
                if (mediaPlayer.isPlaying())
                    return mediaPlayer.getCurrentPosition();
                return 0;
            }

            @Override
            public SongBean getCurrentSong() throws RemoteException {
                return currentSong;
            }

            @Override
            public void updateSettings(int repeatMode, boolean shuffle) throws RemoteException {
                PlayerService.this.repeatMode = repeatMode;
                PlayerService.this.shuffle = shuffle;
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        audioManager.abandonAudioFocus(this);
        NotificationUtil.getStaticInstance(getApplicationContext()).dismiss();
        try{
            mediaPlayer.reset();
            mediaPlayer.release();
        }catch (IllegalStateException e){

        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

}
