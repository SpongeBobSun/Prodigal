package bob.sun.bender.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;

import bob.sun.bender.model.MediaLibrary;
import bob.sun.bender.model.SongBean;
import bob.sun.bender.theme.Theme;

import static bob.sun.bender.adapters.VHImageListItem.Status.ListItemHighlighted;
import static bob.sun.bender.adapters.VHImageListItem.Status.ListItemNormal;

/**
 * Created by sunkuan on 15/5/1.
 */
public class SimpleListMenuAdapter extends RecyclerView.Adapter<VHImageListItem> {
    public static final int SORT_TYPE_ARTIST = 0;
    public static final int SORT_TYPE_TITLE = 1;
    public static final int SORT_TYPE_ALBUM = 2;
    public static final int SORT_TYPE_GENRE = 3;
    public static final int SORT_TYPE_THEME = 4;


    private ArrayList list;
    private Context appContext;
    private int type;
    private int resource;
    private ArrayList<MenuMeta> metaList;
    private int lastPosistion;

    public SimpleListMenuAdapter(Context context, int resource,ArrayList list) {
        super();
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
    public VHImageListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if (appContext == null)
            appContext = parent.getContext();
        View view = LayoutInflater.from(appContext).inflate(resource, parent, false);
        VHImageListItem ret = new VHImageListItem(view);
        return ret;
    }

    @Override
    public void onBindViewHolder(VHImageListItem holder, int position) {
        Object bean = list.get(position);
        if (bean instanceof SongBean && type == SORT_TYPE_ALBUM) {
            holder.configure(getTiltleFromBean(bean),
                    MediaLibrary.getStaticInstance(null).getCoverUriByAlbum(getTiltleFromBean(list.get(position))),
                    metaList.get(position).highlight ? ListItemHighlighted : ListItemNormal);
        } else {
            if (type == SORT_TYPE_ALBUM) {
                holder.configure(getTiltleFromBean(bean),
                        MediaLibrary.getStaticInstance(null).getCoverUriByAlbum((String) bean),
                        metaList.get(position).highlight ? ListItemHighlighted : ListItemNormal);
            } else {
                holder.configure(getTiltleFromBean(bean),
                        null,
                        metaList.get(position).highlight ? ListItemHighlighted : ListItemNormal);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    class MenuMeta{
        public String itemName;
        public boolean highlight;
        public MenuMeta(String arg1,boolean arg2){
            itemName = arg1;
            highlight = arg2;
        }
    }
    public void highlightItem(int position){
        if (metaList.size() == 0){
            return;
        }
        metaList.get(lastPosistion).highlight = false;
        lastPosistion = position;
        metaList.get(position).highlight = true;
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
            case SORT_TYPE_THEME:
                return (String) bean;
        }
        return "";
    }

    public int getType() {
        return type;
    }

    public ArrayList<SongBean> getList() {
        return list;
    }
}
