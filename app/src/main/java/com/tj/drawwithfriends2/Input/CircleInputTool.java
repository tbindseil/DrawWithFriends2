package com.tj.drawwithfriends2.Input;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.InputTransporter;
import com.tj.drawwithfriends2.Zoom;

/**
 * Created by TJ on 10/17/2018.
 */

public class CircleInputTool extends InputTool {
    public CircleInputTool(Zoom currZoom) {
        super(currZoom);
        this.color = Color.RED;
        this.thickness = 10;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            // draw circle
            int currX = pixelXToCurrX(event.getX());
            int currY = pixelYToCurrY(event.getY());
            drawCircle(currX, currY);
        }
    }

    private void drawCircle(int x0, int y0) {
        int radius = thickness;

        int x = radius-1;
        int y = 0;
        int dx = 1;
        int dy = 1;
        int err = dx - (radius << 1);

        while (x >= y) {
            InputTransporter.getInstance().addPoint(x0 + x, y0 + y, color);
            InputTransporter.getInstance().addPoint(x0 + y, y0 + x, color);
            InputTransporter.getInstance().addPoint(x0 - y, y0 + x, color);
            InputTransporter.getInstance().addPoint(x0 - x, y0 + y, color);
            InputTransporter.getInstance().addPoint(x0 - x, y0 - y, color);
            InputTransporter.getInstance().addPoint(x0 - y, y0 - x, color);
            InputTransporter.getInstance().addPoint(x0 + y, y0 - x, color);
            InputTransporter.getInstance().addPoint(x0 + x, y0 - y, color);

            if (err <= 0) {
                y++;
                err += dy;
                dy += 2;
            }

            if (err > 0) {
                x--;
                dx += 2;
                err += dx - (radius << 1);
            }
        }
    }
}
