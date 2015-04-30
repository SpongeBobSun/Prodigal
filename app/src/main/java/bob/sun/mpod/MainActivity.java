package bob.sun.mpod;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.fragments.MainMenu;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.service.PlayerService;
import bob.sun.mpod.view.WheelView;


public class MainActivity extends ActionBarActivity implements OnButtonListener {
    private FragmentManager fragmentManager;
    private MainMenu mainMenu;
    private WheelView wheelView;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        mainMenu = (MainMenu) fragmentManager.findFragmentById(R.id.id_screen_fragment_container);
        if(mainMenu == null){
            mainMenu = new MainMenu();
            fragmentManager.beginTransaction()
                    .add(R.id.id_screen_fragment_container,mainMenu,"mainMenu").commit();
        }
        wheelView = (WheelView) findViewById(R.id.id_wheel_view);
        wheelView.setOnButtonListener(this);

        wheelView.setOnTickListener(mainMenu);

        initOnButtonListener();

        startService();

        //UT for MeidaLibrary
        ArrayList<SongBean> list = MediaLibrary.getStaticInstance(this).getAllSongs();
        for(SongBean bean : list){
            Log.e(bean.getTitle(),bean.getFileName());
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
        Log.e("mPod","onSelect");
    }
}
