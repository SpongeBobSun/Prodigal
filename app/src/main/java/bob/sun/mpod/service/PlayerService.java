package bob.sun.mpod.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;

    public static final int CMD_PLAY = 1;
    public static final int CMD_PAUSE = 2;
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

    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        switch (intent.getIntExtra("CMD",-1)){
            case CMD_PLAY:
                String fileName = intent.getStringExtra("DATA");
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(fileName);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CMD_PAUSE:
                if (!mediaPlayer.isPlaying()){
                   mediaPlayer.pause();
                }
                break;
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public boolean onUnbind(Intent intent){
//        mediaPlayer.reset();
//        mediaPlayer.release();
//        return super.onUnbind(intent);
//    }
    @Override
    public void onDestroy() {
        try{
        mediaPlayer.reset();
        mediaPlayer.release();
        }catch (IllegalStateException e){

        }
    }

}
