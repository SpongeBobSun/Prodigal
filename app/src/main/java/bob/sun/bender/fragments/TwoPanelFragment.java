package bob.sun.bender.fragments;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by bob.sun on 04/02/2017.
 */

public abstract class TwoPanelFragment extends Fragment {

    private int finishCount;

    @NonNull
    abstract public View getLeftPanel();
    @NonNull
    abstract public View getRightPanel();

    public void dismiss(final DismissCallback callback) {
        finishCount = 0;
        View left, right;
        left = getLeftPanel();
        right = getRightPanel();
        TranslateAnimation lAnim, rAnim;
        lAnim = new TranslateAnimation(0, 0 - left.getRight(), 0, 0);
        lAnim.setDuration(300);
        lAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                increaseAnimCount(callback);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rAnim = new TranslateAnimation(0, right.getRight(), 0, 0);
        rAnim.setDuration(300);
        rAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                increaseAnimCount(callback);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        left.startAnimation(lAnim);
        right.startAnimation(rAnim);
    }

    public void show(final DismissCallback callback) {
        finishCount = 0;
        View left, right;
        left = getLeftPanel();
        right = getRightPanel();
        TranslateAnimation lAnim, rAnim;
        lAnim = new TranslateAnimation(0 - left.getRight(), 0, 0, 0);
        lAnim.setDuration(300);
        lAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                increaseAnimCount(callback);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rAnim = new TranslateAnimation(right.getRight(), 0, 0, 0);
        rAnim.setDuration(300);
        rAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                increaseAnimCount(callback);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        left.startAnimation(lAnim);
        right.startAnimation(rAnim);
    }

    synchronized private void increaseAnimCount(DismissCallback callback) {
        if (finishCount == 1) {
            if (callback != null)
                callback.dismissed();
            return;
        }
        finishCount ++;
    }

    public interface DismissCallback {
        void dismissed();
    }
}
