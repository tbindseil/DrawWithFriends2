package com.tj.drawwithfriends2.Input;

import android.graphics.Point;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Settings.Zoom;

public class LineInputTool extends InputTool {
    enum State { INITIAL, ONE_POINT_DOWN };
    State state;

    Point first;
    Point second;

    public LineInputTool(Zoom currZoom, int color, int thickness) {
        super(currZoom, color, thickness);

        state = State.INITIAL;
        first = new Point();
        second = new Point();
    }


    // the ideal implementation essentially requires the ability to draw temporarily to the screen
    public void handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            // draw circle
            int currX = pixelXToCurrX(event.getX());
            int currY = pixelYToCurrY(event.getY());
            int ultimateX = currZoom.currXToUltimateX(currX);
            int ultimateY = currZoom.currYToUltimateY(currY);

            if (state == State.INITIAL) {
                first.x = ultimateX;
                first.y = ultimateY;

                state = State.ONE_POINT_DOWN;
                return;
            } else if (state == State.ONE_POINT_DOWN) {
                second.x = ultimateX;
                second.y = ultimateY;

                InputTransporter.getInstance().queueFillLine(second, first, color, thickness);

                state = State.INITIAL;
            }
        }
    }
}
