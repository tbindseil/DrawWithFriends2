package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.InputTransporter;
import com.tj.drawwithfriends2.Zoom;

/**
 * Created by TJ on 8/9/2018.
 */

public class PencilInputTool extends InputTool {
    // note last touch is in terms of ultimatecoords
    private Point lastTouch;

    public PencilInputTool(Zoom currZoom) {
        super(currZoom);
        this.color = Color.RED;
        this.thickness = 1;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastTouch = null;
        }

        // TODO: what to do with multiple pointers?
        if (event.getPointerCount() > 1) {
            lastTouch = null;
            InputTransporter.getInstance().finishInput();
            return;
        }

        // convert from absolute position on the screen to where in my grid the point is
        // TODO i think my scaling is off for some reason
        int currX = pixelXToCurrX(event.getX());
        int currY = pixelYToCurrY(event.getY());

        // check bounds, don't mark stuff thats not in the currently zoomed version of the painting
        if (currX < 0) {
            currX = 0;
        }
        if (currY < 0) {
            currY = 0;
        }

        // convert from currCoord to ultimateCoord
        int ultimateX = currZoom.currXToUltimateX(currX);
        int ultimateY = currZoom.currYToUltimateY(currY);
        Point currTouch = new Point(ultimateX, ultimateY);

        if (lastTouch == null) {
            InputTransporter.getInstance().fillCircle(currTouch.x, currTouch.y, thickness, color);
        } else {
            InputTransporter.getInstance().drawLine(currTouch, lastTouch, color, thickness);
        }
        lastTouch = currTouch;

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            InputTransporter.getInstance().finishInput();
        }
    }
}