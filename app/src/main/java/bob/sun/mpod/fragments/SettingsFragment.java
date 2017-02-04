package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MenuMeta;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SettingAdapter;

/**
 * Created by bobsun on 15-5-22.
 */
public class SettingsFragment extends Fragment implements OnTickListener {

    private RecyclerView listView;
    private SettingAdapter adatper;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_simple_list_menu, parent, false);
        listView = (RecyclerView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(adatper.getAdapter());
        listView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        currentItemIndex = 0;
        adatper.HighlightItem(0);
        return ret;
    }

    public void setAdatper(SettingAdapter adatper){
        this.adatper = adatper;
    }

    @Override
    public void onNextTick() {
        Log.e("index:", currentItemIndex + "");
        if(currentItemIndex >= listView.getAdapter().getItemCount()-1){
            currentItemIndex = listView.getAdapter().getItemCount()-1;
            return;
        }
        currentItemIndex+=1;
        listView.requestFocus();
        if(currentItemIndex > ((LinearLayoutManager)listView.getLayoutManager()).findLastCompletelyVisibleItemPosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adatper.HighlightItem(currentItemIndex);
    }

    @Override
    public void onPreviousTick() {
        Log.e("index:",currentItemIndex+"");
        if(currentItemIndex < 1){
            return;
        }
        currentItemIndex -= 1;
        listView.requestFocus();
        if(currentItemIndex < ((LinearLayoutManager)listView.getLayoutManager()).findFirstCompletelyVisibleItemPosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adatper.HighlightItem(currentItemIndex);
    }

    @Override
    public SelectionDetail getCurrentSelection(){
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(ret.MENU_TYPE_SETTING);
        ret.setDataType(ret.DATA_TYPE_STRING);
        ret.setData(((SettingAdapter.SettingsAdapter) listView.getAdapter()).getItem(currentItemIndex).itemName);
        return ret;
    }
}
