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
        listView.getChildAt(currentItemIndex).setBackgroundColor(Color.TRANSPARENT);
        currentItemIndex+=1;
        listView.requestFocus();
        listView.setItemChecked(currentItemIndex, true);
        listView.setSelection(currentItemIndex);
        if(listView.getChildAt(currentItemIndex) != null){
            listView.getChildAt(currentItemIndex).setBackgroundColor(Color.GRAY);
            return;
        }
        listView.smoothScrollToPosition(currentItemIndex);
    }

    @Override
    public void onPreviousTick() {
        Log.e("index:",currentItemIndex+"");
        if(currentItemIndex < 1){
            return;
        }
        listView.setItemChecked(currentItemIndex, false);
        listView.getChildAt(currentItemIndex).setBackgroundColor(Color.TRANSPARENT);
        currentItemIndex -= 1;
        listView.requestFocus();
        listView.setItemChecked(currentItemIndex, true);
        listView.setSelection(currentItemIndex);
        listView.getChildAt(currentItemIndex).setBackgroundColor(Color.GRAY);
        listView.smoothScrollToPosition(currentItemIndex);
    }

    @Override
    public void onMenu() {

    }

    @Override
    public void onPlay() {

    }

    @Override
    public void onNext() {

    }

    @Override
    public void onPrevious() {

    }
}
