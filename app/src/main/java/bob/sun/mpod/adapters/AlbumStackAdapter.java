package bob.sun.mpod.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bob.sun.mpod.R;
import bob.sun.mpod.model.MediaLibrary;
import dpl.bobsun.dummypicloader.DummyPicLoader;

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
        DummyPicLoader.getInstance(convertView.getContext()).setDefaultImage(R.drawable.album).loadImageFromUri(mData.get(position),
                (ImageView) convertView.findViewById(R.id.stack_image));
        return convertView;
    }
}
