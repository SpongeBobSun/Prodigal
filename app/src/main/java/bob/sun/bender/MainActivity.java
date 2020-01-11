package bob.sun.bender;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.graphics.ColorUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import bob.sun.bender.adapters.SimpleListMenuAdapter;
import bob.sun.bender.controller.OnButtonListener;
import bob.sun.bender.controller.OnTickListener;
import bob.sun.bender.fragments.AboutFragment;
import bob.sun.bender.fragments.CoverflowFragment;
import bob.sun.bender.fragments.MainMenuFragment;
import bob.sun.bender.fragments.NowPlayingFragment;
import bob.sun.bender.fragments.SettingsFragment;
import bob.sun.bender.fragments.SimpleListFragment;
import bob.sun.bender.fragments.TwoPanelFragment;
import bob.sun.bender.intro.BDIntroActivity;
import bob.sun.bender.model.MediaLibrary;
import bob.sun.bender.model.MenuMeta;
import bob.sun.bender.model.PlayList;
import bob.sun.bender.model.SelectionDetail;
import bob.sun.bender.model.SettingAdapter;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.service.PlayerService;
import bob.sun.bender.theme.Theme;
import bob.sun.bender.theme.ThemeManager;
import bob.sun.bender.utils.AIDLDumper;
import bob.sun.bender.utils.AppConstants;
import bob.sun.bender.utils.NotificationUtil;
import bob.sun.bender.utils.ResUtil;
import bob.sun.bender.utils.UserDefaults;
import bob.sun.bender.utils.VibrateUtil;
import bob.sun.bender.view.WheelView;
import io.fabric.sdk.android.Fabric;
import jp.wasabeef.blurry.internal.Blur;
import jp.wasabeef.blurry.internal.BlurFactor;

public class MainActivity extends AppCompatActivity implements OnButtonListener {
    private FragmentManager fragmentManager;
    private MainMenuFragment mainMenu;
    private SimpleListFragment songsList;
    private SimpleListFragment artistsList;
    private SimpleListFragment albumsList;
    private CoverflowFragment coverFlow;
    private SimpleListFragment genresList;
    private NowPlayingFragment nowPlayingFragment;
    private SettingsFragment settingMenu;

    private ServiceBroadcastReceiver receiver;

    private WheelView wheelView;
    public PlayerServiceAIDL playerService;
    private OnTickListener currentTickObject;
    private Fragment currentFragment;
    private Stack<Fragment> fragmentStack;

    private Intent serviceIntent;
    private AIDLDumper dumper;

    private SongBean lastSongBean;
    private ArrayList lastPlayList;
    private boolean permissionGranted;
    private boolean keepDancing;

    private ImageView bNext, bPrev, bMenu, bPlay;
    private ImageView backgroundImage;
    private Point windowSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        backgroundImage = (ImageView) findViewById(R.id.id_album_background);
        windowSize = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(windowSize);

        VibrateUtil.getStaticInstance(this);
        MediaLibrary.getStaticInstance(this);
        ResUtil.getInstance(this);
        UserDefaults ud = UserDefaults.getStaticInstance(this);

        wheelView = (WheelView) findViewById(R.id.id_wheel_view);

        permissionGranted = false;
        requestPermission();

        initOnButtonListener();

        startService();

        dumper = AIDLDumper.getInstance(this);

        if (ud.shouldShowIntro()) {
            Intent intent = new Intent(this, BDIntroActivity.class);
            startActivity(intent);
        }

        loadTheme();

    }

    private void initFragments() {

        if (fragmentManager != null)
            return;

        fragmentManager = getSupportFragmentManager();
        mainMenu = (MainMenuFragment) fragmentManager.findFragmentByTag("mainMenu");
        if(mainMenu == null){
            mainMenu = new MainMenuFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.id_screen_fragment_container, mainMenu, "mainMenu").commit();
        }
        songsList = (SimpleListFragment) fragmentManager.findFragmentByTag("songsList");
        if(songsList == null){
            songsList = new SimpleListFragment();
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllSongs(MediaLibrary.ORDER_BY_ARTIST));
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
            songsList.setAdapter(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container, songsList, "songsList").hide(songsList).commit();
        }

        nowPlayingFragment = (NowPlayingFragment) fragmentManager.findFragmentByTag("nowPlayingFragment");
        if(nowPlayingFragment == null){
            nowPlayingFragment = new NowPlayingFragment();
            fragmentManager.beginTransaction().
                    add(R.id.id_screen_fragment_container, nowPlayingFragment, "nowPlayingFragment")
                    .hide(nowPlayingFragment)
                    .commit();
        }

        artistsList = (SimpleListFragment) fragmentManager.findFragmentByTag("artistsList");
        if (artistsList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllArtists());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ARTIST);
            artistsList = new SimpleListFragment();
            artistsList.setAdapter(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,artistsList,"artistsList").hide(artistsList).commit();
        }

        albumsList = (SimpleListFragment) fragmentManager.findFragmentByTag("albumList");
        if (albumsList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllAlbums());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
            albumsList = new SimpleListFragment();
            albumsList.setAdapter(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,albumsList,"albumList").hide(albumsList).commit();
        }

        coverFlow = (CoverflowFragment) fragmentManager.findFragmentByTag("coverFlow");
        if (coverFlow == null) {
            coverFlow = new CoverflowFragment();
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container, coverFlow, "coverFlow").hide(coverFlow).commit();
        }

        genresList = (SimpleListFragment) fragmentManager.findFragmentByTag("genresList");
        if (genresList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllGenre());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_GENRE);
            genresList = new SimpleListFragment();
            genresList.setAdapter(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,genresList,"genresList").hide(genresList).commit();
        }

        settingMenu = (SettingsFragment) fragmentManager.findFragmentByTag("settingMenu");
        if (settingMenu == null){
            settingMenu = new SettingsFragment();
            SettingAdapter adapter = SettingAdapter.getStaticInstance(this);
            settingMenu.setAdapter(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,settingMenu,"settingMenu").hide(settingMenu).commit();
        }

        fragmentStack = new Stack<>();
        currentFragment = mainMenu;

        wheelView.setOnButtonListener(this);
        wheelView.setOnTickListener(mainMenu);
        this.currentTickObject = mainMenu;
        return;
    }

    private void initOnButtonListener(){
        View view = findViewById(R.id.id_menu_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenu();
            }
        });
        view = findViewById(R.id.id_play_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay();
            }
        });
        view = findViewById(R.id.id_previous_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevious();
            }
        });
        view = findViewById(R.id.id_next_button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
    }

    private void requestPermission() {
        if (PermissionsManager.hasAllPermissions(MainActivity.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                })) {
            ThemeManager.getInstance(getApplicationContext());
            permissionGranted = true;
            return;
        }
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                permissionGranted = true;
                findViewById(R.id.id_holder_no_permission).setVisibility(View.GONE);
                ThemeManager.getInstance(getApplicationContext());
            }

            @Override
            public void onDenied(String permission) {
                permissionGranted = PermissionsManager.hasAllPermissions(MainActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerService = PlayerServiceAIDL.Stub.asInterface(service);
            if (lastPlayList != null)
                dumper.setPlaylist(lastPlayList);
            dumper.onServiceBinded();
            dumper.updateSettings();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerService = null;
        }
    };
    private void startService(){
        serviceIntent = new Intent(this,PlayerService.class);
        this.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.broadcastSongChange);
        if (receiver == null)
            receiver = new ServiceBroadcastReceiver();
        registerReceiver(receiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void finish() {
        if (!keepDancing) {
            NotificationUtil.getStaticInstance(getApplicationContext()).dismiss();
            stopService(new Intent(this, PlayerService.class));
        }
        unbindService(serviceConnection);

        super.finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume(){
        super.onResume();
        if (!permissionGranted) {
            findViewById(R.id.id_holder_no_permission).setVisibility(View.VISIBLE);
            return;
        }
        keepDancing = false;
        findViewById(R.id.id_holder_no_permission).setVisibility(View.GONE);
        initFragments();
        lastSongBean = new SongBean();
        SharedPreferences preferences = UserDefaults.getStaticInstance(this).getPreferences();
        lastSongBean.setId(preferences.getLong("Id", -1));
        lastSongBean.setTitle(preferences.getString("Title", ""));
        lastSongBean.setAlbum(preferences.getString("Album", ""));
        lastSongBean.setAlbumId(preferences.getLong("AlbumId", -1));
        lastSongBean.setArtist(preferences.getString("Artist", ""));
        lastSongBean.setFilePath(preferences.getString("FilePath", ""));
        lastSongBean.setGenre(preferences.getString("Genre", ""));
        lastSongBean.setDuration((int) preferences.getLong("Duration", 0));

        File objectFile = new File(AppConstants.getPlayistfile());
        if (!objectFile.exists()) {
            lastPlayList = new ArrayList();
            try {
                new File(AppConstants.getExtFolder()).mkdirs();
                objectFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lastPlayList = new ArrayList();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream( new FileInputStream(objectFile));
            lastPlayList.addAll((ArrayList) objectInputStream.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (lastSongBean.getId() != -1 && playerService != null) {
            dumper.setPlaylist(lastPlayList);
        }
        int index;
        if (lastPlayList.contains(lastSongBean)) {
            index = lastPlayList.indexOf(lastSongBean);
            dumper.prepare(index);
        }

        if (playerService != null) {
            try {
                if (playerService.isPlaying()) {
                    SongBean current = playerService.getCurrentSong();
                    loadBackground(current);
                }
            } catch (RemoteException e) {
                //Eat it
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (playerService == null) {
            return;
        }
        SongBean bean = null;
        try {
            bean = playerService.getCurrentSong();
        } catch (RemoteException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        bean = bean == null ? lastSongBean : bean;
        if (bean == null){
            return;
        }

        UserDefaults.getStaticInstance(this).getPreferences().edit()
                .putLong("Id",bean.getId())
                .putString("Title",bean.getTitle())
                .putString("Album",bean.getAlbum())
                .putLong("AlbumId", bean.getAlbumId())
                .putString("Artist",bean.getArtist())
                .putString("FilePath",bean.getFilePath())
                .putString("Genre",bean.getGenre())
                .putLong("Duration",bean.getDuration())
                .commit();
        lastPlayList = dumper.getPlayList();
        if (lastPlayList == null)
            return;
        PlayList saveList = new PlayList();
        saveList.addAll(lastPlayList);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConstants.getPlayistfile()));
            oos.writeObject(saveList);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                keepDancing = true;
                onPause();
                break;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onMenu() {
        if(fragmentStack == null || fragmentStack.isEmpty())
            return;

        Fragment fragment = fragmentStack.pop();

        //Fuck me =.=
        if(currentFragment != mainMenu && currentFragment != artistsList
        && currentFragment != albumsList && currentFragment != nowPlayingFragment
        && currentFragment != songsList && currentFragment != genresList
        && currentFragment != settingMenu && currentFragment != coverFlow
            /* && currentFragment != playingList*/){
            //Back from album songs or artist albums or etc.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ft.remove(currentFragment).show(fragment).commit();
        }else {
            //Back to main menu.
            fragmentManager.beginTransaction().hide(currentFragment).show(fragment).commit();
            if (fragment.getTag() != null && fragment.getTag().equalsIgnoreCase("mainMenu")) {
                ((TwoPanelFragment) fragment).show(null);
            }
        }
        currentFragment = fragment;
        currentTickObject = (OnTickListener) fragment;
        wheelView.setOnTickListener((OnTickListener) fragment);
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onPlay() {
        if(playerService == null){
            startService();
            return;
        }
        boolean empty = false;
        if (dumper.isPlaying()) {
            dumper.pause();
            empty = true;
        } else {
            SongBean current = dumper.getCurrentSong();
            if (current != null && current.getId() != -1) {
                //TODO: Is this causing crash?
                dumper.resume(null);
                empty = true;
            } else if (lastSongBean != null && lastSongBean.getId() != -1){
                dumper.play(lastSongBean, -1);
                empty = true;
            }
        }
        if (!empty)
            Toast.makeText(this, R.string.nothing_to_play, Toast.LENGTH_SHORT).show();

        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onNext() {
        if (!permissionGranted)
            return;
        dumper.next();
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onPrevious() {
        if (!permissionGranted)
            return;
        dumper.previous();
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSelect(){
        VibrateUtil.getStaticInstance(null).TickVibrate();
        SelectionDetail detail = currentTickObject.getCurrentSelection();
        if (detail == null){
            return;
        }
        switch (detail.getMenuType()){
            case SelectionDetail.MENU_TPYE_MAIN:
                switch ((MenuMeta.MenuType) detail.getData()){
                    case Songs:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(songsList, false);
                            }
                        });
                        break;
                    case Artists:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(artistsList, false);
                            }
                        });
                        break;
                    case Albums:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(albumsList, false);
                            }
                        });
                        break;
                    case Coverflow:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(coverFlow, false);
                            }
                        });
                        break;
                    case Genres:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(genresList, false);
                            }
                        });
                        break;
                    case Playlist:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                SimpleListFragment menu = new SimpleListFragment();
                                SimpleListMenuAdapter adapter;
                                if (playerService != null && dumper.getPlayList() != null) {
                                    adapter = new SimpleListMenuAdapter(MainActivity.this,R.layout.item_simple_list_view,dumper.getPlayList());
                                    menu.setAdapter(adapter);
                                    adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                                }else {
                                    if (lastPlayList != null){
                                        adapter = new SimpleListMenuAdapter(MainActivity.this,R.layout.item_simple_list_view,lastPlayList);
                                        menu.setAdapter(adapter);
                                        adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                                    } else {
                                        ArrayList crap = new ArrayList();
                                        crap.add("No playing list.");
                                        adapter = new SimpleListMenuAdapter(MainActivity.this, R.layout.item_simple_list_view, crap);
                                        menu.setAdapter(adapter);
                                        adapter.setArrayListType(-1);
                                    }
                                }

                                fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,menu).hide(menu).commit();
                                switchFragmentTo(menu, false);
                            }
                        });
                        break;
                    case NowPlaying:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(nowPlayingFragment, false);
                                SongBean song = dumper.getCurrentSong();
                                if (song != null) {
                                    nowPlayingFragment.setSong(song);
                                } else {
                                    if (lastSongBean.getId() != -1)
                                        nowPlayingFragment.setSong(lastSongBean);
                                }
                            }
                        });
                        break;
                    case ShuffleSongs:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                ArrayList playList = MediaLibrary.getStaticInstance(MainActivity.this).shuffleList(MediaLibrary.getStaticInstance(MainActivity.this).getAllSongs(MediaLibrary.ORDER_BY_ARTIST));
                                if (playList == null || playList.size() == 0)
                                    return;
                                if (playerService != null) {
                                    dumper.setPlaylist(playList);
                                } else {
                                    lastPlayList = playList;
                                    startService();
                                }
                                dumper.play((SongBean) playList.get(0), 0);
                                switchFragmentTo(nowPlayingFragment, false);
                                nowPlayingFragment.setSong((SongBean) playList.get(0));
                                loadBackground((SongBean) playList.get(0));
                            }
                        });

                        break;
                    case Settings:
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(settingMenu, false);
                            }
                        });
                        break;
                }
                break;
            case SelectionDetail.MENU_TYPE_ARTIST:
                SimpleListMenuAdapter artistAdapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getAlbumsByArtist((String) detail.getData()));
                SimpleListFragment artistMenu = new SimpleListFragment();
                artistMenu.setAdapter(artistAdapter);
                artistAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,artistMenu).hide(artistMenu).commit();
                switchFragmentTo(artistMenu, true);
                break;
            case SelectionDetail.MENU_TYPE_ALBUM:
                SimpleListMenuAdapter albumAdapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getSongsByAlbum((String)detail.getData()));
                SimpleListFragment albumMenu = new SimpleListFragment();
                albumMenu.setAdapter(albumAdapter);
                albumAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,albumMenu).hide(albumMenu).commit();
                switchFragmentTo(albumMenu, true);
                break;
            case SelectionDetail.MENU_TYPE_GENRES:
                SimpleListMenuAdapter genresAdapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getArtistsByGenre((String) detail.getData()));
                SimpleListFragment genresMenu = new SimpleListFragment();
                genresMenu.setAdapter(genresAdapter);
                genresAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ARTIST);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,genresMenu).hide(genresMenu).commit();
                switchFragmentTo(genresMenu, true);
                break;
            case SelectionDetail.MENU_TYPE_SONGS:
                fragmentStack.push(currentFragment);
                nowPlayingFragment.setSong((SongBean) detail.getData());

                if (playerService == null)
                    startService();

                dumper.setPlaylist(detail.getPlaylist());
                dumper.play((SongBean) detail.getData(), detail.getIndexOfList());

                fragmentManager.beginTransaction().hide(currentFragment).show(nowPlayingFragment).commit();
                currentFragment = nowPlayingFragment;
                this.currentTickObject = nowPlayingFragment;
                wheelView.setOnTickListener(nowPlayingFragment);
                break;
            case SelectionDetail.MENU_TYPE_SETTING:
                switch ((MenuMeta.MenuType)detail.getData()){
                    case About:
                        AboutFragment aboutFragment = new AboutFragment();
                        fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,aboutFragment).hide(aboutFragment).commit();
                        switchFragmentTo(aboutFragment, true);
                        break;
                    case ShuffleSettings:
                        UserDefaults.getStaticInstance(this).rollShuffle();
                        settingMenu.reloadSettings();
                        dumper.updateSettings();
                        break;
                    case ThemeSettings:
                        SimpleListMenuAdapter themesAdapter = new SimpleListMenuAdapter(getApplicationContext(),
                                R.layout.item_simple_list_view,
                                ThemeManager.getInstance(getApplicationContext()).getAllThemes());
                        SimpleListFragment themesFragment = new SimpleListFragment();
                        themesFragment.setAdapter(themesAdapter);
                        themesAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_THEME);
                        fragmentManager.beginTransaction()
                                .add(R.id.id_screen_fragment_container,themesFragment).hide(themesFragment).commit();
                        switchFragmentTo(themesFragment, true);
                        break;
                    case RepeatSettings:
                        UserDefaults.getStaticInstance(this).rollRepeat();
                        settingMenu.reloadSettings();
                        dumper.updateSettings();
                        break;
                    case GetSourceCode:
                        Intent source = new Intent(Intent.ACTION_VIEW);
                        source.setData(Uri.parse("https://github.com/SpongeBobSun/Prodigal"));
                        startActivity(source);
                        break;
                    case ContactUs:
                        Intent contact = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:bobsun@outlook.com"));
                        contact.setType("text/html");
                        contact.putExtra(Intent.EXTRA_EMAIL, new String[]{"bobsun@outlook.com"});
                        contact.putExtra(Intent.EXTRA_SUBJECT, "");
                        contact.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(contact, getResources().getString(R.string.send_mail_using)));
                        break;
                }
                break;
            case SelectionDetail.MENU_TYPE_THEMES:
                String theme = (String) detail.getData();
                UserDefaults.getStaticInstance(getApplicationContext()).setTheme(theme);
                loadTheme();
                break;
            default:
                break;
        }
    }
    private void switchFragmentTo(Fragment fragment, boolean slide){
        fragmentStack.push(currentFragment);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (slide) {
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        ft.hide(currentFragment).show(fragment).commit();
        currentFragment = fragment;
        this.currentTickObject = (OnTickListener) fragment;
        wheelView.setOnTickListener((OnTickListener) fragment);
    }

    public void loadTheme() {
        Theme theme = ThemeManager.getInstance(getApplicationContext()).loadCurrentTheme();
        CardView cardView = (CardView) findViewById(R.id.id_main_card);
        cardView.setBackgroundColor(theme.getCardColor());
        findViewById(R.id.id_main).setBackgroundColor(theme.getBackgroundColor());
        getWindow().getDecorView();

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(theme.getBackgroundColor());
        }
        wheelView.loadTheme();
        if (bMenu == null) {
            bMenu = (ImageView) findViewById(R.id.id_menu_button);
            bNext = (ImageView) findViewById(R.id.id_next_button);
            bPrev = (ImageView) findViewById(R.id.id_previous_button);
            bPlay = (ImageView) findViewById(R.id.id_play_button);
        }

        if (mainMenu != null) {
            mainMenu.loadTheme();
        }
        if (nowPlayingFragment != null) {
            nowPlayingFragment.loadTheme();
        }

        //Using Picasso here since we are allow user using their creativity.
        int size = getResources().getDimensionPixelSize(R.dimen.button_width);
        Picasso.with(this).load("file://" + theme.getMenuIcon()).fit().centerInside()
                .config(Bitmap.Config.RGB_565).error(R.drawable.menu).into(bMenu);
        Picasso.with(this).load("file://" + theme.getNextIcon()).fit().centerInside()
                .config(Bitmap.Config.RGB_565).error(R.drawable.next).into(bNext);
        Picasso.with(this).load("file://" + theme.getPlayIcon()).fit().centerInside()
                .config(Bitmap.Config.RGB_565).error(R.drawable.button).into(bPlay);
        Picasso.with(this).load("file://" + theme.getPrevIcon()).fit().centerInside()
                .config(Bitmap.Config.RGB_565).error(R.drawable.prev).into(bPrev);
        this.getWindow().getDecorView().invalidate();
    }

    public void loadBackground(SongBean bean) {
        if (bean == null ) {
            backgroundImage.setImageBitmap(null);
            return;
        }
        final Uri image = Uri.parse(MediaLibrary.getStaticInstance(getApplicationContext())
                .getCoverUriByAlbumId(bean.getAlbumId()));
        Picasso.with(this).load(image).resize(windowSize.x, windowSize.y)
                .centerCrop()
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        BlurFactor factor = new BlurFactor();
                        factor.radius = 25;
                        factor.sampling = 4;
                        factor.width = windowSize.x;
                        factor.height = windowSize.y;
                        factor.color = ColorUtils.setAlphaComponent(
                                ThemeManager.getInstance(getApplicationContext())
                                .loadCurrentTheme().getBackgroundColor(), 20);
                        Bitmap ret = Blur.of(getApplicationContext(), source, factor);
                        source.recycle();
                        return ret;
                    }

                    @Override
                    public String key() {
                        return image.toString() + "/blured";
                    }
                })
                .config(Bitmap.Config.RGB_565).into(backgroundImage);

    }

    class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (nowPlayingFragment != null && !nowPlayingFragment.isHidden())
                nowPlayingFragment.refreshSong();
            if (mainMenu != null && !mainMenu.isHidden())
                mainMenu.refreshCurrentSongIfNeeded();
            try {
                SongBean bean = playerService.getCurrentSong();
                loadBackground(bean);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}
