package com.tj.drawwithfriends2.Settings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by TJ on 8/26/2018.
 */

public class UltimatePixelArray {
    private int width, height;
    private File file;

    // after initial load or create, access must be synchronized because
    // points are added by ui thread and the bitmap is compressed to file by bg thread
    private Bitmap mostRecent;

    public UltimatePixelArray(int width, int height, File file) throws Exception {
        this.width = width;
        this.height = height;
        this.file = file;

        create(width, height);

        SetDimensions();
    }

    public UltimatePixelArray(File file) throws Exception {
        this.file = file;

        load();

        SetDimensions();
    }

    private void SetDimensions() {
        width = mostRecent.getWidth();
        height = mostRecent.getHeight();
    }

    public Bitmap getBitmap() {
        return mostRecent;
    }

    public void erase() {
        int[] pixelArray = new int[width * height];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = 0xffffffff;
        }

        synchronized (mostRecent) {
            mostRecent = Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
        }
    }

    private void create(int width, int height) throws Exception {
        try {
            file.createNewFile();
            file.setWritable(true);
        } catch (Exception e) {
            throw e;
        }

        int[] pixelArray = new int[width * height];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = 0xffffffff;
        }

        mostRecent = Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }

    private void load() {
        mostRecent = BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public void write() {
        synchronized (mostRecent) {
            // compress and write to file
            try {
                FileOutputStream os = new FileOutputStream(file);
                mostRecent.compress(Bitmap.CompressFormat.PNG, 100, os);
            } catch (Exception e) {
                Log.e("FillPixels", "exception with new image" + e.toString());
            }
        }
    }

    public void update(Input next) {
        synchronized (mostRecent) {
            mostRecent = next.imprintOnto(mostRecent);
        }

        //write();
    }

    public void delete() {
        file.delete();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
