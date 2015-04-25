package bob.sun.mpod.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by sunkuan on 15/4/25.
 */
public class MenuAdapter {
    private static MenuAdapter staticInstance;
    private Context appContext;
    private ArrayList<String> mainMenuItems;
    private ArrayAdapter mainMenuAdapter;
    private MenuAdapter(Context context){
        appContext = context;
        initLists();
    }
    private void initLists(){
        mainMenuItems = new ArrayList<>();
        mainMenuItems.add("Artists");
        mainMenuItems.add("Albums");
        mainMenuItems.add("Cover Flow");
        mainMenuItems.add("Songs");
        mainMenuItems.add("Playlist");
        mainMenuItems.add("Genres");
        mainMenuItems.add("Now Playing");
        mainMenuItems.add("Setting");
    }
    public static MenuAdapter getStaticInstance(Context context){
        if (staticInstance == null){
            staticInstance = new MenuAdapter(context);
        }
        return staticInstance;
    }
    public ArrayAdapter getMainMenuAdapter(){
        if(mainMenuAdapter == null)
            mainMenuAdapter = new ArrayAdapter(appContext,android.R.layout.simple_list_item_1,mainMenuItems);
        return mainMenuAdapter;
    }


    class mainMenuAdapter extends ArrayAdapter{

        public mainMenuAdapter(Context context, int resource) {
            super(context, resource);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View ret = null;
            return ret;
        }
    }
}
