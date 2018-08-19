package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/9/2018.
 */

@TargetApi(23)
public class PencilInputTool implements InputTool {
    PencilInput currentUpdate;
    Point lastTouch;
    int color;

    public PencilInputTool(int color) {
        currentUpdate = new PencilInput();
        lastTouch = null;
        this.color = color;
    }

    @Override
    public Input handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            currentUpdate = new PencilInput();
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
        currentUpdate.addToThis(ret, color);

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            return currentUpdate;
        } else {
            return null;
        }
    }

    public void setColor(int color) { this.color = color; }
}
