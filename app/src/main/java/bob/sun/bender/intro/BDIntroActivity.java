package bob.sun.bender.intro;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.OnNavigationBlockedListener;

import bob.sun.bender.utils.UserDefaults;

/**
 * Created by bob.sun on 13/02/2017.
 */

public class BDIntroActivity extends IntroActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        setFullscreen(false);
        super.onCreate(savedInstanceState);

        setButtonBackFunction(BUTTON_BACK_FUNCTION_BACK);

        addSlide(new IntroStepOne());
        addSlide(new IntroStepTwo());
        addSlide(new IntroStepThree());

        addOnNavigationBlockedListener(new OnNavigationBlockedListener() {
            @Override
            public void onNavigationBlocked(int position, int direction) {
                if (position == 2 && direction == DIRECTION_FORWARD) {
                    UserDefaults.getStaticInstance(BDIntroActivity.this.getApplicationContext()).introShown();
                    finish();
                }
            }
        });

    }
}
