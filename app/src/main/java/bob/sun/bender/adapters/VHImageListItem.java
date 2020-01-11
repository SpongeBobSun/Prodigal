package bob.sun.bender.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bob.sun.bender.R;
import bob.sun.bender.theme.ThemeManager;

/**
 * Created by bob.sun on 24/01/2017.
 */

public class VHImageListItem extends RecyclerView.ViewHolder {

    ThemeManager themeManager;

    public enum Status {
        ListItemNormal,
        ListItemHighlighted,
    }

    private View contentView;
    private ImageView imageView;

    public VHImageListItem(View itemView) {
        super(itemView);
        contentView = itemView;
        imageView = (ImageView) contentView.findViewById(R.id.id_itemlistview_imageview);
        themeManager = ThemeManager.getInstance(null);
    }

    public void configure(String str, String imgUri, VHImageListItem.Status status) {
        ((TextView) contentView.findViewById(R.id.id_itemlistview_textview)).setText(str);
        TextView textView = (TextView) contentView.findViewById(R.id.id_itemlistview_textview);
        textView.setText(str);
        textView.setTextColor(themeManager.getCurrentTheme().getTextColor());
        if(status == VHImageListItem.Status.ListItemHighlighted){
            contentView.setBackgroundColor(themeManager.loadCurrentTheme().getItemColor());
        }else{
            contentView.setBackgroundColor(Color.TRANSPARENT);
        }
        if (imgUri != null && !imgUri.equalsIgnoreCase((String) imageView.getTag())) {
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(contentView.getContext())
                    .load(Uri.parse(imgUri))
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .fit().centerCrop()
                    .into(imageView);
            imageView.setTag(imgUri);
        } else if (imgUri == null){
            imageView.setImageResource(R.drawable.album);
        }

    }
}
