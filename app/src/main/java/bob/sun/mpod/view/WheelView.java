package bob.sun.mpod.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bob.sun on 2015/4/23.
 */
public class WheelView extends View implements GestureDetector.OnGestureListener {
    Point center;
    int radiusOut,radiusIn;
    Paint paintOut, paintIn;
    GestureDetector gestureDetector;
    private float startDeg = Float.NaN;
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paintIn = new Paint();
        paintOut = new Paint();
        paintOut.setColor(Color.GRAY);
        paintIn.setColor(Color.WHITE);
        paintOut.setAntiAlias(true);
        paintIn.setAntiAlias(true);
        gestureDetector = new GestureDetector(context,this);
    }

    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec,measureHeightSpec);
        int measuredWidth = measureWidth(measureWidthSpec);
        int measuredHeight = measureHeight(measureHeightSpec);
        this.setMeasuredDimension(measuredWidth,measuredHeight);
        radiusOut = (measuredHeight - 20)/ 2;
        radiusIn = radiusOut/3;
        center.x = measuredWidth/2;
        center.y = measuredHeight/2;
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawCircle(center.x,center.y,radiusOut,paintOut);
        canvas.drawCircle(center.x,center.y,radiusIn,paintIn);
    }

    private float xyToDegrees(float x, float y) {
        float distanceFromCenter = PointF.length((x - 0.5f), (y - 0.5f));
        if (distanceFromCenter < 0.1f
                || distanceFromCenter > 0.5f) { // ignore center and out of bounds events
            return Float.NaN;
        } else {
            return (float) Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
        }
    }


    private int measureWidth(int measureSpec) {
       int specMode = MeasureSpec.getMode(measureSpec);
       int specSize = MeasureSpec.getSize(measureSpec);
       int result = 0;
       if (specMode == MeasureSpec.AT_MOST) {
               result = getWidth();
           } else if (specMode == MeasureSpec.EXACTLY) {
               result = specSize;
           }
       return result;
    }
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onDown(MotionEvent event) {
        float x = event.getX() / ((float) getWidth());
        float y = event.getY() / ((float) getHeight());

        startDeg = xyToDegrees(x, y);
        Log.d("deg = ", "" + startDeg);
        if (! Float.isNaN(startDeg)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onShowPress(MotionEvent event) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent eventA, MotionEvent eventB, float v, float v2) {
        if (!Float.isNaN(startDeg)) {
            float currentDeg = xyToDegrees(eventB.getX() / getWidth(),
                    eventB.getY() / getHeight());

            if (!Float.isNaN(currentDeg)) {
                float degPerTick = 36f;
                float deltaDeg = startDeg - currentDeg;
                int ticks = (int) (Math.signum(deltaDeg)
                        * Math.floor(Math.abs(deltaDeg) / degPerTick));
                if(ticks == 1){
                    Log.e("Ticks","Next");
                }
                if(ticks == -1){
                    Log.e("Ticks","Previous");
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onFling(MotionEvent event, MotionEvent event2, float v, float v2) {
        return false;
    }
}
