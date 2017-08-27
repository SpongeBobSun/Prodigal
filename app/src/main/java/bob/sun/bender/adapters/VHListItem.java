package bob.sun.bender.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bob.sun.bender.R;
import bob.sun.bender.theme.Theme;
import bob.sun.bender.theme.ThemeManager;

/**
 * Created by bob.sun on 24/01/2017.
 */

public class VHListItem extends RecyclerView.ViewHolder {

    public enum Status {
        ListItemNormal,
        ListItemHighlighted,
    }

    private View contentView;
    ThemeManager themeManager;

    public VHListItem(View itemView) {
        super(itemView);
        contentView = itemView;
        themeManager = ThemeManager.getInstance(itemView.getContext());
    }

    public void configureWithString(String str, Status status) {
        TextView textView = (TextView) contentView.findViewById(R.id.id_itemlistview_textview);
        textView.setText(str);
        textView.setTextColor(themeManager.getCurrentTheme().getTextColor());
            if(status == Status.ListItemHighlighted){
                contentView.setBackgroundColor(
                        themeManager.getCurrentTheme().getItemColor());
            }else{
                contentView.setBackgroundColor(Color.TRANSPARENT);
            }
    }
}
