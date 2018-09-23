package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.InputTransporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/9/2018.
 */

@TargetApi(23)
public class PencilInputTool implements InputTool {
    Point lastTouch;
    int color;
    int thickness;

    int width, height;

    public PencilInputTool(int width, int height) {
        lastTouch = null;
        this.color = Color.RED;
        this.thickness = 2;
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastTouch = null;
        }

        int filteredX = filterX((int)event.getX());
        int filteredY = filterY((int)event.getY());

        Point currTouch = new Point(filteredX, filteredY);

        Rect toAdd = null;
        double rotation = 0;
        if (lastTouch == null) {
            toAdd = new Rect(currTouch.x - thickness,
                    currTouch.y - thickness,
                    currTouch.x + thickness,
                    currTouch.y + thickness);
        } else {
            int deltaX = currTouch.x - lastTouch.x;
            int deltaY = currTouch.y - lastTouch.y;
            Point midPoint = new Point(currTouch.x + (deltaX / 2), currTouch.y + (deltaY / 2));
            int lineLenth = (int)Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
            toAdd = new Rect(midPoint.x - (lineLenth / 2), midPoint.y + thickness, midPoint.x + (lineLenth / 2), midPoint.y - thickness);
            if (deltaX != 0) {
                rotation = Math.toDegrees(Math.atan(deltaY / deltaX));
            } else {
                rotation = 90 * (deltaY > 0 ? 1 : -1);
            }
        }
        lastTouch = new Point(filteredX, filteredY);

        InputTransporter.getInstance().addRect(toAdd, color, rotation);

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
