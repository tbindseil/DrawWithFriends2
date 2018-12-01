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

/**
 * Created by TJ on 10/6/2018.
 */

public class ZoomImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
    private Zoom currZoom, saveZoom;
    private int zoomBoost;
    Bitmap bitmap;
    private Context mContext;
    private boolean holdingZoomBox;

    private Point initialTouch;
    private int initialXOff, initialYOff;

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

        bitmap = toDraw;

        setBackgroundColor(Color.YELLOW);

        Drawable result = new BitmapDrawable(mContext.getResources(), toDraw);
        setImageDrawable(result);
        invalidate();
    }

    // in the below method, we always draw the full
    // picture with the zoom rectangle in black
    // so,
    @Override
    protected void onDraw(Canvas canvas) {
        // no anti aliasing
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);
        // scale then shift? or shift then scale
        int xShift = (getWidth() - (currZoom.getUltimateWidth() * zoomBoost)) / 2;
        int yShift = (getHeight() - (currZoom.getUltimateHeight() * zoomBoost)) / 2;
        xShift = xShift / zoomBoost;
        yShift = yShift / zoomBoost;

        canvas.scale(zoomBoost, zoomBoost);

        //Paint p = new Paint();
        //p.setColor(Color.WHITE);
        //canvas.drawRect(xShift, yShift, xShift + bitmap.getWidth(), yShift + bitmap.getHeight(), p);

        canvas.drawBitmap(bitmap, xShift, yShift, new Paint());

        if (currZoom.getZoomLevel() == 1) {
            // fully zoomed out
            return;
        }

        // draw rect over zoomed portion
        Paint p = new Paint();
        if (holdingZoomBox) {
            p.setARGB(64, 0, 0, 255);
        } else {
            p.setARGB(64, 0, 255, 0);
        }
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);

        // trust it
        float rectW = getWidth() / (currZoom.getZoomLevel() + zoomBoost - 1) + (2 * 2);
        float rectH = getHeight() / (currZoom.getZoomLevel() + zoomBoost - 1) + (2 * 2);

        float rectX = xShift + currZoom.getxOffset() - 2;
        float rectY = yShift + currZoom.getyOffset() - 2;

        // Note, middle of rect perimeter seems to be cutoff for shown
        // zooom area, i want it to be inner edge,
        // to fix this, shifted the x,y start by half of stroke width and enlarged
        // width and height by half of stroke length over two
        canvas.drawRect(rectX, rectY, rectX + rectW, rectY + rectH, p);

        // restore state
        canvas.setDrawFilter(oldDrawFilter);
    }

    public void setCurrZoom(Zoom initialZoom) {
        currZoom = initialZoom;
    }

    public void notifyOfWidthAndHeight() {
        currZoom.setPixelsWide(getWidth());
        currZoom.setPixelsTall(getHeight());

        // on setting of width and height, we need
        // to know what level of scaling we need to reach
        // the base
        if (getWidth() < currZoom.getUltimateWidth() ||
                getHeight() < currZoom.getUltimateHeight()) {
            // todo handle this better
            Log.e("ZoomImageView", "picture too big for screen!");
            assert(false);
        }
        zoomBoost = Math.min(getWidth() / currZoom.getUltimateWidth(),
                getHeight() / currZoom.getUltimateHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int xShift = (getWidth() - (currZoom.getUltimateWidth() * zoomBoost)) / 2;
        int yShift = (getHeight() - (currZoom.getUltimateHeight() * zoomBoost)) / 2;

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            int screenXOffset = xShift + (zoomBoost * currZoom.getxOffset());
            int screenYOffset = yShift + (zoomBoost * currZoom.getyOffset());
            if (ev.getX() > screenXOffset &&
                    ev.getX() < screenXOffset + ((float)getWidth() * ((float)zoomBoost / (float)(zoomBoost + currZoom.getZoomLevel()))) &&
                    ev.getY() > screenYOffset &&
                    ev.getY() < screenYOffset + ((float)getHeight() * ((float)zoomBoost / (float)(zoomBoost + currZoom.getZoomLevel())))) {
                holdingZoomBox = true;
                initialTouch = new Point((int)ev.getX(), (int)ev.getY());
                initialXOff = currZoom.getxOffset();
                initialYOff = currZoom.getyOffset();
            }
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (holdingZoomBox) {
                int newXOffset = (int) (initialXOff + ((ev.getX() - initialTouch.x) / zoomBoost));
                int newYOffset = (int) (initialYOff + ((ev.getY() - initialTouch.y) / zoomBoost));
                currZoom.setxOffset(newXOffset);
                currZoom.setyOffset(newYOffset);
            }
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            holdingZoomBox = false;
        }
        invalidate();
        return true;
    }
/*
        float scaleFactorX = getWidth() / currZoom.getUltimateWidth();
        float scaleFactorY = getHeight() / currZoom.getUltimateHeight();
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (ev.getX() > currZoom.getxOffset() * scaleFactorX &&
                    ev.getX() < currZoom.getxOffset() * scaleFactorX + currZoom.getCurrWidth() * scaleFactorX &&
                    ev.getY() > currZoom.getyOffset() * scaleFactorY &&
                    ev.getY() < currZoom.getyOffset() * scaleFactorX + currZoom.getCurrHeight() * scaleFactorY) {
                holdingZoomBox = true;
                initialTouch = new Point((int)ev.getX(), (int)ev.getY());
                initialXOff = currZoom.getxOffset();
                initialYOff = currZoom.getyOffset();
            }
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (holdingZoomBox) {
                scaleFactorX = (float)currZoom.getUltimateWidth() / (float)getWidth();
                scaleFactorY = (float)currZoom.getUltimateHeight() / (float)getHeight();
                int xOff = initialXOff + (int)((ev.getX() - initialTouch.x) * scaleFactorX);
                int yOff = initialYOff + (int)((ev.getY() - initialTouch.y) * scaleFactorY);

                currZoom.setxOffset(xOff);
                currZoom.setyOffset(yOff);

                invalidate();
            }
        }

        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            holdingZoomBox = false;
            invalidate();
        }

        return true;
    }
    */

    public void save() {
        // do nothing
    }

    public void cancel() {
        currZoom.deepCopy(saveZoom);
    }
}
