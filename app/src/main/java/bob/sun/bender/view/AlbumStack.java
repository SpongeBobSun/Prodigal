package bob.sun.bender.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import bob.sun.bender.adapters.AlbumStackAdapter;
import link.fls.swipestack.SwipeStack;

/**
 * Created by bob.sun on 04/02/2017.
 */

public class AlbumStack extends SwipeStack {

    private Handler handler;
    private boolean direction;
    private Runnable swiper;

    public AlbumStack(Context context) {
        super(context);
    }

    public AlbumStack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumStack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        this.setAdapter(new AlbumStackAdapter());
        direction = true;
        this.swiper = new Runnable() {
            @Override
            public void run() {
                if (direction) {
                    swipeTopViewToLeft();
                } else {
                    swipeTopViewToRight();
                }
                if (getCurrentPosition() >= getAdapter().getCount() - 1) {
                    resetStack();
                } else {
                    direction = !direction;
                }
                handler.postDelayed(this, 2000);
            }
        };
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        switch (visibility) {
            case View.GONE:
                stopAutoSwipe();
                break;
            case View.VISIBLE:
                startAutoSwipe();
                break;
        }
    }

    private void startAutoSwipe() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(swiper, 2000);
    }

    private void stopAutoSwipe() {
        handler.removeCallbacks(swiper);
    }
}
