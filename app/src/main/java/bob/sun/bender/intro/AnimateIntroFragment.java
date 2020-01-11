package bob.sun.bender.intro;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bob.sun.bender.R;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class AnimateIntroFragment extends Fragment {

    public enum IntroStep {
        Two,
        Three,
    }

    private IntroStep step;
    private View rotate, tap, hintTwo, hintThree;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.layout_intro_anim, container, false);
        rotate = ret.findViewById(R.id.id_image_rotate);
        tap = ret.findViewById(R.id.id_image_tap);
        hintTwo = ret.findViewById(R.id.intro_text_step_two);
        hintThree = ret.findViewById(R.id.intro_text_step_three);
        if (step == IntroStep.Two) {
            rotate.setVisibility(View.VISIBLE);
            tap.setVisibility(View.GONE);
        } else {
            rotate.setVisibility(View.GONE);
            tap.setVisibility(View.VISIBLE);
        }
        startAnim();
        showHint();
        return ret;
    }

    public AnimateIntroFragment setStep(IntroStep step) {
        this.step = step;
        return this;
    }

    private void showHint() {
        switch (step) {
            case Three:
                hintThree.setVisibility(View.VISIBLE);
                hintTwo.setVisibility(View.GONE);
                break;
            case Two:
                hintThree.setVisibility(View.GONE);
                hintTwo.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startAnim() {
        if (step != IntroStep.Two)
            return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(rotate, "rotation", 0f, 360f);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
}
