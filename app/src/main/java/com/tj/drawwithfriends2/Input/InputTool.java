package com.tj.drawwithfriends2.Input;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;

/**
 * Created by TJ on 8/9/2018.
 */

public interface InputTool {
    public LayerDrawable handleTouch(MotionEvent event);
    public void setColor(int color);
}