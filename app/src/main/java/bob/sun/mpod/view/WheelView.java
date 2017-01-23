package bob.sun.mpod.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import bob.sun.mpod.R;
import bob.sun.mpod.controller.OnButtonListener;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.utils.VibrateUtil;

/**
 * Created by bob.sun on 2015/4/23.
 */
public class WheelView extends View {
    private Point center;
    private int radiusOut,radiusIn;
    private Paint paintOut, paintIn;
    private OnTickListener onTickListener;
    private float startDeg = Float.NaN;
    private OnButtonListener onButtonListener;
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paintIn = new Paint();
        paintOut = new Paint();
        paintOut.setColor(getResources().getColor(R.color.colorAccent));
        paintIn.setColor(getResources().getColor(R.color.colorPrimary));
        paintOut.setAntiAlias(true);
        paintIn.setAntiAlias(true);

        paintOut.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);
//        paintIn.setShadowLayer(10.0f, 0.0f, -2.0f, 0xFF000000);
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
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onDraw(Canvas canvas){
        canvas.drawCircle(center.x,center.y,radiusOut,paintOut);
        canvas.drawCircle(center.x,center.y,radiusIn,paintIn);
    }

    private float xyToDegrees(float x, float y) {
        float distanceFromCenter = PointF.length((x - 0.5f), (y - 0.5f));
        if (distanceFromCenter < 0.15f
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

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX() / (center.x * 2);
                float y = event.getY() / (center.y * 2);

                startDeg = xyToDegrees(x, y);
//                Log.d("deg = ", "" + startDeg);
//                if (Float.isNaN(startDeg)) {
//                    return false;
//                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (!Float.isNaN(startDeg)) {
                    float currentDeg = xyToDegrees((event.getX() - (getWidth() - getHeight())/2) / ((float) getHeight()),
                            event.getY() / getHeight());

                    if (!Float.isNaN(currentDeg)) {
                        float degPerTick = 72f;
                        float deltaDeg = startDeg - currentDeg;
                        if(Math.abs(deltaDeg) < 72f){
                            return true;
                        }
                        int ticks = (int) (Math.signum(deltaDeg)
                                * Math.floor(Math.abs(deltaDeg) / degPerTick));
                        if(ticks == 1){
                            startDeg = currentDeg;
                            if(onTickListener !=null)
                                onTickListener.onNextTick();
                            VibrateUtil.getStaticInstance(null).TickVibrate();
                        }
                        if(ticks == -1){
                            startDeg = currentDeg;
                            if(onTickListener !=null)
                                onTickListener.onPreviousTick();
                            VibrateUtil.getStaticInstance(null).TickVibrate();
                        }
                    }
                    startDeg = currentDeg;
                    return true;
                } else {
                    return false;
                }

            case MotionEvent.ACTION_UP:
                if ((Math.pow(event.getX() - getWidth() / 2f,2) + Math.pow(event.getY() - getHeight() / 2f,2) <= radiusIn*radiusIn )){
                    if(onButtonListener !=null)
                        onButtonListener.onSelect();
                    return true;
                }
                //TODO
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setOnTickListener(OnTickListener listener){
        this.onTickListener = listener;
    }

    public void setOnButtonListener(OnButtonListener listener){
        this.onButtonListener = listener;
    }
}
