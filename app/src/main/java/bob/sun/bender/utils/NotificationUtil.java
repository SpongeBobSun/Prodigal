package bob.sun.bender.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
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
    private PendingIntent clickIntent, nextIntent, prevIntent, playIntent;
    private RemoteViews bigView, normalView;
    private Target imgLoaderTarget;

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
    }

    private void init() {
        normalView = new RemoteViews(appContext.getPackageName(), R.layout.layout_notification);
        bigView = new RemoteViews(appContext.getPackageName(), R.layout.layout_notification_big);
        Intent intentMain = new Intent(appContext, MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        clickIntent = PendingIntent.getActivity(appContext, 1, intentMain, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent intentPlay = new Intent(appContext, PlayerService.class);
//        intentPlay.putExtra("CMD", PlayerService.CMD_PAUSE);
//        playIntent = PendingIntent.getService(appContext, PlayerService.CMD_PAUSE, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent intentNext = new Intent(appContext, PlayerService.class);
//        intentNext.putExtra("CMD", PlayerService.CMD_NEXT);
//        nextIntent = PendingIntent.getService(appContext, PlayerService.CMD_NEXT, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent intentPrev = new Intent(appContext, PlayerService.class);
//        intentPrev.putExtra("CMD", PlayerService.CMD_PREVIOUS);
//        prevIntent = PendingIntent.getService(appContext, PlayerService.CMD_PREVIOUS, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        normalView.setOnClickPendingIntent(R.id.id_button_notification_pause, playIntent);
        normalView.setOnClickPendingIntent(R.id.id_button_notification_prev, prevIntent);
        normalView.setOnClickPendingIntent(R.id.id_button_notification_next, nextIntent);

        bigView.setOnClickPendingIntent(R.id.id_button_notification_pause, playIntent);
        bigView.setOnClickPendingIntent(R.id.id_button_notification_prev, prevIntent);
        bigView.setOnClickPendingIntent(R.id.id_button_notification_next, nextIntent);
    }

    public static NotificationUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new NotificationUtil(context);
        return staticInstance;
    }


    public void sendPlayNotification(final SongBean bean){
        normalView.setTextViewText(R.id.id_notification_title,bean.getTitle());
        bigView.setTextViewText(R.id.id_notification_title,bean.getTitle());

        imgLoaderTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                normalView.setImageViewBitmap(R.id.id_image_view_notification,bitmap);
                bigView.setImageViewBitmap(R.id.id_image_view_notification,bitmap);
                notificationManager.notify(NOTIFICATION_ID,notification);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                normalView.setImageViewResource(R.id.id_image_view_notification, R.drawable.album);
                bigView.setImageViewResource(R.id.id_image_view_notification, R.drawable.album);
                notificationManager.notify(NOTIFICATION_ID,notification);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(appContext)
                .load(MediaLibrary.getStaticInstance(appContext)
                        .getCoverUriByAlbumId(bean.getAlbumId()))
                .error(R.drawable.album)
                .into(imgLoaderTarget);

        notification.tickerText = bean.getTitle();
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void changeSong(SongBean bean){
        sendPlayNotification(bean);
    }

}
