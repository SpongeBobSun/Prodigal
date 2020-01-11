package com.heinrichreimersoftware.materialintro.app;


import androidx.fragment.app.Fragment;

public class SlideFragment extends Fragment {

    public boolean canGoForward() {
        return true;
    }

    public boolean canGoBackward() {
        return true;
    }

    public void updateNavigation() {
        if (getActivity() instanceof IntroActivity) {
            ((IntroActivity) getActivity()).lockSwipeIfNeeded();
        }
    }

    protected void nextSlide() {
        if (getActivity() instanceof IntroActivity) {
            ((IntroActivity) getActivity()).nextSlide();
        }
    }

    protected void previousSlide() {
        if (getActivity() instanceof IntroActivity) {
            ((IntroActivity) getActivity()).previousSlide();
        }
    }

}
