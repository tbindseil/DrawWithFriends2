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

        canvas.save();
        canvas.scale(mProjectFiles.getCurrZoom().getXScale(), mProjectFiles.getCurrZoom().getYScale());
        canvas.translate(mProjectFiles.getXOffset(), mProjectFiles.getYOffset());

        super.onDraw(canvas);

        // restore state
        canvas.restore();
        canvas.setDrawFilter(oldDrawFilter);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // note, I'm gonna need to do some bad assery here for zoomout it looks like,
            // probably just cause 4 / .9, .8, .7 ... doesn't grow very fast? Maybe not tho
            // set currWidth and currHeight based off how zoomy we get
            int newCurrWidth = (int)(mProjectFiles.getCurrWidth() / detector.getScaleFactor());
            int newCurrHeight = (int)(mProjectFiles.getCurrHeight() / detector.getScaleFactor());

            // limit zoomyness
            newCurrWidth = Math.max(mProjectFiles.MIN_WIDTH, Math.min(newCurrWidth, mProjectFiles.getWidth()));
            newCurrHeight = Math.max(mProjectFiles.MIN_HEIGHT, Math.min(newCurrHeight, mProjectFiles.getHeight()));

            // set zoomyness, note this is before we convert pixelXToCurrX etc because
            // convert our focal point to the NEW coordinates
            mProjectFiles.setCurrWidth(newCurrWidth);
            mProjectFiles.setCurrHeight(newCurrHeight);

            // record focus in pixel coordinates
            double pixelFocusX = detector.getFocusX();
            double pixelFocusY = detector.getFocusY();

            // convert to out current grid coordinates and then to the ultimate grid
            int currCoordX = currTool.pixelXToCurrX(pixelFocusX);
            int currCoordY = currTool.pixelYToCurrY(pixelFocusY);
            int ultimateCoordX = mProjectFiles.getCurrZoom().currXToUltimateX(currCoordX);
            int ultimateCoordY = mProjectFiles.getCurrZoom().currYToUltimateY(currCoordY);

            // where the focal point was needs to be the middle of the offset.
            // this requires that we know the width and height, which we computed above
            // now we take that width and height and shift it over until currCoordX is
            // half of the newCurrWidth and the currCoordY is half of newCurrHeight
            int newXOffset = ultimateCoordX - (newCurrWidth / 2);
            int newYOffset = ultimateCoordY - (newCurrHeight / 2);
            mProjectFiles.setXOffset(-1 * newXOffset); // why -1 * ?????????
            mProjectFiles.setYOffset(-1 * newYOffset);

            // TODO ensure that we don't show area that's not part of the painting

            invalidate();
            return true;
        }
    }
}