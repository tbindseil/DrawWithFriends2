package com.tj.drawwithfriends2;

import com.tj.drawwithfriends2.Input.Input;

import java.io.File;
import java.util.concurrent.locks.Lock;

/**
 * Created by TJ on 8/26/2018.
 */

public abstract class PixelArray {
    private int pixelsWide;
    private int pixelsTall;
    private Lock pixelsLock;

    public PixelArray(int width, int height) {
        pixelsWide = width;
        pixelsTall = height;
    }

    public abstract void handleInput(Input next);
}
