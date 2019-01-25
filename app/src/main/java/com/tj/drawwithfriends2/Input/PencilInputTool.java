package com.tj.drawwithfriends2.Input;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Settings.Zoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/9/2018.
 */

public class PencilInputTool extends InputTool {
    // note last touch is in terms of ultimatecoords
    private Point lastTouch;

    public PencilInputTool(Zoom currZoom, int color, int thickness) {
        super(currZoom, color, thickness);
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

        /* this is actually going to be useful
        String debug = "historical x: ";
        for (int i = 0; i < event.getHistorySize(); i++) {
            debug += event.getHistoricalX(i);
            debug += ", ";
        }

        Log.e("debug", debug);
        Log.e("debug", "curr x is " + event.getX()); */

        // convert from absolute position on the screen to where in my grid the point is
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
            InputTransporter.getInstance().queueFillCircle(currTouch.x, currTouch.y, thickness, color);
        } else {
            InputTransporter.getInstance().queueFillLine(currTouch, lastTouch, color, thickness);
        }
        lastTouch = currTouch;

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            InputTransporter.getInstance().finishInput();
        }
    }
}