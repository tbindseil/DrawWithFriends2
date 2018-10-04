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
import android.view.ScaleGestureDetector;

import com.tj.drawwithfriends2.Input.InputTool;

import java.nio.MappedByteBuffer;

/**
 * Created by TJ on 9/29/2018.
 */

public class PaintingImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);

    private Context mContext;

    private ProjectFiles mProjectFiles;

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

    public void setProjectFiles(ProjectFiles files) {
        mProjectFiles = files;
    }

    public void setInputTool(InputTool tool) {
        currTool = tool;
    }

    private void construct() {
        mContext = null;
    }

    public void notifyOfWidthAndHeight() {
        currTool.setPixelsWide(getWidth());
        currTool.setPixelsTall(getHeight());
    }

    public void setColor(int color) {
        currTool.setColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        currTool.handleTouch(motionEvent);
        updatePaintingImage();
        return true;
    }

    public void updatePaintingImage() {
        Bitmap toDraw = mProjectFiles.getBitmap();
        // got a weird exception saying that toDraw was null when scaling
        // got it again....
        try {
            toDraw = InputTransporter.getInstance().produceBitmapToDraw(toDraw);
        } catch (Exception e) {
            Log.e("updatePaintingImage", "exception: " + e.toString());
            Log.e("updatePaintingImage", "stacktrace:");
            e.printStackTrace();
            return;
        }
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

        // restore state
        canvas.setDrawFilter(oldDrawFilter);
    }
}