package bob.sun.bender.model;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bob.sun.bender.R;
import bob.sun.bender.adapters.VHSetting;
import bob.sun.bender.utils.ResUtil;
import bob.sun.bender.utils.UserDefaults;

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
        menutItems.add(new MenuMeta(r.getLocalized(R.string.shuffle),false, MenuMeta.MenuType.ShuffleSettings));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.repeat), false, MenuMeta.MenuType.RepeatSettings));
        menutItems.add(new MenuMeta("Themes", false, MenuMeta.MenuType.ThemeSettings));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.get_source_code),false, MenuMeta.MenuType.GetSourceCode));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.contact_us),false, MenuMeta.MenuType.ContactUs));
        menutItems.add(new MenuMeta(r.getLocalized(R.string.about),false, MenuMeta.MenuType.About));
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

    public class SettingsAdapter extends RecyclerView.Adapter<VHSetting> {
        ArrayList<MenuMeta> arrayList;
        ResUtil resUtil;
        UserDefaults ud;

        public SettingsAdapter(Context context, int resource,ArrayList<MenuMeta> list) {
            super();
            this.arrayList = list;
            resUtil = ResUtil.getInstance(context);
            ud = UserDefaults.getStaticInstance(context);
        }

        @Override
        public VHSetting onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settings, parent, false);
            VHSetting ret = new VHSetting(view);
            return ret;
        }

        @Override
        public void onBindViewHolder(VHSetting holder, int position) {
            MenuMeta item = arrayList.get(position);
            switch (item.menuType) {
                case About:
                    holder.configureWithStrings(item.itemName, null, item.highlight);
                    break;
                case ShuffleSettings:
                    holder.configureWithStrings(item.itemName, resUtil.getBoolString(ud.isShuffle()), item.highlight);
                    break;
                case RepeatSettings:
                    holder.configureWithStrings(item.itemName, resUtil.getSettingsString(ud.getRepeat()), item.highlight);
                    break;
                case ThemeSettings:
                    holder.configureWithStrings(item.itemName, null, item.highlight);
                    break;
                case GetSourceCode:
                    holder.configureWithStrings(item.itemName, null, item.highlight);
                    break;
                case ContactUs:
                    holder.configureWithStrings(item.itemName, null, item.highlight);
                    break;
            }
//            holder.configureWithString(arrayList.get(position).itemName, arrayList.get(position).highlight ? VHListItem.Status.ListItemHighlighted : VHListItem.Status.ListItemNormal);
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
