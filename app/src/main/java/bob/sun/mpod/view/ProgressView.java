package bob.sun.mpod.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
    private RectF outRect;
    private RectF inRect;
    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec,measureHeightSpec);
        int measuredWidth = measureWidth(measureWidthSpec);
        int measuredHeight = measureHeight(measureHeightSpec);
        this.setMeasuredDimension(measuredWidth,measuredHeight);
        outRectPaint = new Paint();
        inRectPaint = new Paint();
        outRectPaint.setAntiAlias(true);
        inRectPaint.setAntiAlias(true);
        outRectPaint.setColor(Color.rgb(0xcc, 0xcc, 0xcc));
        inRectPaint.setColor(Color.rgb(0xcc, 0xcc, 0xcc));
        outRectPaint.setStyle(Paint.Style.STROKE);
        outRectPaint.setStrokeWidth(2);
        outRect = new RectF(0,0,measuredWidth,measuredHeight);
        inRect = new RectF(0,0,0,measuredHeight);
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
        canvas.drawRoundRect(outRect, getWidth() / 2f, getWidth() / 2f, outRectPaint);
        inRect.right = getWidth() * ((float) progress / (float) total);
        canvas.drawRoundRect(inRect, getWidth() / 2f, getWidth() / 2f,inRectPaint);
    }

    public void onProcessChanged(int progress, int total){
        this.progress = progress;
        this.total = total;
        this.postInvalidate();
    }
}
