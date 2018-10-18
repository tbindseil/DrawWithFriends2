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
@TargetApi(26)
public class UltimatePixelArray {
    private int width, height;
    private File file;
    private Bitmap mostRecent;

    public UltimatePixelArray(int width, int height, String absolutePath) throws Exception {
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

    private void create() {
        int[] pixelArray = new int[width * height];
        for (int i = 0; i < pixelArray.length; i++) {
            pixelArray[i] = 0x00ffffff;
        }

        mostRecent = Bitmap.createBitmap(pixelArray, width, height, Bitmap.Config.ARGB_8888);
    }

    private void load() {
        mostRecent = BitmapFactory.decodeFile(file.getAbsolutePath());
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
}
