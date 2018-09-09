package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.Zoom;

/**
 * Created by TJ on 8/26/2018.
 */

public class LocalPixelArray extends PixelArray {
    int xOffset;
    int yOffset;

    Bitmap mostRecent;
    Context context;

    public LocalPixelArray(int width, int height, int xOffset, int yOffset,
                           UltimatePixelArray ultimatePixelArray, Context context) {
        super(width, height);
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        int[] pixelArray = new int[width * height];
        ultimatePixelArray.fillPixels(pixelArray, new Zoom(xOffset, yOffset, width, height));
        this.context = context;

        mostRecent = Bitmap.createBitmap(pixelArray, getPixelsWide(), getPixelsTall(), Bitmap.Config.ARGB_8888);
    }

    public Zoom getZoom() {
        return new Zoom(xOffset, yOffset, getPixelsWide(), getPixelsTall());
    }

    /*public void fillPixels(int[] ret) {
        mostRecent.getPixels(ret, 0, 1, 0, 0, getPixelsWide(), getPixelsTall());
    }*/

    public Bitmap getBitmap() { return mostRecent; }

    public BitmapDrawable getBitmapDrawable() {
        return new BitmapDrawable(context.getResources(), mostRecent);
    }

    public void handleInput(Input next) {
        Canvas drawnOn = next.finalize(mostRecent);
        // bitmap is drawn
    }
}
