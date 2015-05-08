package bob.sun.mpod.skinlayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by sunkuan on 15/5/7.
 */
public class SkinLayerUp extends FrameLayout {
    public SkinLayerUp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLUE);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        return false;
    }
}
