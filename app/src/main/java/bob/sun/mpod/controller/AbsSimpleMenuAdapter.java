package bob.sun.mpod.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by sunkuan on 15/4/30.
 */
public abstract class AbsSimpleMenuAdapter {
    private ArrayList arrayList;

    public AbsSimpleMenuAdapter(){

    }
    public void setArrayList(ArrayList list){
        arrayList = list;
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
