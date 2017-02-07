package bob.sun.mpod.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import bob.sun.mpod.MainActivity;
import bob.sun.mpod.R;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.service.PlayerService;

/**
 * Created by bobsun on 15-6-12.
 */
public class NotificationUtil {

    public static final int NOTIFICATION_ID = 1;


    private static NotificationUtil staticInstance;
    private Context appContext;
    private NotificationManager notificationManager;
    private Notification notification;
    private NotificationCompat.Builder builder;
    private PendingIntent clickIntent, nextIntent, prevIntent, playIntent;
    private RemoteViews bigView, normalView;

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
        bigView = new RemoteViews(appContext.getPackageName(), R.layout.layout_notification);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        clickIntent = PendingIntent.getActivity(appContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent = new Intent(appContext, PlayerService.class);
        intent.putExtra("CMD", PlayerService.CMD_PAUSE);
        playIntent = PendingIntent.getService(appContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent = new Intent(appContext, PlayerService.class);
        intent.putExtra("CMD", PlayerService.CMD_NEXT);
        nextIntent = PendingIntent.getService(appContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent = new Intent(appContext, PlayerService.class);
        intent.putExtra("CMD", PlayerService.CMD_PREVIOUS);
        prevIntent = PendingIntent.getService(appContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        normalView.setOnClickPendingIntent(R.id.id_button_notification_pause, playIntent);
        normalView.setOnClickPendingIntent(R.id.id_button_notification_prev, prevIntent);
        normalView.setOnClickPendingIntent(R.id.id_button_notification_next, nextIntent);
    }

    public static NotificationUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new NotificationUtil(context);
        return staticInstance;
    }


    public void sendPlayNotification(SongBean bean){
        notification.tickerText = bean.getTitle();
        notificationManager.notify(NOTIFICATION_ID,notification);
        Picasso.with(appContext).load(MediaLibrary.getStaticInstance(appContext).getCoverUriByAlbumId(bean.getAlbumId()))
                .into(normalView, R.id.id_image_view_notification, NOTIFICATION_ID, notification);
    }

    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void changeSong(SongBean bean){
        sendPlayNotification(bean);
    }

}
