package bob.sun.mpod.adapters;

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

import bob.sun.mpod.model.MenuMeta;

/**
 * Created by sunkuan on 15/4/25.
 */
public class MenuAdapter {
    private static MenuAdapter staticInstance;
    private Context appContext;
    private ArrayList<MenuMeta> mainMenuItems;
    private MainMenuAdapter mainMenuAdapter;
    private int lastPosistion;
    private MenuAdapter(Context context){
        appContext = context;
        initLists();
    }
    private void initLists(){
        mainMenuItems = new ArrayList<>();
        mainMenuItems.add(new MenuMeta("Artists",false, MenuMeta.MenuType.Artists));
        mainMenuItems.add(new MenuMeta("Albums",false, MenuMeta.MenuType.Albums));
        mainMenuItems.add(new MenuMeta("Cover Flow",false, MenuMeta.MenuType.Coverflow));
        mainMenuItems.add(new MenuMeta("Songs",false, MenuMeta.MenuType.Songs));
        mainMenuItems.add(new MenuMeta("Playlist",false, MenuMeta.MenuType.Playlist));
        mainMenuItems.add(new MenuMeta("Genres",false, MenuMeta.MenuType.Genres));
        mainMenuItems.add(new MenuMeta("Shuffle Songs",false, MenuMeta.MenuType.ShuffleSongs));
        mainMenuItems.add(new MenuMeta("Settings",false, MenuMeta.MenuType.Settings));
        mainMenuItems.add(new MenuMeta("Now Playing",false, MenuMeta.MenuType.NowPlaying));
    }
    public static MenuAdapter getStaticInstance(Context context){
        if (staticInstance == null){
            staticInstance = new MenuAdapter(context);
        }
        return staticInstance;
    }
    public RecyclerView.Adapter getMainMenuAdapter(){
        if(mainMenuAdapter == null)
            mainMenuAdapter = new MainMenuAdapter(mainMenuItems);
        return mainMenuAdapter;
    }

    public void HighlightItem(int position){
        mainMenuItems.get(lastPosistion).highlight = false;
        lastPosistion = position;
        mainMenuItems.get(position).highlight = true;
        mainMenuAdapter.notifyDataSetChanged();
    }



    public class MainMenuAdapter extends RecyclerView.Adapter<VHListItem>{

        ArrayList<MenuMeta> arrayList;
        Context context;


        public MainMenuAdapter(ArrayList<MenuMeta> list) {
            super();
            arrayList = list;
        }

        @Override
        public VHListItem onCreateViewHolder(ViewGroup parent, int viewType) {
            if (context == null)
                context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_simple_list_view, parent, false);
            VHListItem ret = new VHListItem(view);

            return ret;
        }

        @Override
        public void onBindViewHolder(VHListItem holder, int position) {
            MenuMeta item = arrayList.get(position);
            holder.configureWithString(item.itemName,
                    item.highlight ? VHListItem.Status.ListItemHighlighted : VHListItem.Status.ListItemNormal);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public MenuMeta getItem(int pos) {
            return arrayList.get(pos);
        }
    }
}
