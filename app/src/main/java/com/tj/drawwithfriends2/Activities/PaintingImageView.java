package com.tj.drawwithfriends2.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Input.InputTool;
import com.tj.drawwithfriends2.Input.InputTransporter;
import com.tj.drawwithfriends2.Settings.Zoom;

/**
 * Created by TJ on 9/29/2018.
 */

public class PaintingImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);
    // todo make this private, that would involve
    // launching the image from java
    private Zoom currZoom;
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
        setBackgroundColor(Color.BLACK);
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
        return true;
    }

    public void updatePaintingImage() {
        // got a weird exception saying that toDraw was null when scaling
        // got it again....
        Bitmap toDraw;
        try {
            toDraw = InputTransporter.getInstance().drawQueuedInputs();
        } catch (Exception e) {
            //Log.e("updatePaintingImage", "exception: " + e.toString());
            //Log.e("updatePaintingImage", "stacktrace:");
            //e.printStackTrace();
            return;
        }
        bitmap = toDraw;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // no anti aliasing
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);

        canvas.save();

        canvas.scale(currZoom.getPixelWidth(), currZoom.getPixelWidth());

        int xShift = currZoom.getxOffset() * -1;
        int yShift = currZoom.getyOffset() * -1;

        //synchronized (bitmap) {
            canvas.drawBitmap(bitmap, xShift, yShift, new Paint());
        //}

        // restore state
        canvas.restore();
        canvas.setDrawFilter(oldDrawFilter);
    }
}