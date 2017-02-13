package bob.sun.bender.utils;

import android.os.RemoteException;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import bob.sun.bender.PlayerServiceAIDL;
import bob.sun.bender.model.SongBean;
import io.fabric.sdk.android.Fabric;

/**
 * Created by bob.sun on 09/02/2017.
 */

public class AIDLDumper {

    public static boolean isPlaying(PlayerServiceAIDL service) {
        boolean ret = false;
        try {
            ret = service.isPlaying();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return ret;
    }

    public static ArrayList<SongBean> getPlayList(PlayerServiceAIDL service) {
        ArrayList ret = null;
        try {
            ret = (ArrayList) service.getPlayList();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return ret;
    }

    public static SongBean getCurrentSong(PlayerServiceAIDL service) {
        SongBean ret = null;
        try {
            ret = service.getCurrentSong();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return ret;
    }

    public static void setPlaylist(PlayerServiceAIDL service, ArrayList list) {
        try {
            service.setPlayList(list);
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static int getCurrent(PlayerServiceAIDL service) {
        long ret = 0;
        try {
            ret = service.getCurrent();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return (int) ret;
    }

    public static int getDuration(PlayerServiceAIDL service) {
        long ret = 0;
        try {
            ret = service.getDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return (int) ret;
    }

    public static void updateSettings(PlayerServiceAIDL service) {
        UserDefaults ud = UserDefaults.getStaticInstance(null);
        try {
            service.updateSettings(ud.getRepeat(), ud.isShuffle());
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
