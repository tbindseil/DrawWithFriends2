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
        int filteredX = filterX((int)event.getX());
        int filteredY = filterY((int)event.getY());

        if (filteredX >= 0 && filteredX < width && filteredY >= 0 && filteredY < height) {
            InputTransporter.getInstance().addPoint(filteredX, filteredY, color);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            InputTransporter.getInstance().finishInput();
        }
    }

    private int filterX(int x) {
        if (maxX < 0) {
            Log.e("filterX", "maxX not set yet");
            return 0;
        }
        return (int)((width / maxX) * x);
    }

    private int filterY(int y) {
        if (maxY < 0) {
            Log.e("filterY", "maxY not set yet");
            return 0;
        }
        return (int)((height / maxY) * y);
    }

    public void setColor(int color) { this.color = color; }

    public void setThickness(int thickness) { this.thickness = thickness; }
}