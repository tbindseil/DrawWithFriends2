package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Input.InputTool;

/**
 * Created by TJ on 9/29/2018.
 */

public class PaintingImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);

    private Context mContext;

    // todo make this private, that would involve
    // launching the image from java
    private Zoom currZoom;
    private int zoomBoost;
    private Bitmap bitmap;

    private InputTool currTool;

    public PaintingImageView(Context context) {
        super(context);
        construct();
    }

    public PaintingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public PaintingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setCurrZoom(Zoom newZoom) {
        currZoom = newZoom;
    }

    public void setBitmap(Bitmap ongoingBitmap) {
        bitmap = ongoingBitmap;
    }

    public void setInputTool(InputTool tool) {
        currTool = tool;
    }

    private void construct() {
        mContext = null;
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
            Log.e("PaintingImageView", "picture too big for screen!");
            assert(false);
        }
        zoomBoost = Math.min(getWidth() / currZoom.getUltimateWidth(),
                getHeight() / currZoom.getUltimateHeight());
    }

    public void setColor(int color) {
        currTool.setColor(color);
    }
    public void setThickness(int thickness) {
        if (currTool != null) {
            currTool.setThickness(thickness);
        }
        else {
            Log.e("setThickness", "null");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        currTool.handleTouch(motionEvent);
        updatePaintingImage();
        return true;
    }

    public void updatePaintingImage() {
        // got a weird exception saying that toDraw was null when scaling
        // got it again....
        Bitmap toDraw;
        try {
            toDraw = InputTransporter.getInstance().drawQueuedInputs();
        } catch (Exception e) {
            Log.e("updatePaintingImage", "exception: " + e.toString());
            Log.e("updatePaintingImage", "stacktrace:");
            e.printStackTrace();
            return;
        }
        BitmapDrawable result = new BitmapDrawable(mContext.getResources(), toDraw);
        setImageDrawable(result);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // no anti aliasing
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);

        canvas.save();

        canvas.scale(currZoom.getZoomLevel() + zoomBoost - 1,
                currZoom.getZoomLevel() + zoomBoost - 1);

        if (currZoom.getZoomLevel() == 1) {
            // center zoomed out picture

        } else {
            // deal with offsets
        }

        canvas.drawBitmap(bitmap, 0, 0, new Paint());

        // restore state
        canvas.restore();
        canvas.setDrawFilter(oldDrawFilter);
    }
}