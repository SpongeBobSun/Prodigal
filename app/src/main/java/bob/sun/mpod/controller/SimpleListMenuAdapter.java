package bob.sun.mpod.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import bob.sun.mpod.R;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SongBean;
import dpl.bobsun.dummypicloader.DummyPicLoader;

/**
 * Created by sunkuan on 15/5/1.
 */
public class SimpleListMenuAdapter extends ArrayAdapter {
    public static final int SORT_TYPE_ARTIST = 0;
    public static final int SORT_TYPE_TITLE = 1;
    public static final int SORT_TYPE_ALBUM = 2;
    public static final int SORT_TYPE_GENRE = 3;


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
        ViewHolder holder;
        if(ret == null) {
            ret = ((Activity) appContext).getLayoutInflater()
                    .inflate(R.layout.item_simple_list_view, parent, false);
            holder = new ViewHolder();
            holder.content = getTiltleFromBean(list.get(position));

            if (this.type == SORT_TYPE_ALBUM){
                holder.imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
                ImageView imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
                imageView.setVisibility(View.VISIBLE);
                DummyPicLoader.getInstance(getContext()).resize(300,300).setDefaultImage(R.drawable.ic_album).loadImageFromUri(MediaLibrary.getStaticInstance(null).getCoverUriByAlbum(getTiltleFromBean(list.get(position))), imageView);
                ret.setTag(holder);
            }
        }else {
            if (ret.getTag() == null){
                holder = new ViewHolder();
                holder.content = getTiltleFromBean(list.get(position));
                if (this.type == SORT_TYPE_ALBUM){
                    holder.imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
                    ImageView imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
                    imageView.setVisibility(View.VISIBLE);
                    DummyPicLoader.getInstance(getContext()).resize(300, 300).setDefaultImage(R.drawable.ic_album).loadImageFromUri(MediaLibrary.getStaticInstance(null).getCoverUriByAlbum(getTiltleFromBean(list.get(position))),imageView);
                    ret.setTag(holder);
                }
            }else{
                holder = (ViewHolder) ret.getTag();
                if (holder.content != getTiltleFromBean((list.get(position)))) {
                    holder.content = getTiltleFromBean(list.get(position));
                    if (this.type == SORT_TYPE_ALBUM) {
                        ImageView imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
                        imageView.setVisibility(View.VISIBLE);
                        DummyPicLoader.getInstance(getContext()).resize(300, 300).setDefaultImage(R.drawable.ic_album).loadImageFromUri(MediaLibrary.getStaticInstance(null).getCoverUriByAlbum(getTiltleFromBean(list.get(position))), imageView);
                        ret.setTag(holder);
                    }
//                    holder.needLoadCover = true;
                }
//                ret.setTag(holder);
            }
        }
        TextView textView = (TextView) ret.findViewById(R.id.id_itemlistview_textview);
        textView.setText(holder.content);
        if (this.type == SORT_TYPE_ALBUM /* && holder.needLoadCover*/){
//            ImageView imageView = (ImageView) ret.findViewById(R.id.id_itemlistview_imageview);
//            imageView.setVisibility(View.VISIBLE);
//            DummyPicLoader.getInstance(getContext()).resize(imageView.getWidth(),imageView.getHeight()).loadImageFromUri(MediaLibrary.getStaticInstance(null).getCoverUriByAlbum(getTiltleFromBean(list.get(position))),imageView);
        }

        if(metaList.get(position).highlight){
            ret.setBackgroundColor(Color.GRAY);
        }else{
            ret.setBackgroundColor(Color.TRANSPARENT);
        }
        ret.setTag(holder);
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
        if (metaList.size() == 0){
            return;
        }
        metaList.get(lastPosistion).highlight = false;
        lastPosistion = position;
        metaList.get(position).highlight = true;
        this.notifyDataSetChanged();
    }
    private String getTiltleFromBean(Object bean){
        switch (type){
            case SORT_TYPE_ARTIST :
                return (String) bean;
            case SORT_TYPE_ALBUM :
                return (String) bean;
            case SORT_TYPE_TITLE :
                return ((SongBean) bean).getTitle();
            case SORT_TYPE_GENRE :
                return (String) bean;
            default:
                return "";
        }
    }

    class ViewHolder{
        String content;
        ImageView imageView;
//        boolean needLoadCover;
    }
    public int getType() {
        return type;
    }

    public ArrayList<SongBean> getList() {
        return list;
    }
}
