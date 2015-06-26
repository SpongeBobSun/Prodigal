package bob.sun.mpod.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import bob.sun.mpod.MainActivity;
import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MenuAdapter;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.MenuMeta;
import bob.sun.mpod.service.PlayerService;

/**
 * Created by sunkuan on 2015/4/23.
 */
public class MainMenu extends Fragment implements OnTickListener {
    ListView listView;
    int currentItemIndex;
    ImageView imageView;
    LinearLayout nowPlayingPage;

    private static int menuIcons[] = {
            R.drawable.artist,
            R.drawable.album,
            R.drawable.cover_flow,
            R.drawable.songs,
            R.drawable.playlist,
            R.drawable.genre,
            R.drawable.shuffle,
            R.drawable.settings,
            R.drawable.songs
    };
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_main_menu,parent,false);
        listView = (ListView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(MenuAdapter.getStaticInstance(getActivity()).getMainMenuAdapter());
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        currentItemIndex = 0;
        MenuAdapter.getStaticInstance(null).HighlightItem(0);
        imageView = (ImageView) ret.findViewById(R.id.id_main_menu_image);
        nowPlayingPage = (LinearLayout) ret.findViewById(R.id.id_mainmenu_nowplaying);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),menuIcons[0]));
        return ret;
    }

    @Override
    public void onNextTick() {
        if(currentItemIndex >= listView.getAdapter().getCount()-1){
            currentItemIndex = listView.getAdapter().getCount()-1;
            return;
        }
        listView.setItemChecked(currentItemIndex, false);
        currentItemIndex+=1;
        listView.requestFocus();
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setItemChecked(currentItemIndex, true);
            }
        });
        if(currentItemIndex > listView.getLastVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
        if (currentItemIndex == listView.getAdapter().getCount() -1){
            imageView.setVisibility(View.GONE);
            nowPlayingPage.setVisibility(View.VISIBLE);
            PlayerService playerService = ((MainActivity) getActivity()).playerService;
            if (playerService == null || playerService.getCurrentSong() == null){
                ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_artist)).setText("Nobody");
                ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_title)).setText("Nothing");
                return;
            }
            ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_artist)).setText(playerService.getCurrentSong().getArtist());
            ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_title)).setText(playerService.getCurrentSong().getTitle());
            return;
        }
        imageView.destroyDrawingCache();
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),menuIcons[currentItemIndex]));
    }

    @Override
    public void onPreviousTick() {
        if(currentItemIndex < 1){
            return;
        }
        listView.setItemChecked(currentItemIndex, false);
        if (currentItemIndex == listView.getAdapter().getCount()-1){
            imageView.setVisibility(View.VISIBLE);
            nowPlayingPage.setVisibility(View.GONE);
        }
        currentItemIndex -= 1;
        listView.requestFocus();
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setItemChecked(currentItemIndex, true);
            }
        });
        if(currentItemIndex < listView.getFirstVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
        imageView.destroyDrawingCache();
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), menuIcons[currentItemIndex]));
    }

    @Override
    public SelectionDetail getCurrentSelection(){
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(ret.MENU_TPYE_MAIN);
        ret.setDataType(ret.DATA_TYPE_STRING);
        ret.setData(((MenuMeta) listView.getAdapter().getItem(currentItemIndex)).itemName);
        return ret;
    }
}
