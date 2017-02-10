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

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer mediaPlayer;
    private ArrayList<SongBean> playlist;
    private int index;
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
        switch (intent.getIntExtra("CMD",-1)){
            case CMD_PLAY:
                String fileName = intent.getStringExtra("DATA");
                index = intent.getIntExtra("INDEX",0);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    Intent msg = new Intent(AppConstants.broadcastSongChange);
                    msg.setPackage(this.getPackageName());
                    sendBroadcast(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                NotificationUtil.getStaticInstance(getApplicationContext()).sendPlayNotification(playlist.get(index));
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
                if (!mediaPlayer.isPlaying()){
                    if (playlist == null || playlist.size() == 0){
                        String resumeName = intent.getStringExtra("DATA");
                        if (resumeName == null)
                            break;
                        try {
                            mediaPlayer.setDataSource(resumeName);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    } else {
                        mediaPlayer.start();
                    }

                    if (playlist != null && playlist.size() > 0)
                        NotificationUtil.getStaticInstance(getApplicationContext()).sendPlayNotification(playlist.get(index));
                }
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
        //Todo
        //Add play sequence logic here.
        //  *Shuffle
        //  *Looping
        //  *Loop list
        onNext();
    }

    private void onNext(){
        if (playlist == null || index >= playlist.size()-1){
            return;
        }
        index++;
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(playlist.get(index).getFilePath());
            mediaPlayer.prepare();
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
                    return playlist.indexOf(index);
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
                if (playlist == null || playlist.size() == 0)
                    return null;
                return playlist.get(index);
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.reset();
        mediaPlayer.release();
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
