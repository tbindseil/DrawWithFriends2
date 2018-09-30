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

    private ScaleGestureDetector mScaleDetector;

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
        mScaleDetector.setQuickScaleEnabled(false);
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
        mScaleDetector.onTouchEvent(motionEvent);
        currTool.handleTouch(motionEvent);
        updatePaintingImage();
        return true;
    }

    public void updatePaintingImage() {
        Bitmap toDraw = mProjectFiles.getBitmap();
        // got a weird exception saying that toDraw was null when scaling
        toDraw = InputTransporter.getInstance().produceBitmapToDraw(toDraw);
        Drawable result = new BitmapDrawable(mContext.getResources(), toDraw);
        setImageDrawable(result);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // no anti aliasing
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);

        canvas.save();
        canvas.scale(mProjectFiles.getCurrZoom().getXScale(), mProjectFiles.getCurrZoom().getYScale());

        super.onDraw(canvas);

        // restore state
        canvas.restore();
        canvas.setDrawFilter(oldDrawFilter);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // in progress questions
            Log.e("ScaleDetector", "in progress? " + (detector.isInProgress() ? "true" : "false"));
            Log.e("ScaleDetector", "quick scale enabled?" + (detector.isQuickScaleEnabled() ? "true" : "false"));

            // set currWidth and currHeight based off how zoomy we get
            int newCurrWidth = (int)(mProjectFiles.getCurrWidth() / detector.getScaleFactor());
            int newCurrHeight = (int)(mProjectFiles.getCurrHeight() / detector.getScaleFactor());
            mProjectFiles.setCurrWidth(Math.max(mProjectFiles.MIN_WIDTH, Math.min(newCurrWidth, mProjectFiles.getWidth())));
            mProjectFiles.setCurrHeight(Math.max(mProjectFiles.MIN_HEIGHT, Math.min(newCurrHeight, mProjectFiles.getHeight())));

            // record focus in pixel coordinates
            double pixelFocusX = detector.getFocusX();
            double pixelFocusY = detector.getFocusY();

            // convert to out current grid coordinates
            int currCoordX = currTool.pixelXToCurrX(pixelFocusX);
            int currCoordY = currTool.pixelYToCurrY(pixelFocusY);

            // set currZoom's x and y offset based off some cool math I'm about to do

            // ensure that we don't show area that's not part of the painting

            invalidate();
            return true;
        }
    }
}