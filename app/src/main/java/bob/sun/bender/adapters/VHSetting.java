package bob.sun.bender.adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bob.sun.bender.R;
import bob.sun.bender.theme.ThemeManager;

import static android.view.View.GONE;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class VHSetting extends RecyclerView.ViewHolder {
    View contentView;
    TextView titleView, valueView;
    ThemeManager themeManager;

    public VHSetting(View itemView) {
        super(itemView);
        contentView = itemView;
        titleView = (TextView) contentView.findViewById(R.id.id_itemlistview_textview);
        valueView = (TextView) contentView.findViewById(R.id.id_itemlistview_settings);
        themeManager = ThemeManager.getInstance(itemView.getContext());
    }

    public void configureWithStrings(String title, String value, boolean high) {
        if (value == null) {
            valueView.setVisibility(GONE);
        } else {
            valueView.setVisibility(View.VISIBLE);
            valueView.setText(value);
        }
        titleView.setTextColor(themeManager.getCurrentTheme().getTextColor());
        titleView.setText(title);
        contentView.setBackgroundColor(high ? themeManager.getCurrentTheme().getItemColor()
                : Color.TRANSPARENT);
    }
}
