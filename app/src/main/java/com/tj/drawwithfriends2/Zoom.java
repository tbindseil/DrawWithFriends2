package com.tj.drawwithfriends2;

import android.util.Log;

/**
 * Created by TJ on 9/29/2018.
 */

public class Zoom {
    // random thought, scale could basically be an enum
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
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public int getCurrWidth() {
        return currWidth;
    }

    public void setCurrWidth(int currWidth) {
        this.currWidth = currWidth;
    }

    public int getCurrHeight() {
        return currHeight;
    }

    public void setCurrHeight(int currHeight) {
        this.currHeight = currHeight;
    }

    public int getUltimateWidth() {
        return ultimateWidth;
    }

    public int getUltimateHeight() {
        return ultimateHeight;
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
