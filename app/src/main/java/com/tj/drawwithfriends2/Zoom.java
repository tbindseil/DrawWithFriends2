package com.tj.drawwithfriends2;

import android.util.Log;

/**
 * Created by TJ on 9/29/2018.
 */

public class Zoom {
    public final int xOffset = 0;
    public final int yOffset = 0;
    public final int currWidth;
    public final int currHeight;

    public Zoom(int xOffset, int yOffset, int currWidth, int currHeight) {
        this.currWidth = currWidth;
        this.currHeight = currHeight;
    }

    public int currXToUltimateX(int currX) {
        return currX + xOffset;
    }

    public int currYToUltimateY(int currY) {
        return currY + yOffset;
    }
}
