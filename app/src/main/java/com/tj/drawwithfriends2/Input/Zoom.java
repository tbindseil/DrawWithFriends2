package com.tj.drawwithfriends2.Input;

/**
 * Created by TJ on 9/8/2018.
 */

public class Zoom {
    public final int xOffset, yOffset, width, height;

    public Zoom(int x, int y, int w, int h) {
        xOffset = x;
        yOffset = y;
        width = w;
        height = h;
    }

    public int getNumPixels() {
        return width * height;
    }

    public int getZoomedOffset(int x, int y) {


        return 0;
    }
}
