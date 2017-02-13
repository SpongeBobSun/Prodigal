package bob.sun.bender.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bob.sun.bender.R;

import static android.view.View.GONE;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class VHSetting extends RecyclerView.ViewHolder {
    View contentView;
    TextView titleView, valueView;

    public VHSetting(View itemView) {
        super(itemView);
        contentView = itemView;
        titleView = (TextView) contentView.findViewById(R.id.id_itemlistview_textview);
        valueView = (TextView) contentView.findViewById(R.id.id_itemlistview_settings);
    }

    public void configureWithStrings(String title, String value, boolean high) {
        if (value == null) {
            valueView.setVisibility(GONE);
        } else {
            valueView.setVisibility(View.VISIBLE);
            valueView.setText(value);
        }
        titleView.setText(title);
        contentView.setBackgroundColor(high ? Color.LTGRAY : Color.TRANSPARENT);
    }
}
