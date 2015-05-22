package bob.sun.mpod.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bob.sun.mpod.R;

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
        menutItems = new ArrayList();
        menutItems.add(new MenuMeta("About",false));
        menutItems.add(new MenuMeta("Shuffle",false));
        menutItems.add(new MenuMeta("Repeat", false));
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

    public class SettingsAdapter extends ArrayAdapter{
        ArrayList<MenuMeta> arrayList;

        public SettingsAdapter(Context context, int resource,ArrayList<MenuMeta> list) {
            super(context, resource,list);
            this.arrayList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View ret = convertView;
            if(ret == null) {
                ret = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_simple_list_view, parent,false);
            }
            ((TextView) ret.findViewById(R.id.id_itemlistview_textview)).setText(arrayList.get(position).itemName);
            if(arrayList.get(position).highlight){
                ret.setBackgroundColor(Color.GRAY);
            }else{
                ret.setBackgroundColor(Color.TRANSPARENT);
            }
            return ret;
        }
    }
}
