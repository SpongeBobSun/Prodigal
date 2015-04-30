package bob.sun.mpod.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import bob.sun.mpod.R;

/**
 * Created by sunkuan on 15/4/30.
 */
public class SimpleMenuAdatper extends ArrayAdapter {
    private Context appContext;
    private int resource;
    private ArrayList<MenuMeta> list;
    public SimpleMenuAdatper(Context context, int resource,ArrayList list) {
        super(context, resource);
        appContext = context;
        this.resource = resource;
        this.list = new ArrayList<MenuMeta>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            list.add(new MenuMeta((String) iterator.next(),false));
        }
    }

    @Override
    public View getView(int posistion,View convertView,ViewGroup parent){
        View ret = convertView;
        if(ret == null) {
            ret = ((Activity) appContext).getLayoutInflater()
                    .inflate(R.layout.item_simple_list_view, parent, false);
        }
        TextView textView = (TextView) ret.findViewById(R.id.id_itemlistview_textview);
        textView.setText(list.get(posistion).itemName);
        return ret;
    }

    public void HighlightItem(int pos){

    }
    class MenuMeta{
        public String itemName;
        public boolean highlight;
        public MenuMeta(String arg1,boolean arg2){
            itemName = arg1;
            highlight = arg2;
        }
    }
}
