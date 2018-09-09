package com.tj.drawwithfriends2.Input;

/**
 * Created by TJ on 9/8/2018.
 */

public class Zoom {
    // note all values are measured as ints, ie width ints by height ints is in the picture currently
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
