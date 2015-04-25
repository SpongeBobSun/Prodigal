package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MenuAdapter;

/**
 * Created by sunkuan on 2015/4/23.
 */
public class MainMenu extends Fragment implements OnTickListener {
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_main_menu,parent,false);
        listView = (ListView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(MenuAdapter.getStaticInstance(getActivity()).getMainMenuAdapter());
        return ret;
    }

    @Override
    public void onNextTick() {
        int pos = listView.getFirstVisiblePosition()+1;
        listView.smoothScrollToPosition(pos);
    }

    @Override
    public void onPreviousTick() {
        listView.smoothScrollToPosition(listView.getLastVisiblePosition()-1);
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
