package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private ListView listView;
    private SettingAdapter adatper;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_simple_list_menu, parent, false);
        listView = (ListView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(adatper.getAdapter());
        currentItemIndex = 0;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adatper.HighlightItem(0);
        return ret;
    }

    public void setAdatper(SettingAdapter adatper){
        this.adatper = adatper;
    }

    @Override
    public void onNextTick() {
        Log.e("index:", currentItemIndex + "");
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
        Log.e("index:",currentItemIndex+"");
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
    public SelectionDetail getCurrentSelection(){
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(ret.MENU_TYPE_SETTING);
        ret.setDataType(ret.DATA_TYPE_STRING);
        ret.setData(((MenuMeta) listView.getAdapter().getItem(currentItemIndex)).itemName);
        return ret;
    }
}
