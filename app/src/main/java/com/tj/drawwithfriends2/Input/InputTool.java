package com.tj.drawwithfriends2.Input;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.Zoom;

import java.util.List;

/**
 * Created by TJ on 8/9/2018.
 */


    // TODO, move InputTool into the ownership of project files
    // this is because it will eventually be a setting and have to be persisted
    // also I think it will straighten out this currZoom ownership and mutablitiy
    // issue I am so hung up on rn
public abstract class InputTool {
    protected Zoom currZoom;

    protected int color;
    protected int thickness;

    public InputTool(Zoom currZoom, int color, int thickness) {
        this.currZoom = currZoom;
        this.color = color;
        this.thickness = thickness;
    }

    public abstract void handleTouch(MotionEvent event);

    public void setColor(int color) { this.color = color; }
    public void setThickness(int thickness) { this.thickness = thickness; }

    public int pixelXToCurrX(double x) {
        if (currZoom.getZoomBoost() < 0) {
            Log.e("filterX", "zoomBoost not set yet");
            return 0;
        }
        return (int) (x / (double)(currZoom.getZoomBoost() + currZoom.getZoomLevel() - 1));
    }

    public int pixelYToCurrY(double y) {
        if (currZoom.getZoomBoost() < 0) {
            Log.e("filterY", "zoomBoost not set yet");
            return 0;
        }
        return (int) (y / (double)(currZoom.getZoomBoost() + currZoom.getZoomLevel() - 1));
    }
}