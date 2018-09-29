package com.tj.drawwithfriends2.Input;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Zoom;

/**
 * Created by TJ on 8/9/2018.
 */

public interface InputTool {
    void handleTouch(MotionEvent event);
    void setCurrZoom(Zoom currZoom);
    void setPixelsWide(double pixelsWide);
    void setPixelsTall(double pixelsTall);
    void setColor(int color);
    void setThickness(int thickness);
}