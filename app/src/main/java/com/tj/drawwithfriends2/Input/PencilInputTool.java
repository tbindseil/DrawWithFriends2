package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.InputTransporter;

/**
 * Created by TJ on 8/9/2018.
 */

@TargetApi(23)
public class PencilInputTool implements InputTool {
    Point lastTouch;
    int color;
    int thickness;

    int width, height;
    double maxX, maxY;

    public PencilInputTool(int width, int height) {
        this.color = Color.RED;
        this.thickness = 2;
        this.width = width;
        this.height = height;
        this.maxX = -1;
        this.maxY = -1;
    }

    public void setMaxXY(double maxX, double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
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

        int filteredX = filterX((int) event.getX());
        int filteredY = filterY((int) event.getY());
        Point currTouch = new Point(filteredX, filteredY);

        if (filteredX < 0) {
            filteredX = 0;
        }
        if (filteredX >= width) {
            filteredX = width;
        }
        if (filteredY < 0) {
            filteredY = 0;
        }
        if (filteredY >= height) {
            filteredY = height;
        }

        if (lastTouch == null) {
            InputTransporter.getInstance().addPoint(filteredX, filteredY, color);
        } else {
            plotLine(currTouch);
        }

        lastTouch = currTouch;

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            InputTransporter.getInstance().finishInput();
        }
    }

    // thanks wikipedia and Jack Bresenham

    private void plotLineLow(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int D = 2 * dy - dx;
        int y = y0;

        for (int x = x0; x < x1; x++) {
            InputTransporter.getInstance().addPoint(x, y, color);
            if (D > 0) {
                y = y + yi;
                D = D - 2 * dx;
            }
            D = D + 2 * dy;
        }
    }

    private void plotLineHigh(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int D = 2 * dx - dy;
        int x = x0;

        for (int y = y0; y < y1; y++) {
            InputTransporter.getInstance().addPoint(x, y, color);
            if (D > 0)
                x = x + xi;
            D = D - 2 * dy;
            D = D + 2 * dx;
        }
    }

    private void plotLine(Point currPoint) {
        int x0 = lastTouch.x;
        int y0 = lastTouch.y;
        int x1 = currPoint.x;
        int y1 = currPoint.y;

        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1) {
                plotLineLow(x1, y1, x0, y0);
            } else {
                plotLineLow(x0, y0, x1, y1);
            }
        } else {
            if (y0 > y1) {
                plotLineHigh(x1, y1, x0, y0);
            } else {
                plotLineHigh(x0, y0, x1, y1);
            }
        }
    }

    private int filterX(int x) {
        if (maxX < 0) {
            Log.e("filterX", "maxX not set yet");
            return 0;
        }
        return (int) ((width / maxX) * x);
    }

    private int filterY(int y) {
        if (maxY < 0) {
            Log.e("filterY", "maxY not set yet");
            return 0;
        }
        return (int) ((height / maxY) * y);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}