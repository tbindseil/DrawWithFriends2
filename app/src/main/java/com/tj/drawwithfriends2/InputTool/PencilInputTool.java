package com.tj.drawwithfriends2.InputTool;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/9/2018.
 */

public class PencilInputTool implements InputTool {
    Point lastTouch;
    int color;

    public PencilInputTool(int color) {
        lastTouch = null;
        this.color = color;
    }

    @Override
    public Drawable handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastTouch = null;
        }

        List<Point> touchPoints = new ArrayList<>();
        if (lastTouch != null) {
            touchPoints.add(new Point(lastTouch.x, lastTouch.y));
        }
        lastTouch = new Point((int)event.getX(), (int)event.getY());

        // note deep copy is important, lasttouch is mutable
        touchPoints.add(new Point((int)event.getX(), (int)event.getY()));
        Point[] ret = new Point[touchPoints.size()];
        touchPoints.toArray(ret);
        return new PencilInput(ret, color);
    }

    public void setColor(int color) { this.color = color; }
}
