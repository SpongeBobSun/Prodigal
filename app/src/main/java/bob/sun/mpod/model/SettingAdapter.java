package bob.sun.mpod.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bob.sun.mpod.R;
import bob.sun.mpod.utils.ResUtil;
import bob.sun.mpod.adapters.VHListItem;

/**
 * Created by bobsun on 15-5-22.
 */
public class SettingAdapter {
    private static SettingAdapter staticInstance;
    private Context context;
    private ArrayList<MenuMeta> menutItems;
    private int lastPosistion;
    private SettingsAdapter adapter;
    private SettingAdapter(Context context){
        this.context = context;
        initMenuItems();
    }
    public static SettingAdapter getStaticInstance(Context context){
        if (staticInstance == null)
            staticInstance = new SettingAdapter(context);
        return staticInstance;
    }
    private void initMenuItems(){
        ResUtil r = ResUtil.getInstance(null);
        menutItems = new ArrayList();
        menutItems.add(new MenuMeta(r.getLocalized(R.string.about),false, MenuMeta.MenuType.About));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.shuffle),false, MenuMeta.MenuType.ShuffleSettings));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.repeat), false, MenuMeta.MenuType.RepeatSettings));
    }

    public SettingsAdapter getAdapter(){
        if (adapter == null)
            adapter = new SettingsAdapter(this.context,R.layout.layout_simple_list_menu,menutItems);
        return adapter;
    }

    public void HighlightItem(int position){
        menutItems.get(lastPosistion).highlight = false;
        lastPosistion = position;
        menutItems.get(position).highlight = true;
        adapter.notifyDataSetChanged();
    }

    public class SettingsAdapter extends RecyclerView.Adapter<VHListItem> {
        ArrayList<MenuMeta> arrayList;

        public SettingsAdapter(Context context, int resource,ArrayList<MenuMeta> list) {
            super();
            this.arrayList = list;
        }

        @Override
        public VHListItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_list_view, parent, false);
            VHListItem ret = new VHListItem(view);
            return ret;
        }

        @Override
        public void onBindViewHolder(VHListItem holder, int position) {
            holder.configureWithString(arrayList.get(position).itemName, arrayList.get(position).highlight ? VHListItem.Status.ListItemHighlighted : VHListItem.Status.ListItemNormal);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public MenuMeta getItem(int position) {
            return arrayList.get(position);
        }
    }
}
