package bob.sun.mpod.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MenuAdapter;

/**
 * Created by sunkuan on 2015/4/23.
 */
public class MainMenu extends Fragment implements OnTickListener {
    ListView listView;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_main_menu,parent,false);
        listView = (ListView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(MenuAdapter.getStaticInstance(getActivity()).getMainMenuAdapter());
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        currentItemIndex = 0;
        return ret;
    }

    @Override
    public void onNextTick() {
        Log.e("index:",currentItemIndex+"");
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
//                listView.setItemChecked(currentItemIndex, true);
                listView.setSelection(currentItemIndex);
            }
        });
        if(currentItemIndex > listView.getLastVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
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
                listView.setSelection(currentItemIndex);
            }
        });
        if(currentItemIndex < listView.getFirstVisiblePosition())
            listView.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
    }

}
