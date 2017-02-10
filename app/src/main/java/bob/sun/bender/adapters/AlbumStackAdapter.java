package bob.sun.bender.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bob.sun.bender.R;
import bob.sun.bender.model.MediaLibrary;

/**
 * Created by bob.sun on 04/02/2017.
 */

public class AlbumStackAdapter extends BaseAdapter {
    private List<String> mData;

    public AlbumStackAdapter() {
        mData = MediaLibrary.getStaticInstance(null).getAllCoverUries();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album_card, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.stack_image);

        Picasso.with(convertView.getContext())
                .load(mData.get(position))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.album)
                .into(imageView);
        return convertView;
    }
}
