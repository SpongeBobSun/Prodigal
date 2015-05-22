package bob.sun.mpod.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by sunkuan on 15/5/13.
 */
public class ProgressView extends View {
    private int progress;
    private int total;
    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private Paint outRectPaint;
    private Paint inRectPaint;
    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec,measureHeightSpec);
        int measuredWidth = measureWidth(measureWidthSpec);
        int measuredHeight = measureHeight(measureHeightSpec);
        this.setMeasuredDimension(measuredWidth,measuredHeight);
        outRectPaint = new Paint();
        inRectPaint = new Paint();
        outRectPaint.setColor(Color.rgb(0x00,0x96,0x88));
        inRectPaint.setColor(Color.rgb(0xcc, 0xcc, 0xcc));
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
    public void onDraw(Canvas canvas){
        canvas.drawRect(0, 0, getWidth(), getHeight(), outRectPaint);
        canvas.drawRect(0,0,getWidth() * ((float)progress / (float)total),getHeight(),inRectPaint);
    }

    public void onProcessChanged(int progress, int total){
        this.progress = progress;
        this.total = total;
        this.postInvalidate();
//        Log.e("Progress",""+progress);
    }
}
