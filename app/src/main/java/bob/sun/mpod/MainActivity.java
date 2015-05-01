package bob.sun.mpod;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.SimpleAdatperByTitle;
import bob.sun.mpod.fragments.MainMenu;
import bob.sun.mpod.fragments.SimpleListMenu;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.service.PlayerService;
import bob.sun.mpod.view.WheelView;


public class MainActivity extends ActionBarActivity implements OnButtonListener {
    private FragmentManager fragmentManager;
    private MainMenu mainMenu;
    private SimpleListMenu songsList;
    private SimpleListMenu artistsList;
    private SimpleListMenu albumsList;
    private WheelView wheelView;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    private OnTickListener currentTickObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

        wheelView = (WheelView) findViewById(R.id.id_wheel_view);
        wheelView.setOnButtonListener(this);
        wheelView.setOnTickListener(mainMenu);
        this.currentTickObject = mainMenu;

        initOnButtonListener();

        startService();

        MediaLibrary.getStaticInstance(this);

//        //UT for MeidaLibrary
//        ArrayList<SongBean> list = MediaLibrary.getStaticInstance(this).getSongsByAlbum("sdcard");
//        for(SongBean bean : list){
//            Log.e(bean.getArtist(),bean.getFileName());
////            Log.e("Albums",bean);
//        }

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
            songsList.setAdatper(new SimpleAdatperByTitle(this,R.layout.item_simple_list_view,MediaLibrary.getStaticInstance(this).getAllSongs(MediaLibrary.ORDER_BY_ARTIST)));
            fragmentManager.beginTransaction().add(R.id.id_screen_fragment_container,songsList,"songsList").hide(songsList).commit();
        }
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
                playerService = (PlayerService) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                playerService = null;
            }
        };
        Intent intent = new Intent(this,PlayerService.class);
        this.bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        startService();
    }
    @Override
    protected void onStop(){
        super.onPause();
        unbindService(serviceConnection);
    }
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        unbindService(serviceConnection);
//    }
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
    public void onMenu() {
        Log.e("mPod","onMenu");
    }

    @Override
    public void onPlay() {
        Log.e("mPod","onPlay");
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
                        fragmentManager.beginTransaction().hide(mainMenu).show(songsList).commit();
                        break;
                    case "Artist":

                        break;
                    case "Albums":

                        break;
                }
                break;
            case SelectionDetail.MENU_TYPE_ARTIST:
                break;
            case SelectionDetail.MENU_TYPE_ALBUM:
                break;
            case SelectionDetail.MENU_TYPE_SONGS:
                break;
            default:
                break;
        }
    }
}
