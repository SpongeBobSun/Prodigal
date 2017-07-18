package bob.sun.bender.service;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bob.sun.bender.PlayerServiceAIDL;
import bob.sun.bender.R;
import bob.sun.bender.model.MediaLibrary;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.utils.AppConstants;
import bob.sun.bender.utils.NotificationUtil;
import io.fabric.sdk.android.Fabric;

/**
 * Created by sunkuan on 15/4/29.
 */
public class PlayerService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener {

    public final static int cmdPlay   = 0x60;
    public final static int cmdPause  = 0x61;
    public final static int cmdNext   = 0x62;
    public final static int cmdPrev   = 0x63;

    private MediaPlayer mediaPlayer;
    private ArrayList<SongBean> playlist;
    private SongBean currentSong;
    private int index, repeatMode;
    private boolean shuffle;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;

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
        initMediaSession();
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "PrdPlayer", mediaButtonReceiver, null);
//        mediaSession.setCallback(mMediaSessionCallback);
        mediaSession.setFlags( MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS );

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);
        setSessionToken(mediaSession.getSessionToken());

    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        switch (intent.getIntExtra("CMD", -1)) {
            case cmdPause:
                onPause();
                break;
            case cmdNext:
                onNext();
                break;
            case cmdPrev:
                onPrevious();
                break;
            case cmdPlay:
                onPause();
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
        mediaSession.setActive(false);
        if (playlist == null || index >= playlist.size() - 1){
            if (playlist == null)
                return;
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
        mediaSession.setActive(true);
        sendNotification(currentSong);
    }
    private void onPrevious(){
        mediaSession.setActive(false);
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
        mediaSession.setActive(true);
        sendNotification(playlist.get(index));
    }

    private void onPause() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaSession.setActive(false);
            sendNotification(currentSong);
        } else {
            mediaPlayer.start();
            mediaSession.setActive(true);
            sendNotification(currentSong);
        }
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
        return new PlayerServiceBinder();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
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

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if(mediaPlayer != null) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if(mediaPlayer != null) {
                    if( !mediaPlayer.isPlaying() ) {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
            default: {
                if (focusChange <= 0) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }


    class PlayerServiceBinder extends PlayerServiceAIDL.Stub {
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

        @Override
        public SongBean getPrevSong() {
            if (index <= 0 || playlist.size() == 0) {
                return null;
            }
            return playlist.get(index - 1);
        }

        @Override
        public SongBean getNextSong() {
            if (index >= playlist.size() - 1 || playlist.size() == 0) {
                return null;
            }
            return playlist.get(index + 1);
        }

        @Override
        
        public void play(final SongBean song, int index) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (playlist == null) {
                        PlayerService.this.index = -1;
                    } else {
                        PlayerService.this.index = playlist.indexOf(song);
                    }
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(song.getFilePath());
                        mediaPlayer.prepare();
                        currentSong = song;
                        Intent msg = new Intent(AppConstants.broadcastSongChange);
                        msg.setPackage(PlayerService.this.getPackageName());
                        sendBroadcast(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                    mediaSession.setActive(true);
                    sendNotification(song);
                    }
                });
        }

        @Override
        public void next() throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onNext();
                }
            });
        }

        @Override
        public void previous() throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onPrevious();
                }
            });
        }

        @Override
        public void pause() throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onPause();
                }
            });
        }

        @Override
        public void resume(final SongBean songF) throws RemoteException {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SongBean song = songF;
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
                    if (song != null) {
                        mediaSession.setActive(true);
                        sendNotification(song);
                    }
                }
            });
        }

        @Override
        public void prepare(int index) throws RemoteException {
            PlayerService.this.index = index;
        }

        @Override
        public void seek(int position) throws RemoteException {
            final int positionF = position;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onSeek(positionF);
                }
            });
        }
    }

    private MediaMetadataCompat getMediaMetaFrom(SongBean bean, Bitmap bitmap) {
        MediaMetadataCompat ret;
        ret = new MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, bean.getArtist())
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, bean.getAlbum())
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, bean.getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, bean.getDuration())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
                .build();
        return ret;
    }

    private void sendNotification(final SongBean bean) {
        if (bean == null) {
            return;
        }
        Picasso.with(getApplicationContext())
                .load(MediaLibrary.getStaticInstance(getApplicationContext()).getCoverUriByAlbumId(bean.getAlbumId()))
                .config(Bitmap.Config.RGB_565)
                .error(R.drawable.album)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        MediaMetadataCompat metaData = getMediaMetaFrom(bean, bitmap);
                        mediaSession.setMetadata(metaData);
                        NotificationUtil.getStaticInstance(getApplicationContext())
                                .showPlayingNotification(PlayerService.this, mediaSession);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                                R.drawable.album, options);

                        MediaMetadataCompat metaData = getMediaMetaFrom(bean, bitmap);
                        mediaSession.setMetadata(metaData);
                        NotificationUtil.getStaticInstance(getApplicationContext())
                                .showPlayingNotification(PlayerService.this, mediaSession);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }
}
