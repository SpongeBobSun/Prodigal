package bob.sun.mpod.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;

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

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.reset();
        mediaPlayer.release();
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        try{
        mediaPlayer.reset();
        mediaPlayer.release();
        }catch (IllegalStateException e){

        }
    }

}
