package com.tj.drawwithfriends2.Input;

import android.graphics.Color;
import android.graphics.Rect;

/**
 * Created by TJ on 9/1/2018.
 */

public class RotatedRect {
    public final Rect r;
    public final int c;
    public final double rotation;

    public RotatedRect(Rect r, int c, double rotation) {
        this.r = r;
        this.c = c;
        this.rotation = rotation;
    }
}
