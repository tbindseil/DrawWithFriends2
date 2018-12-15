package com.tj.drawwithfriends2.Activities;

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

import com.tj.drawwithfriends2.Settings.Zoom;

/**
 * Created by TJ on 10/6/2018.
 */

public class ZoomImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
    private Zoom currZoom;
    private int savedXOffset, savedYOffset, savedZoomLevel;
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

        savedXOffset = currZoom.getxOffset();
        savedYOffset = currZoom.getyOffset();
        savedZoomLevel = currZoom.getZoomLevel();

        mContext = context;
        holdingZoomBox = false;

        bitmap = toDraw;

        setBackgroundColor(Color.BLACK);

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
        int xShift = (getWidth() - (currZoom.getUltimateWidth() * currZoom.getZoomBoost())) / 2;
        int yShift = (getHeight() - (currZoom.getUltimateHeight() * currZoom.getZoomBoost())) / 2;
        xShift = xShift / currZoom.getZoomBoost();
        yShift = yShift / currZoom.getZoomBoost();

        canvas.scale(currZoom.getZoomBoost(), currZoom.getZoomBoost());

        canvas.drawBitmap(bitmap, xShift, yShift, new Paint());

        if (currZoom.getZoomLevel() == 0) {
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
        float rectW = getWidth() / currZoom.getPixelWidth() + (2 * 2);
        float rectH = getHeight() / currZoom.getPixelWidth() + (2 * 2);

        float rectX = xShift + currZoom.getxOffset() - 2;
        float rectY = yShift + currZoom.getyOffset() - 2;

        // Note, middle of rect perimeter seems to be cutoff for shown
        // zoom area, i want it to be inner edge,
        // to fix this, shifted the x,y start by half of stroke width and enlarged
        // width and height by half of stroke length over two
        canvas.drawRect(rectX, rectY, rectX + rectW, rectY + rectH, p);

        // restore state
        canvas.setDrawFilter(oldDrawFilter);
    }

    public void setCurrZoom(Zoom initialZoom) {
        currZoom = initialZoom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        currZoom.setWindowWidthAndHeight(w, h);

        // on setting of width and height, we need
        // to know what level of scaling we need to reach
        // the base
        if (getWidth() < currZoom.getUltimateWidth() ||
                getHeight() < currZoom.getUltimateHeight()) {
            // todo handle this better
            Log.e("ZoomImageView", "picture too big for screen!");
            assert(false);
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int xShift = (getWidth() - (currZoom.getUltimateWidth() * currZoom.getZoomBoost())) / 2;
        int yShift = (getHeight() - (currZoom.getUltimateHeight() * currZoom.getZoomBoost())) / 2;

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            int screenXOffset = xShift + (currZoom.getZoomBoost() * currZoom.getxOffset());
            int screenYOffset = yShift + (currZoom.getZoomBoost() * currZoom.getyOffset());
            if (ev.getX() > screenXOffset &&
                    ev.getX() < screenXOffset + ((float)getWidth() * ((float)currZoom.getZoomBoost() / (float)(currZoom.getZoomBoost() + currZoom.getZoomLevel() + 1))) &&
                    ev.getY() > screenYOffset &&
                    ev.getY() < screenYOffset + ((float)getHeight() * ((float)currZoom.getZoomBoost() / (float)(currZoom.getZoomBoost() + currZoom.getZoomLevel() + 1)))) {
                holdingZoomBox = true;
                initialTouch = new Point((int)ev.getX(), (int)ev.getY());
                initialXOff = currZoom.getxOffset();
                initialYOff = currZoom.getyOffset();
            }
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (holdingZoomBox) {
                int newXOffset = (int) (initialXOff + ((ev.getX() - initialTouch.x) / currZoom.getZoomBoost()));
                int newYOffset = (int) (initialYOff + ((ev.getY() - initialTouch.y) / currZoom.getZoomBoost()));
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

    public void save() {
        // do nothing
    }

    public void cancel() {
        currZoom.restore(savedXOffset, savedYOffset, savedZoomLevel);
    }
}
