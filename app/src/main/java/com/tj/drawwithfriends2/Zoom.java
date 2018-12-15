package com.tj.drawwithfriends2;

import android.util.Log;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.subtractExact;

/**
 * Created by TJ on 9/29/2018.
 */

public class Zoom extends Configurable {
    private static final int DEFAULT_XOFFSET = 0;
    private static final int DEFAULT_YOFFSET = 0;
    private static final int DEFAULT_ZOOM_LEVEL = 0;

    private Configuration xOffsett;
    private int xOffset; // these are always in terms of bitmap pixels
    private int yOffset;
    private final int ultimateWidth; // dimensions of bitmap
    private final int ultimateHeight;
    private int zoomBoost; // width of pixel that is maximum without missing part of the picture, with zoom level of 0
    private int zoomLevel; // level 0 is zoomed out

    private double pixelsWide;
    private double pixelsTall;

    public Zoom(int ultimateWidth, int ultimateHeight) throws Exception {
        super();
        settings.put("xoff", new Configuration(Integer.toString(DEFAULT_XOFFSET)));
        settings.put("yoff", new Configuration(Integer.toString(DEFAULT_YOFFSET)));
        settings.put("zoomlevel", new Configuration(Integer.toString(DEFAULT_ZOOM_LEVEL)));

        this.ultimateWidth = ultimateWidth;
        this.ultimateHeight = ultimateHeight;

        // these are determined by the view using this
        // probably could use polymorphism to switch between painting and zoom view
        this.pixelsWide = -1;
        this.pixelsTall = -1;
    }

    public int getxOffset() {
        return xOffset;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
        boundChanges();
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
        boundChanges();
    }

    public void setWindowWidthAndHeight(int pixelsWide, int pixelsTall) {
        this.pixelsWide = pixelsWide;
        this.pixelsTall = pixelsTall;
        zoomBoost = Math.min((int)(this.pixelsWide / ultimateWidth),
                (int)(this.pixelsTall / ultimateHeight));
    }

    public int getUltimateWidth() {
        return ultimateWidth;
    }

    public int getUltimateHeight() {
        return ultimateHeight;
    }

    private void boundChanges() {
        // xOff = current x offset + distance moved bounded by 0 and width - currWidth
        // same for y
        int xOff = xOffset;
        int yOff = yOffset;
        xOff = max(xOff, 0);
        yOff = max(yOff, 0);
        xOff = min(xOff, ultimateWidth - (int)((float)ultimateWidth * ((float)zoomBoost / (float)getPixelWidth())));
        yOff = min(yOff, ultimateHeight - (int)((float)ultimateHeight * ((float)zoomBoost / (float)getPixelWidth())));
        xOffset = xOff;
        yOffset = yOff;
    }

    public int currXToUltimateX(int currX) {
        return currX + xOffset;
    }

    public int currYToUltimateY(int currY) {
        return currY + yOffset;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    // TODO stay centered when zooming
    public void setZoomLevel(int level) {
        zoomLevel = level;
    }

    public int getZoomBoost() { return zoomBoost; }

    public int getPixelWidth() {
        return zoomLevel + zoomBoost;
    }

    public void restore(int savedXOffset, int savedYOffset, int savedZoomLevel) {
        this.xOffset = savedXOffset;
        this.yOffset = savedYOffset;
        this.zoomLevel = savedZoomLevel;
    }

}
