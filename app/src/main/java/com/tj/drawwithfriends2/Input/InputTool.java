package com.tj.drawwithfriends2.Input;

import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Zoom;

/**
 * Created by TJ on 8/9/2018.
 */


    // TODO, move InputTool into the ownership of project files
    // this is because it will eventually be a setting and have to be persisted
    // also I think it will straighten out this currZoom ownership and mutablitiy
    // issue I am so hung up on rn
public abstract class InputTool {
    private double pixelsWide, pixelsTall;
    private Zoom currZoom;

    public InputTool(Zoom currZoom) {
        this.currZoom = currZoom;
    }

    public abstract void handleTouch(MotionEvent event);
    public abstract void setColor(int color);
    public abstract void setThickness(int thickness);

    public void setPixelsWide(double pixelsWide) { this.pixelsWide = pixelsWide; }
    public void setPixelsTall(double pixelsTall) { this.pixelsTall = pixelsTall; }

    public int pixelXToCurrX(double x) {
        if (pixelsWide < 0) {
            Log.e("filterX", "pixelsWide not set yet");
            return 0;
        }
        return (int) ((currZoom.getCurrWidth() / pixelsWide) * x);
    }

    public int pixelYToCurrY(double y) {
        if (pixelsTall < 0) {
            Log.e("filterY", "pixelsTall not set yet");
            return 0;
        }
        return (int) ((currZoom.getCurrHeight() / pixelsTall) * y);
    }
}