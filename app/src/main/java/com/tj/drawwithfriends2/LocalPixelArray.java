package com.tj.drawwithfriends2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.tj.drawwithfriends2.Input.Input;

/**
 * Created by TJ on 8/26/2018.
 */

public class LocalPixelArray extends PixelArray {
    int xOffset;
    int yOffset;

    int[] pixelArray;
    BitmapDrawable mostRecent;

    public LocalPixelArray(int width, int height, int xOffset, int yOffset,
                           UltimatePixelArray ultimatePixelArray) {
        super(width, height);
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        pixelArray = new int[width * height];
        ultimatePixelArray.fillPixels(pixelArray, xOffset, yOffset, width, height);

        loadMostRecent();
    }

    private void loadMostRecent() {
        //mostRecent = new BitmapDrawable(Bitmap.createBitmap());
    }

    @Override
    public void handleInput(Input next) {
// for each pixel in pixel array, check if in map! this is faster than for
    }
}
