package bob.sun.mpod.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import bob.sun.mpod.R;
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

    private NotificationUtil(Context context){
        appContext = context;
        notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
    }

    public static NotificationUtil getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new NotificationUtil(context);
        return staticInstance;
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
        notification.contentView = new RemoteViews(appContext.getPackageName(), R.layout.layout_notification);
        notification.icon = R.drawable.ic_nowplaying;
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    public void sendPauseNotification(){

    }
    public void sendStopNotification(){

    }
}
