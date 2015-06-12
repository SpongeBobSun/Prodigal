package bob.sun.mpod.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import bob.sun.mpod.R;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SongBean;

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
    private PendingIntent clickIntent;
    private RemoteViews remoteViews;

    private NotificationUtil(Context context){
        appContext = context;
        notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(appContext);
        notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        this.setRemoteView(R.layout.layout_notification);

        notification.contentView = remoteViews;
        //Todo
        //Switch next & previous.
//        remoteViews.setOnClickPendingIntent();
    }

    public static NotificationUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new NotificationUtil(context);
        return staticInstance;
    }

    public NotificationUtil setClass(Class c){
        clickIntent = PendingIntent.getActivity(appContext,0,new Intent(appContext,c),0);
        return this;
    }

    public NotificationCompat.Builder getBuilder(){
        return builder;
    }

    public void setRemoteView(int res){
        remoteViews = new RemoteViews(appContext.getPackageName(),res);
    }

    /**
    //General interface for NotificationUtil. Add it to SBLib someday...
    public void sendTextNotification(String text){

    }

    public void sendNotificationWithView(String text,RemoteViews remoteViews){

    }

    public void sendTextOngoingNotification(String text){

    }

    public void sendOngoingNotificationWithView(String text,RemoteViews remoteViews){

    }
*/
    //Interfaces for mPod only.
    //In mPod, we only use ongoing notifications.

    public void sendPlayNotification(SongBean bean){
        notification.tickerText = bean.getTitle();
        notification.contentView.setImageViewBitmap(R.id.id_image_view_notification,
                MediaLibrary.getStaticInstance(appContext).getCoverImageBySong(bean.getId()));

        notification.icon = R.drawable.ic_nowplaying;
        if (clickIntent != null){
            notification.contentIntent = clickIntent;
        }
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    public void changeSong(SongBean bean){
        sendPlayNotification(bean);
    }

    public void sendPauseNotification(){

    }
    public void sendStopNotification(){

    }
}
