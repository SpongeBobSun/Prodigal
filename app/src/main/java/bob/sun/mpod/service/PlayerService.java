package bob.sun.mpod.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import bob.sun.mpod.controller.PlayingListener;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.utils.NotificationUtil;
import bob.sun.mpod.utils.PreferenceUtil;

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private final ServiceBinder binder = new ServiceBinder();
    private ArrayList<SongBean> playlist;
    private int index;
    private PlayingListener playingListener;
//    private Thread progressThread;
    private ProgressRunnable runnable;
    public static final int CMD_PLAY = 1;
    public static final int CMD_PAUSE = 2;
    public static final int CMD_RESUME = 3;
    public static final int CMD_STOP = 4;
    public static final int CMD_NEXT = 5;
    public static final int CMD_PREVIOUS = 6;
    public static final int CMD_VOLUMN_UP = 7;
    public static final int CMD_VOLUMN_DOWN = 8;

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
//        progressThread = new Thread(new ProgressRunnable());
        runnable = new ProgressRunnable();
//        new Thread(runnable).start();
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        if (intent == null){
            Log.e("PlayerService",""+flags);
            return START_FLAG_RETRY;
        }
        switch (intent.getIntExtra("CMD",-1)){
            case CMD_PLAY:
                String fileName = intent.getStringExtra("DATA");
                index = intent.getIntExtra("INDEX",0);
                runnable.stop();
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    if (playingListener!=null)
                        playingListener.onSongChanged(playlist.get(index));
                    runnable.start();
                    new Thread(runnable).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                NotificationUtil.getStaticInstance(this.getApplicationContext()).sendPlayNotification(playlist.get(index));
                break;
            case CMD_PAUSE:
                if (mediaPlayer.isPlaying()){
                   mediaPlayer.pause();
                }
                break;
            case CMD_RESUME:
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                break;
            case CMD_NEXT:
                onNext();
                break;
            case CMD_PREVIOUS:
                onPrevious();
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
        runnable.stop();
        try {
            mediaPlayer.setDataSource(playlist.get(index).getFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            runnable.start();
            new Thread(runnable).start();
            if (playingListener != null)
                playingListener.onSongChanged(playlist.get(index));
        } catch (IOException e) {
            e.printStackTrace();
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
        runnable.stop();
        try {
            mediaPlayer.setDataSource(playlist.get(index).getFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            runnable.start();
            new Thread(runnable).start();
            if (playingListener != null )
                playingListener.onSongChanged(playlist.get(index));
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationUtil.getStaticInstance(getApplicationContext()).changeSong(playlist.get(index));
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

//    @Override
//    public boolean onUnbind(Intent intent){
//        mediaPlayer.reset();
//        mediaPlayer.release();
//        return super.onUnbind(intent);
//    }
    @Override
    public void onDestroy() {
        Log.e("mPod PlayerService","onDestroy");
        try{
        mediaPlayer.reset();
        mediaPlayer.release();
        }catch (IllegalStateException e){

        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public class ServiceBinder extends Binder{
        public Service getService(){return PlayerService.this;}
    }

    public void setPlayList(ArrayList<SongBean> list){
        playlist = list;
    }

    public ArrayList getPlayList(){
        return playlist;
    }

    public void setPlayingListener(PlayingListener playingListener) {
        this.playingListener = playingListener;
    }

    private class ProgressRunnable implements Runnable{
        int total;
        int current;
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }
        public void start(){
            stop.set(false);
        }
        @Override
        public void run() {
            while (!stop.get()) {
                total = mediaPlayer.getDuration();
                if(total == 0)
                    continue;
                current = mediaPlayer.getCurrentPosition();
                if (playingListener == null)
                    continue;
                playingListener.onProcessChanged(current, total);
                SystemClock.sleep(1000);
            }
        }

    }

    public SongBean getCurrentSong(){
        if (playlist == null || playlist.get(index) == null)
            return null;
        return playlist.get(index);
    }
}
