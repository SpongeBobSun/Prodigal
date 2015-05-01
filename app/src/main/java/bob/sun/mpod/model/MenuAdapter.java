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
        mainMenuItems.add(new MenuMeta("Artists",false));
        mainMenuItems.add(new MenuMeta("Albums",false));
        mainMenuItems.add(new MenuMeta("Cover Flow",false));
        mainMenuItems.add(new MenuMeta("Songs",false));
        mainMenuItems.add(new MenuMeta("Playlist",false));
        mainMenuItems.add(new MenuMeta("Genres",false));
        mainMenuItems.add(new MenuMeta("Now Playing",false));
        mainMenuItems.add(new MenuMeta("Setting",false));
    }
    public static MenuAdapter getStaticInstance(Context context){
        if (staticInstance == null){
            staticInstance = new MenuAdapter(context);
        }
        return staticInstance;
    }
    public ArrayAdapter getMainMenuAdapter(){
        if(mainMenuAdapter == null)
            mainMenuAdapter = new MainMenuAdapter(appContext,R.layout.item_simple_list_view,mainMenuItems);
        return mainMenuAdapter;
    }

    public void HighlightItem(int position){
        mainMenuItems.get(lastPosistion).highlight = false;
        lastPosistion = position;
        mainMenuItems.get(position).highlight = true;
        mainMenuAdapter.notifyDataSetChanged();
    }

    public class MenuMeta{
        public String itemName;
        public boolean highlight;
        public MenuMeta(String arg1,boolean arg2){
            itemName = arg1;
            highlight = arg2;
        }
    }

    class MainMenuAdapter extends ArrayAdapter{
        ArrayList<MenuMeta> arrayList;
        int resource;
        public MainMenuAdapter(Context context, int resource, ArrayList<MenuMeta> list) {
            super(context, resource,list);
            this.resource = resource;
            arrayList = list;
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
