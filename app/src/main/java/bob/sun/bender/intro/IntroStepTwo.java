package bob.sun.bender.intro;


import android.support.v4.app.Fragment;

import com.heinrichreimersoftware.materialintro.slide.Slide;

import bob.sun.bender.R;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class IntroStepTwo implements Slide {
    Fragment fragment;

    @Override
    public Fragment getFragment() {
        if (fragment == null)
            fragment = new AnimateIntroFragment().setStep(AnimateIntroFragment.IntroStep.Two);
        return fragment;
    }

    @Override
    public int getBackground() {
        return R.color.colorPrimary;
    }

    @Override
    public int getBackgroundDark() {
        return R.color.colorPrimary;
    }

    @Override
    public boolean canGoForward() {
        return true;
    }

    @Override
    public boolean canGoBackward() {
        return true;
    }
}
