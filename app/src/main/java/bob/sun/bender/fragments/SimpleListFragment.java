package bob.sun.bender.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bob.sun.bender.R;
import bob.sun.bender.adapters.VHImageListItem;
import bob.sun.bender.controller.OnTickListener;
import bob.sun.bender.adapters.SimpleListMenuAdapter;
import bob.sun.bender.model.SelectionDetail;

/**
 * Created by sunkuan on 15/4/30.
 */
public class SimpleListFragment extends Fragment implements OnTickListener {

    private RecyclerView listView;
    private SimpleListMenuAdapter adatper;
    int currentItemIndex;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent,
                             Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_simple_list_menu, parent, false);
        listView = (RecyclerView) ret.findViewById(R.id.id_list_view_main_menu);
        listView.setAdapter(adatper);
        listView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        currentItemIndex = 0;
        adatper.highlightItem(0);
        return ret;
    }

    public void setAdatper(SimpleListMenuAdapter adatper){
        this.adatper = adatper;
    }

    /**
     * Compromise for the playinglist fragment.
     */
    public SimpleListMenuAdapter getAdapter(){
        return adatper;
    }

    @Override
    public void onNextTick() {
        VHImageListItem holder;
        if(currentItemIndex >= listView.getAdapter().getItemCount()-1){
            currentItemIndex = listView.getAdapter().getItemCount()-1;
            holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex);
            adatper.onBindViewHolder(holder, currentItemIndex);
            return;
        }
        currentItemIndex+=1;
        adatper.highlightItem(currentItemIndex);
        holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex - 1);
        if (holder != null)
            adatper.onBindViewHolder(holder, currentItemIndex - 1);
        if(currentItemIndex > ((LinearLayoutManager) listView.getLayoutManager()).findLastCompletelyVisibleItemPosition()) {
            listView.getLayoutManager().scrollToPosition(currentItemIndex);
            return;
        }
        holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex);
        if (holder != null) {
            adatper.onBindViewHolder(holder, currentItemIndex);
        }
    }

    @Override
    public void onPreviousTick() {
        VHImageListItem holder;
        if(currentItemIndex < 1){
            holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex);
            adatper.onBindViewHolder(holder, currentItemIndex);
            return;
        }
        currentItemIndex -= 1;
        adatper.highlightItem(currentItemIndex);
        holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex + 1);
        if (holder != null)
            adatper.onBindViewHolder(holder, currentItemIndex + 1);
        if(currentItemIndex < ((LinearLayoutManager) listView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()) {
            listView.getLayoutManager().scrollToPosition(currentItemIndex);
            return;
        }
        holder = (VHImageListItem) listView.findViewHolderForAdapterPosition(currentItemIndex);
        if (holder != null) {
            adatper.onBindViewHolder(holder, currentItemIndex);
        }

    }

    @Override
    public SelectionDetail getCurrentSelection() {
        SelectionDetail ret = new SelectionDetail();
        if (adatper.getItemCount() == 0){
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
