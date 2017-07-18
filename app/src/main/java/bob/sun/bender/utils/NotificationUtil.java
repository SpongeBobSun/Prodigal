package bob.sun.bender.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import bob.sun.bender.MainActivity;
import bob.sun.bender.R;
import bob.sun.bender.model.MediaLibrary;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.service.PlayerService;

/**
 * Created by bobsun on 15-6-12.
 */
public class NotificationUtil {

    public static final int NOTIFICATION_ID = 0x5020;


    private static NotificationUtil staticInstance;
    private Context appContext;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private PendingIntent clickIntent, nextIntent, prevIntent, playIntent, pauseIntent;
    private RemoteViews bigView, normalView;
    private Target imgLoaderTarget;

    private ComponentName receiverName;

    private NotificationUtil(Context context){
        appContext = context;
        notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        init();

        builder = new NotificationCompat.Builder(appContext);
        builder.setCustomBigContentView(bigView)
                .setCustomContentView(normalView)
                .setSmallIcon(R.drawable.pod_notification)
                .setContentIntent(clickIntent);
        notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        receiverName = new ComponentName(context.getApplicationContext(), MediaButtonReceiver.class);
    }

    private void init() {
        Intent intentMain = new Intent(appContext, MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        clickIntent = PendingIntent.getActivity(appContext, 1, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(appContext, PlayerService.class);
        intentPlay.putExtra("CMD", PlayerService.cmdPause);
        playIntent = PendingIntent.getService(appContext, PlayerService.cmdPause, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPause = new Intent(appContext, PlayerService.class);
        intentPause.putExtra("CMD", PlayerService.cmdPlay);
        pauseIntent = PendingIntent.getService(appContext, PlayerService.cmdPlay, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(appContext, PlayerService.class);
        intentNext.putExtra("CMD", PlayerService.cmdNext);
        nextIntent = PendingIntent.getService(appContext, PlayerService.cmdNext, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPrev = new Intent(appContext, PlayerService.class);
        intentPrev.putExtra("CMD", PlayerService.cmdPrev);
        prevIntent = PendingIntent.getService(appContext, PlayerService.cmdPrev, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static NotificationUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new NotificationUtil(context);
        return staticInstance;
    }


    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }


    public void showPlayingNotification(PlayerService service, MediaSessionCompat session) {
        NotificationCompat.Builder builder = notificationBuilder(service, session);
        if( builder == null ) {
            return;
        }
        NotificationCompat.Action action;
        if (session.isActive()) {
            action = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", playIntent);
        } else {
            action = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pauseIntent);
        }
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous",
                prevIntent));
        builder.addAction(action);
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next",
                nextIntent));

        builder.setOngoing(true);

        builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(1).setMediaSession(session.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(service).notify(NOTIFICATION_ID, builder.build());
    }

    private NotificationCompat.Builder notificationBuilder(PlayerService context, MediaSessionCompat session) {
        MediaControllerCompat controller = session.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART))
                .setContentIntent(clickIntent)
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, receiverName, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return builder;
    }
}
