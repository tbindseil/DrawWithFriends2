package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by TJ on 8/26/2018.
 */

// owned by project files

public class UltimatePixelArray {
    private int width, height;
    private File file;
    private Bitmap mostRecent;

    public UltimatePixelArray(int width, int height, String absolutePath) throws Exception {
        // TODO this is horrible, and will changge it it turns out
        // the file exists
        this.width = width;
        this.height = height;

        this.file = new File(absolutePath);
        try {
            file.createNewFile();
            file.setWritable(true);
        } catch (Exception e) {
            throw e;
        }
    }

    public Bitmap getBitmap() {
        return mostRecent;
    }

    public void init() {
        if (file.length() == 0) {
            create();
        } else {
            load();
        }
    }

    public void erase() {
        int[] pixelArray = new int[width * height];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = 0x00ffffff;
        }

        mostRecent = Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }

    private void create() {
        int[] pixelArray = new int[width * height];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = 0xffffffff;
        }

        mostRecent = Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }

    private void load() {
        mostRecent = BitmapFactory.decodeFile(file.getAbsolutePath());
        width = mostRecent.getWidth();
        height = mostRecent.getHeight();
    }

    private void write() {
        // compress and write to file
        try {
            FileOutputStream os = new FileOutputStream(file);
            mostRecent.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (Exception e) {
            Log.e("FillPixels", "exception with new image" + e.toString());
        }
    }

    void update(Input next) {
        // apply the edit
        mostRecent = next.imprintOnto(mostRecent);

        write();
    }

    void setAlpha() {
        mostRecent = mostRecent.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < mostRecent.getWidth(); x++) {
            for (int y = 0; y < mostRecent.getHeight(); y++) {
                int currPixel = mostRecent.getPixel(x, y);
                currPixel |= 0xff000000;
                mostRecent.setPixel(x, y, currPixel);
            }
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }
}
