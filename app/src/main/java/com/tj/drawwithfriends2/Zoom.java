package com.tj.drawwithfriends2;

import android.util.Log;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by TJ on 9/29/2018.
 */

public class Zoom {
    // random thought, scale could basically be an enum
    // TODO move scale calculations all to in here
    private int xOffset;
    private int yOffset;
    private int currWidth;
    private int currHeight;
    private int ultimateWidth;
    private int ultimateHeight;

    public Zoom(int xOffset, int yOffset, int currWidth, int currHeight, int ultimateWidth, int ultimateHeight) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.currWidth = currWidth;
        this.currHeight = currHeight;
        this.ultimateWidth = ultimateWidth;
        this.ultimateHeight = ultimateHeight;

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

    public int getCurrWidth() {
        return currWidth;
    }

    public void setCurrWidth(int currWidth) {
        this.currWidth = currWidth;
        boundChanges();
    }

    public int getCurrHeight() {
        return currHeight;
    }

    public void setCurrHeight(int currHeight) {
        this.currHeight = currHeight;
        boundChanges();
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
        xOff = min(xOff, ultimateWidth - currWidth);
        yOff = min(yOff, ultimateHeight - currHeight);
        xOffset = xOff;
        yOffset = yOff;
    }

    public int getXScale() {
        // this should result in a no remainder division... I think...
        return ultimateWidth / currWidth;
        // i don't think thats the case anymore...
    }

    public int getYScale() {
        // this should result in a no remainder division... I think...
        return ultimateHeight / currHeight;
        // i don't think thats the case anymore...
    }

    public int currXToUltimateX(int currX) {
        return currX + xOffset;
    }

    public int currYToUltimateY(int currY) {
        return currY + yOffset;
    }

    public Zoom deepCopy() {
        Zoom ret = new Zoom(xOffset, yOffset, currWidth, currHeight, ultimateWidth, ultimateHeight);
        return ret;
    }
}
