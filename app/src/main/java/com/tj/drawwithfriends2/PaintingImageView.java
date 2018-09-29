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
import android.view.View;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.InputTool;

/**
 * Created by TJ on 9/29/2018.
 */

public class PaintingImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);

    Context mContext;

    ProjectFiles mProjectFiles;

    private InputTool currTool;

    private ScaleGestureDetector mScaleDetector;
    float mScaleFactor;

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
        mScaleDetector = new ScaleGestureDetector(mContext, new PaintingImageView.ScaleListener());
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

    public void setMaxXY() {
        currTool.setMaxXY(getWidth(), getHeight());
    }

    public void setColor(int color) {
        currTool.setColor(color);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleDetector.onTouchEvent(motionEvent);
        currTool.handleTouch(motionEvent);
        updatePaintingImage();
        return true;
    }

    public void updatePaintingImage() {
        Bitmap toDraw = mProjectFiles.getBitmap();
        toDraw = InputTransporter.getInstance().produceBitmapToDraw(toDraw);
        Drawable result = new BitmapDrawable(mContext.getResources(), toDraw);
        setImageDrawable(result);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);
        super.onDraw(canvas);
        canvas.setDrawFilter(oldDrawFilter);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            Log.e("ScaleDetector", "mSaleFactor set to " + mScaleFactor);

            // invalidate();
            return true;
        }
    }
}