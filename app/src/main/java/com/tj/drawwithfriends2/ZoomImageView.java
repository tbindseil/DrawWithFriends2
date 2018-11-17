package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by TJ on 10/6/2018.
 */

public class ZoomImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
    private Zoom currZoom, saveZoom;
    private Context mContext;
    private boolean holdingZoomBox;
    private Point lastTouch;

    public ZoomImageView(Context context) {
        super(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void launch(Zoom startZoom, Context context, Bitmap toDraw) {
        currZoom = startZoom;
        saveZoom = startZoom.deepCopy();
        mContext = context;
        holdingZoomBox = false;

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
        p.setStrokeWidth(5);

        float scaleFactorX = getWidth() / currZoom.getUltimateWidth();
        float scaleFactorY = getHeight() / currZoom.getUltimateHeight();
        float scaledXOff = currZoom.getxOffset() * scaleFactorX;
        float scaledYOff = currZoom.getyOffset() * scaleFactorY;
        canvas.drawRect(scaledXOff,
                scaledYOff,
                scaledXOff + currZoom.getCurrWidth() * scaleFactorX,
                scaledYOff + currZoom.getCurrHeight() * scaleFactorY, p);

        // restore state
        canvas.setDrawFilter(oldDrawFilter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float scaleFactorX = getWidth() / currZoom.getUltimateWidth();
        float scaleFactorY = getHeight() / currZoom.getUltimateHeight();
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (ev.getX() > currZoom.getxOffset() * scaleFactorX &&
                    ev.getX() < currZoom.getxOffset() * scaleFactorX + currZoom.getCurrWidth() * scaleFactorX &&
                    ev.getY() > currZoom.getyOffset() * scaleFactorY &&
                    ev.getY() < currZoom.getyOffset() * scaleFactorX + currZoom.getCurrHeight() * scaleFactorY) {
                holdingZoomBox = true;
                lastTouch = new Point((int)ev.getX(), (int)ev.getY());
            }
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (holdingZoomBox) {
                scaleFactorX = (float)currZoom.getUltimateWidth() / (float)getWidth();
                scaleFactorY = (float)currZoom.getUltimateHeight() / (float)getHeight();
                int xOff = currZoom.getxOffset();
                int yOff = currZoom.getyOffset();
                xOff = xOff + (int)((ev.getX() - lastTouch.x) * scaleFactorX);
                yOff = yOff + (int)((ev.getY() - lastTouch.y) * scaleFactorY);

                currZoom.setxOffset(xOff);
                currZoom.setyOffset(yOff);

                lastTouch = new Point((int)ev.getX(), (int)ev.getY());
                invalidate();
            }
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            holdingZoomBox = false;
        }

        return true;
    }

    public void save() {
        // do nothing
    }

    public void cancel() {
        currZoom.deepCopy(saveZoom);
    }
}
