package bob.sun.mpod;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.SimpleListMenuAdapter;
import bob.sun.mpod.fragments.MainMenu;
import bob.sun.mpod.fragments.NowPlayingFragment;
import bob.sun.mpod.fragments.SimpleListMenu;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.service.PlayerService;
import bob.sun.mpod.utils.VibrateUtil;
import bob.sun.mpod.view.WheelView;


public class MainActivity extends ActionBarActivity implements OnButtonListener {
    private FragmentManager fragmentManager;
    private MainMenu mainMenu;
    private SimpleListMenu songsList;
    private SimpleListMenu artistsList;
    private SimpleListMenu albumsList;
    private NowPlayingFragment nowPlayingFragment;
    private WheelView wheelView;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    private OnTickListener currentTickObject;
    private Fragment currentFragment;
    private Stack<Fragment> fragmentStack;
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
        ArrayList<String> list = MediaLibrary.getStaticInstance(this).getAllGenre();
        for(String bean : list){
//            Log.e(bean.getArtist(),bean.getFileName());
            Log.e("Albums",bean);
        }

    }

    private void initFragments(){
        fragmentManager = getSupportFragmentManager();
        mainMenu = (MainMenu) fragmentManager.findFragmentByTag("mainMenu");
        if(mainMenu == null){
            mainMenu = new MainMenu();
            fragmentManager.beginTransaction()
                    .add(R.id.id_screen_fragment_container,mainMenu,"mainMenu").commit();
        }
        songsList = (SimpleListMenu) fragmentManager.findFragmentByTag("songsList");
        if(songsList == null){
            songsList = new SimpleListMenu();
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllSongs(MediaLibrary.ORDER_BY_ARTIST));
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_TITLE);
            songsList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,songsList,"songsList").hide(songsList).commit();
        }

        nowPlayingFragment = (NowPlayingFragment) fragmentManager.findFragmentByTag("nowPlayingFragment");
        if(nowPlayingFragment == null){
            nowPlayingFragment = new NowPlayingFragment();
            fragmentManager.beginTransaction().
                    add(R.id.id_screen_fragment_container,nowPlayingFragment,"nowPlayingFragment")
                    .hide(nowPlayingFragment)
                    .commit();
        }

        artistsList = (SimpleListMenu) fragmentManager.findFragmentByTag("artistsList");
        if (artistsList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllArtists());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ARTIST);
            artistsList = new SimpleListMenu();
            artistsList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,artistsList,"artistsList").hide(artistsList).commit();
        }

        albumsList = (SimpleListMenu) fragmentManager.findFragmentByTag("albumList");
        if (albumsList == null){
            SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllAlbums());
            adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
            albumsList = new SimpleListMenu();
            albumsList.setAdatper(adapter);
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,albumsList,"albumList").hide(albumsList).commit();
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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerService = null;
            }
        };
        Intent intent = new Intent(this,PlayerService.class);
        this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        startService();
//    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(serviceConnection);
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

    //TODO
    //INITIAL & SAVE ALL FRAGMENT.
    @Override
    public void onMenu() {
        Log.e("mPod","onMenu");
        if(fragmentStack.isEmpty())
            return;

        Fragment fragment = fragmentStack.pop();
//        if(currentFragment != mainMenu || currentFragment != artistsList
//        || currentFragment != albumsList || currentFragment != nowPlayingFragment
//        || currentFragment != songsList){
//            fragmentManager.beginTransaction()
//                    .remove(currentFragment).show(fragment).commit();
//        }else {
            fragmentManager.beginTransaction()
                    .hide(currentFragment).show(fragment).commit();
//        }
        currentFragment = fragment;
        currentTickObject = (OnTickListener) fragment;
        wheelView.setOnTickListener((OnTickListener) fragment);
    }

    @Override
    public void onPlay() {
        if(playerService == null){
            Intent intent = new Intent(this,PlayerService.class);
            this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        }
        if (playerService.isPlaying() == true){
            Intent intent = new Intent(this,PlayerService.class);
            intent.putExtra("CMD",PlayerService.CMD_PAUSE);
            startService(intent);
        }else{
            //TODO
            //Add resume & pick play logic here.
        }
    }

    @Override
    public void onNext() {
        Log.e("mPod","onNext");
    }

    @Override
    public void onPrevious() {
        Log.e("mPod","onPrevious");
    }

    @Override
    public void onSelect(){
        SelectionDetail detail = currentTickObject.getCurrentSelection();
        switch (detail.getMenuType()){
            case SelectionDetail.MENU_TPYE_MAIN:
                switch ((String) detail.getData()){
                    case "Songs":
                        switchFragmentTo(songsList);
                        break;
                    case "Artists":
                        switchFragmentTo(artistsList);
                        break;
                    case "Albums":
                        switchFragmentTo(albumsList);
                        break;
                    case "Now Playing":
                        switchFragmentTo(nowPlayingFragment);
                        break;
                }
                break;
            case SelectionDetail.MENU_TYPE_ARTIST:
                SimpleListMenuAdapter adapter = new SimpleListMenuAdapter(this,
                        R.layout.item_simple_list_view,
                        MediaLibrary.getStaticInstance(this)
                                .getAlbumsByArtist((String) detail.getData()));
                SimpleListMenu menu = new SimpleListMenu();
                menu.setAdatper(adapter);
                adapter.setArrayListType(SimpleListMenuAdapter.SORT_TYPE_ALBUM);
                fragmentManager.beginTransaction()
                        .add(R.id.id_screen_fragment_container,menu).hide(menu).commit();
                switchFragmentTo(menu);
                break;
            case SelectionDetail.MENU_TYPE_ALBUM:
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
                startService(intent);

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
