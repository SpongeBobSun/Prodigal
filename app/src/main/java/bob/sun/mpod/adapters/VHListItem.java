package bob.sun.mpod.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bob.sun.mpod.R;

/**
 * Created by bob.sun on 24/01/2017.
 */

public class VHListItem extends RecyclerView.ViewHolder {

    public enum Status {
        ListItemNormal,
        ListItemHighlighted,
    }

    private View contentView;

    public VHListItem(View itemView) {
        super(itemView);
        contentView = itemView;
    }

    public void configureWithString(String str, Status status) {
        ((TextView) contentView.findViewById(R.id.id_itemlistview_textview)).setText(str);
            if(status == Status.ListItemHighlighted){
                contentView.setBackgroundColor(Color.LTGRAY);
            }else{
                contentView.setBackgroundColor(Color.TRANSPARENT);
            }
    }
}
