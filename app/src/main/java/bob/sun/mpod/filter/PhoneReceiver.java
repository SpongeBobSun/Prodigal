package bob.sun.mpod.filter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import bob.sun.mpod.service.PlayerService;

/**
 * Created by bobsun on 15-5-25.
 */
public class PhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Intent serviceIntent = new Intent(context,PlayerService.class);
            intent.putExtra("CMD", PlayerService.CMD_PAUSE);
            context.startService(serviceIntent);
        }else{
            TelephonyManager tm =
                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //Incoming call
                    Intent serviceIntent = new Intent(context,PlayerService.class);
                    intent.putExtra("CMD", PlayerService.CMD_PAUSE);
                    context.startService(serviceIntent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Accept incoming call
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    //Incoming call waiting
                    break;
            }
        }
    }
}
