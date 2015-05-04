package bob.sun.mpod.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import bob.sun.mpod.R;
import bob.sun.mpod.model.SongBean;

/**
 * Created by sunkuan on 15/5/1.
 */
public class SimpleListMenuAdapter extends ArrayAdapter {
    public static final int SORT_TYPE_ARTIST = 0;
    public static final int SORT_TYPE_TITLE = 1;
    public static final int SORT_TYPE_ALBUM = 2;


    private ArrayList<SongBean> list;
    private Context appContext;
    private int type;
    private int resource;
    private ArrayList<MenuMeta> metaList;
    private int lastPosistion;

    public SimpleListMenuAdapter(Context context, int resource,ArrayList list) {
        super(context, resource,list);
        appContext = context;
        this.resource = resource;
        this.list = list;
    }
    public void setArrayListType(int type){
        this.type = type;
        metaList = new ArrayList<>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            metaList.add(new MenuMeta(getTiltleFromBean(iterator.next()),false));
        }
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        View ret = convertView;
        if(ret == null) {
            ret = ((Activity) appContext).getLayoutInflater()
                    .inflate(R.layout.item_simple_list_view, parent, false);
        }
        TextView textView = (TextView) ret.findViewById(R.id.id_itemlistview_textview);
        textView.setText(getTiltleFromBean(list.get(position)));
        if(metaList.get(position).highlight){
            ret.setBackgroundColor(Color.GRAY);
        }else{
            ret.setBackgroundColor(Color.TRANSPARENT);
        }
        return ret;
    }
    class MenuMeta{
        public String itemName;
        public boolean highlight;
        public MenuMeta(String arg1,boolean arg2){
            itemName = arg1;
            highlight = arg2;
        }
    }
    public void HighlightItem(int position){
        metaList.get(lastPosistion).highlight = false;
        lastPosistion = position;
        metaList.get(position).highlight = true;
        this.notifyDataSetChanged();
    }
    private String getTiltleFromBean(Object bean){
        switch (type){
            case SORT_TYPE_ARTIST :
                return ((SongBean) bean).getArtist();
            case SORT_TYPE_ALBUM :
                return ((SongBean) bean).getAlbum();
            case SORT_TYPE_TITLE :
                return ((SongBean) bean).getTitle();
            default:
                return "";
        }
    }

    public int getType() {
        return type;
    }
}
