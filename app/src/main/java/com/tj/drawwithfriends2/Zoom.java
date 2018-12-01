package com.tj.drawwithfriends2;

import android.util.Log;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by TJ on 9/29/2018.
 */

public class Zoom {
    // TODO move scale calculations all to in here
    private int xOffset; // these are always in terms of bitmap pixels
    private int yOffset;
    private int ultimateWidth; // dimensions of bitmap
    private int ultimateHeight;
    private int zoomBoost; // todo keep cleaning up this concept
    private int zoomLevel; // level = side lenth of one pixel from bitmap

    private double pixelsWide;
    private double pixelsTall;

    public Zoom(int xOffset, int yOffset, int ultimateWidth, int ultimateHeight, double pixelsWide, double pixelsTall, int zoomLevel) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.ultimateWidth = ultimateWidth;
        this.ultimateHeight = ultimateHeight;
        this.pixelsWide = pixelsWide;
        this.pixelsTall = pixelsTall;
        this.zoomLevel = zoomLevel;
        zoomBoost = -1;
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

    public void setPixelsWide(double pixelsWide) {
        this.pixelsWide = pixelsWide;
    }

    public double getPixelsWide() {
        return pixelsWide;
    }

    public void setPixelsTall(double pixelsTall) {
        this.pixelsTall = pixelsTall;
    }

    public double getPixelsTall() {
        return pixelsTall;
    }

    public int getUltimateWidth() {
        return ultimateWidth;
    }

    public void setUltimateWidth(int newUltimateWidth) {
        ultimateWidth = newUltimateWidth;
    }

    public int getUltimateHeight() {
        return ultimateHeight;
    }

    public void setUltimateHeight(int newUltimateHeight) {
        ultimateHeight = newUltimateHeight;
    }

    private void boundChanges() {
        // xOff = current x offset + distance moved bounded by 0 and width - currWidth
        // same for y
        int xOff = xOffset;
        int yOff = yOffset;
        xOff = max(xOff, 0);
        yOff = max(yOff, 0);
        xOff = min(xOff, ultimateWidth - (int)((float)ultimateWidth * ((float)zoomBoost / (float)(zoomLevel - 1 + zoomBoost))));
        yOff = min(yOff, ultimateHeight - (int)((float)ultimateHeight * ((float)zoomBoost / (float)(zoomLevel - 1 + zoomBoost))));
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
        return zoomLevel + zoomBoost - 1;
    }

    public Zoom deepCopy() {
        Zoom ret = new Zoom(xOffset, yOffset, ultimateWidth, ultimateHeight, pixelsWide, pixelsTall, zoomLevel);
        return ret;
    }

    public void deepCopy(Zoom toCopy) {
        xOffset = toCopy.getxOffset();
        yOffset = toCopy.getyOffset();
        ultimateWidth = toCopy.getUltimateWidth();
        ultimateHeight = toCopy.getUltimateHeight();
        pixelsWide = toCopy.getPixelsWide();
        pixelsTall = toCopy.getPixelsTall();
        zoomLevel = toCopy.getZoomLevel();
    }
}
