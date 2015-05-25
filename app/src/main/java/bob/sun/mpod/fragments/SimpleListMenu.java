package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.controller.SimpleListMenuAdapter;
import bob.sun.mpod.model.MenuAdapter;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;

/**
 * Created by sunkuan on 15/4/30.
 */
public class SimpleListMenu extends Fragment implements OnTickListener {

    private ListView listView;
    private SimpleListMenuAdapter adatper;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent,
                             Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_simple_list_menu, parent, false);
        listView = (ListView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(adatper);
        currentItemIndex = 0;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adatper.HighlightItem(0);
        return ret;
    }

    public void setAdatper(SimpleListMenuAdapter adatper){
        this.adatper = adatper;
    }

    @Override
    public void onNextTick() {
//        Log.e("index:", currentItemIndex + "");
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
//                listView.setSelection(currentItemIndex);
            }
        });
        if(currentItemIndex > listView.getLastVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adatper.HighlightItem(currentItemIndex);
    }

    @Override
    public void onPreviousTick() {
//        Log.e("index:",currentItemIndex+"");
        if(currentItemIndex < 1){
            return;
        }
        listView.setItemChecked(currentItemIndex, false);
        currentItemIndex -= 1;
        listView.requestFocus();
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setItemChecked(currentItemIndex, true);
//                listView.setSelection(currentItemIndex);
            }
        });
        if(currentItemIndex < listView.getFirstVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adatper.HighlightItem(currentItemIndex);
    }

    @Override
    public SelectionDetail getCurrentSelection() {
        SelectionDetail ret = new SelectionDetail();
        if (adatper.getCount() == 0){
            return ret;
        }
        switch (adatper.getType()){
            case SimpleListMenuAdapter.SORT_TYPE_TITLE:
                ret.setMenuType(ret.MENU_TYPE_SONGS);
                ret.setDataType(ret.DATA_TYPE_SONG);
                ret.setData(adatper.getItem(currentItemIndex));
                ret.setPlaylist(adatper.getList());
                ret.setIndexOfList(currentItemIndex);
                break;
            case SimpleListMenuAdapter.SORT_TYPE_ARTIST:
                ret.setMenuType(ret.MENU_TYPE_ARTIST);
                ret.setData(ret.DATA_TYPE_STRING);
                ret.setData(adatper.getItem(currentItemIndex));
                break;
            case SimpleListMenuAdapter.SORT_TYPE_ALBUM:
                ret.setMenuType(ret.MENU_TYPE_ALBUM);
                ret.setData(ret.DATA_TYPE_STRING);
                ret.setData(adatper.getItem(currentItemIndex));
                break;
            case SimpleListMenuAdapter.SORT_TYPE_GENRE:
                ret.setMenuType(ret.MENU_TYPE_GENRES);
                ret.setData(adatper.getItem(currentItemIndex));
        }
        return ret;
    }
}
