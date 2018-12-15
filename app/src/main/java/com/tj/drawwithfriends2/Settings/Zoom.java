package com.tj.drawwithfriends2.Settings;

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

    private final ConfigurationInt xOffset; // these are always in terms of bitmap pixels
    private final ConfigurationInt yOffset;
    private final ConfigurationInt zoomLevel; // level 0 is zoomed out
    private final int ultimateWidth; // dimensions of bitmap
    private final int ultimateHeight;
    private int zoomBoost; // width of pixel that is maximum without missing part of the picture, with zoom level of 0

    private double pixelsWide;
    private double pixelsTall;

    public Zoom(int ultimateWidth, int ultimateHeight) throws Exception {
        super();
        xOffset = new ConfigurationInt(DEFAULT_XOFFSET);
        yOffset = new ConfigurationInt(DEFAULT_YOFFSET);
        zoomLevel = new ConfigurationInt(DEFAULT_ZOOM_LEVEL);
        settings.put("xoff", xOffset);
        settings.put("yoff", yOffset);
        settings.put("zoomlevel", zoomLevel);

        this.ultimateWidth = ultimateWidth;
        this.ultimateHeight = ultimateHeight;

        // these are determined by the view using this
        // probably could use polymorphism to switch between painting and zoom view
        this.pixelsWide = -1;
        this.pixelsTall = -1;
    }

    public int getxOffset() {
        return xOffset.getInt();
    }

    public void setxOffset(int xOffset) {
        this.xOffset.setInt(xOffset);
        boundChanges();
    }

    public int getyOffset() {
        return yOffset.getInt();
    }

    public void setyOffset(int yOffset) {
        this.yOffset.setInt(yOffset);
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
        int xOff = xOffset.getInt();
        int yOff = yOffset.getInt();
        xOff = max(xOff, 0);
        yOff = max(yOff, 0);
        xOff = min(xOff, ultimateWidth - (int)((float)ultimateWidth * ((float)zoomBoost / (float)getPixelWidth())));
        yOff = min(yOff, ultimateHeight - (int)((float)ultimateHeight * ((float)zoomBoost / (float)getPixelWidth())));
        xOffset.setInt(xOff);
        yOffset.setInt(yOff);
    }

    public int currXToUltimateX(int currX) {
        return currX + xOffset.getInt();
    }

    public int currYToUltimateY(int currY) {
        return currY + yOffset.getInt();
    }

    public int getZoomLevel() {
        return zoomLevel.getInt();
    }

    // TODO stay centered when zooming
    public void setZoomLevel(int level) {
        zoomLevel.setInt(level);
    }

    public int getZoomBoost() { return zoomBoost; }

    public int getPixelWidth() {
        return zoomLevel.getInt() + zoomBoost;
    }

    public void restore(int savedXOffset, int savedYOffset, int savedZoomLevel) {
        this.xOffset.setInt(savedXOffset);
        this.yOffset.setInt(savedYOffset);
        this.zoomLevel.setInt(savedZoomLevel);
    }
}
