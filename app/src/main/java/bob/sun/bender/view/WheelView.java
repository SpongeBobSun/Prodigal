package bob.sun.bender.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import bob.sun.bender.BuildConfig;
import bob.sun.bender.R;
import bob.sun.bender.controller.OnButtonListener;
import bob.sun.bender.controller.OnTickListener;
import bob.sun.bender.theme.Theme;
import bob.sun.bender.theme.ThemeManager;
import bob.sun.bender.utils.AppConstants;
import bob.sun.bender.utils.VibrateUtil;

/**
 * Created by bob.sun on 2015/4/23.
 */
public class WheelView extends View {


    private Point center;
    private Path viewBound;
    private int radiusOut,radiusIn;
    private Paint paintOut, paintIn, ripplePaint;
    private OnTickListener onTickListener;
    private float startDeg = Float.NaN;
    private OnButtonListener onButtonListener;
    private float buttonWidth, buttonHeight;
    private Theme theme;

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        center = new Point();
        paintIn = new Paint();
        paintOut = new Paint();
        ripplePaint = new Paint();
        paintIn.setStrokeCap(Paint.Cap.ROUND);
        paintOut.setAntiAlias(true);
        paintIn.setAntiAlias(true);
        paintOut.setShadowLayer(8f, 0.0f, 8f,
                Color.GRAY);

        buttonWidth = getResources().getDimensionPixelSize(R.dimen.button_width);
        buttonHeight = getResources().getDimensionPixelSize(R.dimen.button_height);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void loadTheme() {
        if (isInEditMode()) {
            this.theme = Theme.defaultTheme();
            this.invalidate();
            return;
        }
        this.theme = ThemeManager.getInstance(getContext().getApplicationContext()).loadCurrentTheme();
        paintOut.setColor(theme.getWheelColor());
        paintIn.setColor(Color.TRANSPARENT);
        paintIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        this.invalidate();
    }

    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec,measureHeightSpec);
        int measuredHeight = measureHeight(measureHeightSpec);
        int measuredWidth = measuredHeight;
        this.setMeasuredDimension(measuredWidth,measuredHeight);
        radiusOut = (measuredHeight - 20)/ 2;
        radiusIn = radiusOut/3;
        center.x = measuredWidth/2;
        center.y = measuredHeight/2;
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        viewBound = new Path();
        viewBound.addCircle(center.x, center.y, radiusOut, Path.Direction.CW);
    }

    @Override
    public void onDraw(Canvas canvas){
        if (this.theme == null) {
            loadTheme();
        }
        switch (theme.getShape()) {
            case AppConstants.ThemeShapeOval:
                drawCircle(canvas);
                break;
            case AppConstants.ThemeShapePolygon:
                drawPolygon(canvas, theme.sides);
                break;
            case AppConstants.ThemeShapeRect:
                drawRect(canvas);
                break;
            default:
                drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
        float radiusIn = this.radiusIn * theme.getInner();
        float radiusOut = this.radiusOut * theme.getOuter();
        canvas.drawCircle(center.x,center.y,radiusOut,paintOut);
        canvas.save();
        if (viewBound != null)
            canvas.clipPath(viewBound, Region.Op.REPLACE);

        canvas.drawCircle(center.x,center.y,radiusIn,paintIn);

        if (Build.VERSION.SDK_INT != 23)
            canvas.restore();
    }

    private void drawRect(Canvas canvas) {
        float radiusIn = this.radiusIn * theme.getInner();
        float radiusOut = this.radiusOut * theme.getOuter();
        canvas.drawRect(center.x - radiusOut, center.y - radiusOut, center.x + radiusOut, center.y + radiusOut, paintOut);
        canvas.save();
        if (viewBound != null) {
            canvas.clipPath(viewBound, Region.Op.REPLACE);
        }
        canvas.drawRect(center.x - radiusIn, center.y - radiusIn, center.x + radiusIn, center.y + radiusIn, paintIn);
        if (Build.VERSION.SDK_INT != 23) {
            canvas.restore();
        }
    }

    private void drawPolygon(Canvas canvas, int sides) {
        float radiusIn = this.radiusIn * theme.getInner();
        float radiusOut = this.radiusOut * theme.getOuter();
        Path pathIn = new Path();
        Path pathOut = new Path();
        if (sides <= 3) {
            sides = 4;
        }
        pathIn.moveTo(center.x, center.y - radiusIn);
        pathOut.moveTo(center.x, center.y - radiusOut);
        Paint tp = null;
        if (BuildConfig.DEBUG) {
            tp = new Paint();
            tp.setTextSize(50);
            tp.setColor(Color.BLACK);
        }
        for(int side = 0; side < sides; side++) {
            double theta = 2 * Math.PI / sides * side + Math.PI / 2;
            double xOut = center.x + radiusOut * Math.cos(theta);
            double yOut = center.y - radiusOut * Math.sin(theta);
            double xIn = center.x + radiusIn * Math.cos(theta);
            double yIn = center.y - radiusIn * Math.sin(theta);
            pathIn.lineTo((float) xIn, (float) yIn);
            pathOut.lineTo((float) xOut, (float) yOut);
            if (BuildConfig.DEBUG) {
                canvas.drawText("" + side, (float) xOut, (float)yOut, tp);
            }
        }
        pathOut.close();
        pathIn.close();
        canvas.drawPath(pathOut, paintOut);
        canvas.drawPath(pathIn, paintIn);
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
            result = getHeight();
            if (result == 0)
                result = specSize;
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
