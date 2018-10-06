package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by TJ on 10/6/2018.
 */

public class ZoomImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
    private Zoom currZoom, saveZoom;
    private Context mContext;

    public ZoomImageView(Context context) {
        super(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Zoom startZoom, Context context, Bitmap toDraw) {
        currZoom = startZoom;
        saveZoom = startZoom.deepCopy();
        mContext = context;

        Drawable result = new BitmapDrawable(mContext.getResources(), toDraw);
        setImageDrawable(result);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // no anti aliasing
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);

        super.onDraw(canvas);

        // draw rect over zoomed portion
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);

        float scaleFactorX = getWidth() / currZoom.getUltimateWidth();
        float scaleFactorY = getHeight() / currZoom.getUltimateHeight();
        canvas.drawRect(currZoom.getxOffset() * scaleFactorX,
                currZoom.getyOffset() * scaleFactorY,
                currZoom.getCurrWidth() * scaleFactorX,
                currZoom.getCurrHeight() * scaleFactorY, p);

        // restore state
        canvas.setDrawFilter(oldDrawFilter);
    }

    public Zoom getCurrZoom() { return currZoom; }
    public Zoom getSaveZoom() { return saveZoom; }
}
