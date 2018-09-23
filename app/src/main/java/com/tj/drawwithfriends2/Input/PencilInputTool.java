package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
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

    public PencilInputTool(int width, int height) {
        this.color = Color.RED;
        this.thickness = 2;
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        int filteredX = filterX((int)event.getX());
        int filteredY = filterY((int)event.getY());

        InputTransporter.getInstance().addPoint(filteredX, filteredY, color);

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            InputTransporter.getInstance().finishInput();
        }
    }

    private int filterX(int x) {
        return x / width;
    }

    private int filterY(int y) {
        return y / height;
    }

    public void setColor(int color) { this.color = color; }

    public void setThickness(int thickness) { this.thickness = thickness; }
}