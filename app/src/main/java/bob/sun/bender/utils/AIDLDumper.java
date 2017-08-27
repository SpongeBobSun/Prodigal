package bob.sun.bender.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import bob.sun.bender.MainActivity;
import bob.sun.bender.PlayerServiceAIDL;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.service.PlayerService;

/**
 * Created by bob.sun on 09/02/2017.
 */

public class AIDLDumper {

    private MainActivity context;
    private PlayerServiceAIDL service;
    private static AIDLDumper instance;
    private ArrayList<RemoteOperation> pending;

    private AIDLDumper(MainActivity context) {
        this.context = context;
        pending = new ArrayList<>();
    }

    public static AIDLDumper getInstance(MainActivity context) {
        if (instance == null) {
            instance = new AIDLDumper(context);
        }
        return instance;
    }

    private PlayerServiceAIDL getService() {
        if (service == null) {
            service = context.playerService;
        }
        return service;
    }

    public void onServiceBinded() {
        for (RemoteOperation ro : pending) {
            ro.run();
        }
        pending.clear();
    }

    public boolean isPlaying() {
        PlayerServiceAIDL service = getService();
        boolean ret = false;
        try {
            ret = service.isPlaying();
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return ret;
    }

    public ArrayList<SongBean> getPlayList() {
        PlayerServiceAIDL service = getService();
        ArrayList ret = null;
        try {
            ret = (ArrayList) service.getPlayList();
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return ret;
    }

    public SongBean getCurrentSong() {
        PlayerServiceAIDL service = getService();
        SongBean ret = null;
        try {
            ret = service.getCurrentSong();
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return ret;
    }

    public void setPlaylist(ArrayList list) {
        PlayerServiceAIDL service = getService();
        try {
            service.setPlayList(list);
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }

    public int getCurrent() {
        PlayerServiceAIDL service = getService();
        long ret = 0;
        try {
            ret = service.getCurrent();
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return (int) ret;
    }

    public int getDuration() {
        PlayerServiceAIDL service = getService();
        long ret = 0;
        try {
            ret = service.getDuration();
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return (int) ret;
    }

    public void updateSettings() {
        PlayerServiceAIDL service = getService();
        UserDefaults ud = UserDefaults.getStaticInstance(null);
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod(), ud.getRepeat(), ud.isShuffle()));
            return;
        }
        try {
            service.updateSettings(ud.getRepeat(), ud.isShuffle());
        } catch (RemoteException e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }

    public SongBean getPrevSong() {
        PlayerServiceAIDL service = getService();
        SongBean ret = null;
        try {
            ret = service.getPrevSong();
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return ret;
    }
    public SongBean getNextSong() {
        PlayerServiceAIDL service = getService();
        SongBean ret = null;
        try {
            ret = service.getNextSong();
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
        return ret;
    }
    public void play(final SongBean song, final int index) {
        final PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod(), song, index));
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    service.play(song, index);
                } catch (Exception e) {
                    Logger.dExp(e);
                    Crashlytics.logException(e);
                }
            }
        });
    }
    public void next() {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod()));
            return;
        }
        try {
            service.next();
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }
    public void previous() {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod()));
            return;
        }
        try {
            service.previous();
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }
    public void pause() {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod()));
            return;
        }
        try {
            service.pause();
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }
    public void resume(SongBean song) {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod(), song));
            return;
        }
        try {
            service.resume(song);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }
    public void prepare(int index) {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod(), index));
            return;
        }
        try {
            service.prepare(index);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }
    public void seek(int position) {
        PlayerServiceAIDL service = getService();
        if (service == null) {
            class Fetcher {}
            pending.add(new RemoteOperation(Fetcher.class.getEnclosingMethod(), position));
            return;
        }
        try {
            service.seek(position);
        } catch (Exception e) {
            Logger.dExp(e);
            Crashlytics.logException(e);
        }
    }

    class RemoteOperation implements Runnable {
        private Method method;
        private Object[] params;

        public RemoteOperation(Method method, Object... params) {
            this.method = method;
            this.params = params;
        }

        @Override
        public void run() {
            try {
                method.invoke(AIDLDumper.this, params);
            } catch (IllegalAccessException e) {
                Logger.dExp(e);
                Crashlytics.logException(e);
            } catch (InvocationTargetException e) {
                Logger.dExp(e);
                Crashlytics.logException(e);
            }
        }
    }
}
