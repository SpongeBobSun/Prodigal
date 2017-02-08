package bob.sun.mpod.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.AlbumBean;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;

import static bob.sun.mpod.model.SelectionDetail.MENU_TYPE_ALBUM;

/**
 * Created by bob.sun on 06/02/2017.
 */

public class CoverflowFragment extends Fragment implements OnTickListener {

    private ViewPager pager;
    private CoverflowPagerAdapter pagerAdapter;
    private PagerContainer pagerContainer;
    private CoverFlow flow;
    private boolean resized;

    public CoverflowFragment() {
        resized = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        final View ret = inflater.inflate(R.layout.layout_coverflow, parent, false);
        pagerContainer = (PagerContainer) ret.findViewById(R.id.pager_container);
        pager = (ViewPager) ret.findViewById(R.id.view_pager);
        pagerAdapter = new CoverflowPagerAdapter();
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(3);
        flow = new CoverFlow.Builder()
                .with(pager)
                .pagerMargin(0)
                .scale(0.3f)
                .spaceSize(0f)
                .rotationY(30f)
                .build();
        pagerContainer.setOverlapEnabled(true);
        pager.post(new Runnable() {
            @Override public void run() {
                View view = (View) pager.getAdapter().instantiateItem(pager, 0);
                ViewCompat.setElevation(view, 8.0f);
            }
        });
        ret.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = pagerContainer.getHeight();
                if (height <= 0 || resized) {
                    return;
                }
                ViewGroup.LayoutParams lp = pager.getLayoutParams();
                int margin = getResources().getDimensionPixelSize(R.dimen.cover_flow_margin);
                lp.height = height - margin * 2;
                lp.width = lp.height;
                pager.setLayoutParams(lp);
                pagerAdapter.resized = true;
                pagerContainer.invalidate();
                pagerAdapter.notifyDataSetChanged();
                pager.post(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(pagerAdapter.getCount() / 2);
                    }
                });
                resized = true;
            }
        });
        return ret;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onNextTick() {
        int current = pager.getCurrentItem();
        if (!pagerAdapter.canGoNext(current)) {
            return;
        }
        current += 1;
        pager.setCurrentItem(current, true);
    }

    @Override
    public void onPreviousTick() {
        int current = pager.getCurrentItem();
        if (!pagerAdapter.canGoBack(current)) {
            return;
        }
        current -= 1;
        pager.setCurrentItem(current, true);
    }

    @Override
    public SelectionDetail getCurrentSelection() {
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(MENU_TYPE_ALBUM);
        ret.setData(pagerAdapter.getItem(pager.getCurrentItem()).getName());
        return ret;
    }

    class CoverflowPagerAdapter extends PagerAdapter {

        private ArrayList<AlbumBean> imgs;
        public boolean resized;

        public CoverflowPagerAdapter() {
            imgs = MediaLibrary.getStaticInstance(getContext()).getAllAlbumsWrapped();
            resized = false;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View ret = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_coverflow_img, null);
            if (imgs.size() == 0)
                return ret;
            ImageView img = (ImageView) ret.findViewById(R.id.cover_image);
            Picasso.with(container.getContext())
                    .load(Uri.parse(imgs.get(position).getCover()))
                    .placeholder(R.drawable.album)
                    .fit()
                    .centerInside()
                    .into(img);
            ((TextView) ret.findViewById(R.id.cover_text)).setText(imgs.get(position).getName());
            container.addView(ret);
            return ret;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return resized ? imgs.size() : 0;
        }

        public AlbumBean getItem(int position) {
            return imgs.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public boolean canGoNext(int current) {
            return current != imgs.size();
        }

        public boolean canGoBack(int current) {
            return current != 0;
        }
    }
}
