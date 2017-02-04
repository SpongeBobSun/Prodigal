package bob.sun.mpod;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.SimpleListMenuAdapter;
import bob.sun.mpod.fragments.AboutFragment;
import bob.sun.mpod.fragments.MainMenuFragment;
import bob.sun.mpod.fragments.NowPlayingFragment;
import bob.sun.mpod.fragments.SettingsFragment;
import bob.sun.mpod.fragments.SimpleListFragment;
import bob.sun.mpod.fragments.TwoPanelFragment;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.PlayList;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SettingAdapter;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.service.PlayerService;
import bob.sun.mpod.utils.PreferenceUtil;
import bob.sun.mpod.utils.VibrateUtil;
import bob.sun.mpod.view.WheelView;


public class MainActivity extends AppCompatActivity implements OnButtonListener {
    private FragmentManager fragmentManager;
    private MainMenuFragment mainMenu;
    private SimpleListFragment songsList;
    private SimpleListFragment artistsList;
    private SimpleListFragment albumsList;
    private SimpleListFragment genresList;
    private NowPlayingFragment nowPlayingFragment;
    private SettingsFragment settingMenu;

    private SimpleListFragment artistsAlbumList;
    private SimpleListFragment artistsAlbumSongList;
    private SimpleListFragment albumSongList;

    private WheelView wheelView;
    private ServiceConnection serviceConnection;
    public PlayerService playerService;
    private OnTickListener currentTickObject;
    private Fragment currentFragment;
    private Stack<Fragment> fragmentStack;

    private Intent serviceIntent;

    private SongBean lastSongBean;
    private ArrayList lastPlayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VibrateUtil.getStaticInstance(this);

        initFragments();

        wheelView = (WheelView) findViewById(R.id.id_wheel_view);
        wheelView.setOnButtonListener(this);
        wheelView.setOnTickListener(mainMenu);
        this.currentTickObject = mainMenu;

        initOnButtonListener();

        startService();

        MediaLibrary.getStaticInstance(this);

        //Unit Test for MeidaLibrary
        ArrayList<String> list = MediaLibrary.getStaticInstance(this).getArtistsByGenre("Rock");
        for(String bean : list){
//            Log.e(bean.getArtist(),bean.getFileName());
            Log.e("Artists - ",bean);
        }

    }

    private void initFragments(){
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
            songsList.setAdatper(adapter);
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
            artistsList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,artistsList,"artistsList").hide(artistsList).commit();
        }

        albumsList = (SimpleListFragment) fragmentManager.findFragmentByTag("albumList");
        if (albumsList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllAlbums());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
            albumsList = new SimpleListFragment();
            albumsList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,albumsList,"albumList").hide(albumsList).commit();
        }

        genresList = (SimpleListFragment) fragmentManager.findFragmentByTag("genresList");
        if (genresList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllGenre());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_GENRE);
            genresList = new SimpleListFragment();
            genresList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,genresList,"genresList").hide(genresList).commit();
        }

        settingMenu = (SettingsFragment) fragmentManager.findFragmentByTag("settingMenu");
        if (settingMenu == null){
            settingMenu = new SettingsFragment();
            SettingAdapter adapter = SettingAdapter.getStaticInstance(this);
            settingMenu.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,settingMenu,"settingMenu").hide(settingMenu).commit();
        }

        fragmentStack = new Stack<>();
        currentFragment = mainMenu;
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

    private void startService(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                playerService = (PlayerService) ((PlayerService.ServiceBinder) service).getService();
                playerService.setPlayingListener(nowPlayingFragment);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerService = null;
            }
        };
        serviceIntent = new Intent(this,PlayerService.class);
        this.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        startService();
//    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
//        this.stopService(serviceIntent);
        unbindService(serviceConnection);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onResume(){
        super.onResume();
        lastSongBean = new SongBean();
        SharedPreferences preferences = PreferenceUtil.getStaticInstance(this).getPreferences();
        lastSongBean.setId(preferences.getLong("Id", 0));
        lastSongBean.setTitle(preferences.getString("Title", ""));
        lastSongBean.setAlbum(preferences.getString("Album", ""));
        lastSongBean.setArtist(preferences.getString("Artist", ""));
        lastSongBean.setFilePath(preferences.getString("FilePath", ""));
        lastSongBean.setGenre(preferences.getString("Genre", ""));
        lastSongBean.setDuration((int) preferences.getLong("Duration", 0));

//        HashSet prefList = (HashSet) PreferenceUtil.getStaticInstance(this).getPreferences().getStringSet("LastList",null);
//        if (prefList == null)
//            return;
//        lastPlayList = new ArrayList();
//        Iterator iterator = prefList.iterator();
//        while(iterator.hasNext())
//            lastPlayList.add(MediaLibrary.getStaticInstance(this).getSongById((String) iterator.next()));
        File objectFile = new File("/data/data/bob.sun.mpod/playlistobject");
        if (! objectFile.exists())
            return;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream( new FileInputStream(objectFile));
            lastPlayList = new ArrayList();
            lastPlayList.addAll((ArrayList) objectInputStream.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onPause(){
        super.onPause();
        if (playerService == null) {
            return;
        }
        SongBean bean = playerService.getCurrentSong();
        if (bean == null){
            return;
        }
        PreferenceUtil.getStaticInstance(this).getPreferences().edit()
                .putLong("Id",bean.getId())
                .putString("Title",bean.getTitle())
                .putString("Album",bean.getAlbum())
                .putString("Artist",bean.getArtist())
                .putString("FilePath",bean.getFilePath())
                .putString("Genre",bean.getGenre())
                .putLong("Duration",bean.getDuration())
                .commit();
        lastPlayList = playerService.getPlayList();
        if (lastPlayList == null)
            return;
        PlayList saveList = new PlayList();
        saveList.addAll(lastPlayList);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/data/data/bob.sun.mpod/playlistobject"));
            oos.writeObject(saveList);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        HashSet saveList = new HashSet();
//        Iterator iterator = lastPlayList.iterator();
//        while(iterator.hasNext())
//            saveList.add(""+((SongBean) iterator.next()).getId());
//        PreferenceUtil.getStaticInstance(this).getPreferences().edit().putStringSet("LastList", saveList).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                super.onKeyDown(keyCode,event);
                onPause();
                return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onMenu() {
        Log.e("mPod", "onMenu");
        if(fragmentStack.isEmpty())
            return;

        Fragment fragment = fragmentStack.pop();

        if(currentFragment != mainMenu && currentFragment != artistsList
        && currentFragment != albumsList && currentFragment != nowPlayingFragment
        && currentFragment != songsList && currentFragment != genresList
        && currentFragment != settingMenu /* && currentFragment != playingList*/){
            fragmentManager.beginTransaction()
                    .remove(currentFragment).show(fragment).commit();
        }else {
            fragmentManager.beginTransaction()
                    .hide(currentFragment).show(fragment).commit();
        }
        currentFragment = fragment;
        currentTickObject = (OnTickListener) fragment;
        wheelView.setOnTickListener((OnTickListener) fragment);

        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onPlay() {
        if(playerService == null){
            Intent intent = new Intent(this,PlayerService.class);
            this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        }
        if (playerService.isPlaying() == true){
            Intent intent = new Intent(this,PlayerService.class);
            intent.putExtra("CMD", PlayerService.CMD_PAUSE);
            startService(intent);
        }else{
            //TODO
            //Add resume & pick play logic here.
            Intent intent = new Intent(this,PlayerService.class);
            if (playerService.getCurrentSong() == null && playerService.getPlayList() == null) {
                intent.putExtra("DATA",lastSongBean.getFilePath());
            }
            intent.putExtra("CMD",PlayerService.CMD_RESUME);
            startService(intent);
        }
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onNext() {
        Log.e("mPod","onNext");
        Intent intent = new Intent(this,PlayerService.class);
        intent.putExtra("CMD",PlayerService.CMD_NEXT);
        startService(intent);
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @Override
    public void onPrevious() {
        Log.e("mPod", "onPrevious");
        Intent intent = new Intent(this,PlayerService.class);
        intent.putExtra("CMD",PlayerService.CMD_PREVIOUS);
        startService(intent);
        VibrateUtil.getStaticInstance(null).TickVibrate();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSelect(){
        VibrateUtil.getStaticInstance(null).TickVibrate();
        SelectionDetail detail = currentTickObject.getCurrentSelection();
        if (detail == null){
            Toast.makeText(this,"SelectionDetail is NULL",Toast.LENGTH_LONG).show();
            return;
        }
        switch (detail.getMenuType()){
            case SelectionDetail.MENU_TPYE_MAIN:
                switch ((String) detail.getData()){
                    case "Songs":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(songsList);
                            }
                        });
                        break;
                    case "Artists":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(artistsList);
                            }
                        });
                        break;
                    case "Albums":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(albumsList);
                            }
                        });
                        break;
                    case "Genres":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(genresList);
                            }
                        });
                        break;
                    case "Playlist":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                SimpleListFragment menu = new SimpleListFragment();
                                SimpleListMenuAdapter adapter;
                                if (playerService != null && playerService.getPlayList() != null) {
                                    adapter = new SimpleListMenuAdapter(MainActivity.this,R.layout.item_simple_list_view,playerService.getPlayList());
                                    menu.setAdatper(adapter);
                                    adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                                }else {
                                    if (lastPlayList != null){
                                        adapter = new SimpleListMenuAdapter(MainActivity.this,R.layout.item_simple_list_view,lastPlayList);
                                        menu.setAdatper(adapter);
                                        adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                                    } else {
                                        ArrayList crap = new ArrayList();
                                        crap.add("No playing list.");
                                        adapter = new SimpleListMenuAdapter(MainActivity.this, R.layout.item_simple_list_view, crap);
                                        menu.setAdatper(adapter);
                                        adapter.setArrayListType(-1);
                                    }
                                }

                                fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,menu).hide(menu).commit();
                                switchFragmentTo(menu);
                            }
                        });
                        break;
                    case "Now Playing":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(nowPlayingFragment);
                                if (playerService.getCurrentSong() != null){
                                    nowPlayingFragment.setSong(playerService.getCurrentSong());
                                }else {
                                    if (lastSongBean != null)
                                        nowPlayingFragment.setSong(lastSongBean);
                                }
                            }
                        });
                        break;
                    case "Shuffle Songs":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(nowPlayingFragment);
                                Intent intent = new Intent(MainActivity.this,PlayerService.class);
                                ArrayList playList = MediaLibrary.getStaticInstance(MainActivity.this).shuffleList(MediaLibrary.getStaticInstance(MainActivity.this).getAllSongs(MediaLibrary.ORDER_BY_ARTIST));
                                intent.putExtra("CMD",PlayerService.CMD_PLAY);
                                intent.putExtra("DATA",((SongBean) playList.get(0)).getFilePath());
                                intent.putExtra("INDEX", 0);
                                startService(intent);
                                nowPlayingFragment.setSong((SongBean) playList.get(0));
                                playerService.setPlayList(playList);
                            }
                        });

                        break;
                    case "Setting":
                        ((MainMenuFragment) currentFragment).dismiss(new TwoPanelFragment.DismissCallback() {
                            @Override
                            public void dismissed() {
                                switchFragmentTo(settingMenu);
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
                artistMenu.setAdatper(artistAdapter);
                artistAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,artistMenu).hide(artistMenu).commit();
                switchFragmentTo(artistMenu);
                break;
            case SelectionDetail.MENU_TYPE_ALBUM:
                SimpleListMenuAdapter albumAdapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getSongsByAlbum((String)detail.getData()));
                SimpleListFragment albumMenu = new SimpleListFragment();
                albumMenu.setAdatper(albumAdapter);
                albumAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,albumMenu).hide(albumMenu).commit();
                switchFragmentTo(albumMenu);
                break;
            case SelectionDetail.MENU_TYPE_GENRES:
                SimpleListMenuAdapter genresAdapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getArtistsByGenre((String) detail.getData()));
                SimpleListFragment genresMenu = new SimpleListFragment();
                genresMenu.setAdatper(genresAdapter);
                genresAdapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ARTIST);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,genresMenu).hide(genresMenu).commit();
                switchFragmentTo(genresMenu);
                break;
            case SelectionDetail.MENU_TYPE_SONGS:
                fragmentStack.push(currentFragment);
                nowPlayingFragment.setSong((SongBean) detail.getData());
                fragmentManager.beginTransaction().hide(currentFragment).show(nowPlayingFragment).commit();
                currentFragment = nowPlayingFragment;
                this.currentTickObject = nowPlayingFragment;
                wheelView.setOnTickListener(nowPlayingFragment);

                Intent intent = new Intent(this,PlayerService.class);
                intent.putExtra("CMD",PlayerService.CMD_PLAY);
                intent.putExtra("DATA",((SongBean) detail.getData()).getFilePath());
                intent.putExtra("INDEX",detail.getIndexOfList());
                startService(intent);
                playerService.setPlayList(detail.getPlaylist());

                break;
            case SelectionDetail.MENU_TYPE_SETTING:
                switch ((String) detail.getData()){
                    case "About":
                        AboutFragment aboutFragment = new AboutFragment();
                        fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,aboutFragment).hide(aboutFragment).commit();
                        switchFragmentTo(aboutFragment);
                        break;
                    case "Shuffle":

                        break;
                    case "Repeat":

                        break;
                }
                break;
            default:
                break;
        }
    }
    private void switchFragmentTo(Fragment fragment){
        fragmentStack.push(currentFragment);
        fragmentManager.beginTransaction().hide(currentFragment).show(fragment).commit();
        currentFragment = fragment;
        this.currentTickObject = (OnTickListener) fragment;
        wheelView.setOnTickListener((OnTickListener) fragment);
    }
}
