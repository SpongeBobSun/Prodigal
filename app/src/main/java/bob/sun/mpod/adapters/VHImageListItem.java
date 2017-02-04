package bob.sun.mpod.adapters;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bob.sun.mpod.R;
import dpl.bobsun.dummypicloader.DummyPicLoader;

/**
 * Created by bob.sun on 24/01/2017.
 */

public class VHImageListItem extends RecyclerView.ViewHolder {

    public enum Status {
        ListItemNormal,
        ListItemHighlighted,
    }

    private View contentView;

    public VHImageListItem(View itemView) {
        super(itemView);
        contentView = itemView;
    }

    public void configure(String str, String imgUri, VHImageListItem.Status status) {
        ((TextView) contentView.findViewById(R.id.id_itemlistview_textview)).setText(str);
        if(status == VHImageListItem.Status.ListItemHighlighted){
            contentView.setBackgroundColor(Color.LTGRAY);
        }else{
            contentView.setBackgroundColor(Color.TRANSPARENT);
        }
        ImageView imageView = (ImageView) contentView.findViewById(R.id.id_itemlistview_imageview);
        if (imgUri != null) {
            imageView.setVisibility(View.VISIBLE);
            DummyPicLoader.getInstance(contentView.getContext())
//                    .resize(imageView.getWidth(), imageView.getHeight())
                    .loadImageFromUri(imgUri, imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

    }
}
