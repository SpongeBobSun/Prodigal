package bob.sun.bender.intro;

import android.support.v4.app.Fragment;

import com.heinrichreimersoftware.materialintro.slide.Slide;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class IntroStepThree implements Slide {
    @Override
    public Fragment getFragment() {
        return null;
    }

    @Override
    public int getBackground() {
        return 0;
    }

    @Override
    public int getBackgroundDark() {
        return 0;
    }

    @Override
    public boolean canGoForward() {
        return false;
    }

    @Override
    public boolean canGoBackward() {
        return false;
    }
}
