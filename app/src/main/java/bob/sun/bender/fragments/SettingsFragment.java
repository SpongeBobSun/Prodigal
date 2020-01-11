package bob.sun.bender.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bob.sun.bender.R;
import bob.sun.bender.controller.OnTickListener;
import bob.sun.bender.model.SelectionDetail;
import bob.sun.bender.model.SettingAdapter;

/**
 * Created by bobsun on 15-5-22.
 */
public class SettingsFragment extends Fragment implements OnTickListener {

    private RecyclerView listView;
    private SettingAdapter adapter;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_simple_list_menu, parent, false);
        listView = (RecyclerView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(adapter.getAdapter());
        listView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        currentItemIndex = 0;
        adapter.HighlightItem(0);
        return ret;
    }

    public void setAdapter(SettingAdapter adapter){
        this.adapter = adapter;
    }

    public void reloadSettings() {
        adapter.getAdapter().notifyDataSetChanged();;
    }

    @Override
    public void onNextTick() {
        if(currentItemIndex >= listView.getAdapter().getItemCount()-1){
            currentItemIndex = listView.getAdapter().getItemCount()-1;
            return;
        }
        currentItemIndex+=1;
        listView.requestFocus();
        if(currentItemIndex > ((LinearLayoutManager)listView.getLayoutManager()).findLastCompletelyVisibleItemPosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adapter.HighlightItem(currentItemIndex);
    }

    @Override
    public void onPreviousTick() {
        if(currentItemIndex < 1){
            return;
        }
        currentItemIndex -= 1;
        listView.requestFocus();
        if(currentItemIndex < ((LinearLayoutManager)listView.getLayoutManager()).findFirstCompletelyVisibleItemPosition())
            listView.smoothScrollToPosition(currentItemIndex);
        adapter.HighlightItem(currentItemIndex);
    }

    @Override
    public SelectionDetail getCurrentSelection(){
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(ret.MENU_TYPE_SETTING);
        ret.setData(((SettingAdapter.SettingsAdapter) listView.getAdapter()).getItem(currentItemIndex).menuType);
        return ret;
    }
}
