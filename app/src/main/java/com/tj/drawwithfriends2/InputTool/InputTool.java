package com.tj.drawwithfriends2.InputTool;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Created by TJ on 8/9/2018.
 */

public interface InputTool {
    public Drawable handleTouch(MotionEvent event);
    public void setColor(int color);
}