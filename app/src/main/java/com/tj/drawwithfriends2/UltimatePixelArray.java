package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Created by TJ on 8/26/2018.
 */

// owned by project files
@TargetApi(26)
public class UltimatePixelArray {
    int width, height;
    File file;
    MappedByteBuffer[] buffs;

    public UltimatePixelArray(int width, int height, File file) throws Exception {
        this.width = width;
        this.height = height;

        this.file = file;
    }

    public void fillPixels(int[] pixelArrray) {
        if (pixelArrray.length < width * height) {
            Log.e("fillPixels", "array too small!");
            return;
        }

        try {
            buffs = new MappedByteBuffer[height];
            FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE);
            for (int i = 0; i < buffs.length; i++) {
                buffs[i] = channel.map(FileChannel.MapMode.READ_WRITE, i * width, width);
                buffs[i].load();
                IntBuffer toCopy = buffs[i].asIntBuffer();
                toCopy.get(pixelArrray, width * i, width);
            }
        } catch (Exception e) {
            Log.e("fillPixels", "exception: " + e.toString());
        }
    }

    // NOTE: assumes buffs is already mapped and the right dimensions
    // todo probably just replace with compress as png
    void update(Bitmap fillFrom) {
        try {
            for (int row = 0; row < height; row++) {
                IntBuffer copyTo = buffs[row].asIntBuffer();
                // Note: I'm not even gonna bother with hasArray stuff
                // just gonna use a small transfer array
                int[] toFill = new int[width];
                for (int col = 0; col < width; col++) {
                    toFill[col] = fillFrom.getPixel(row, col);
                }
                copyTo.put(toFill);
            }
            // turn mapped byte buffers to int buffers,
            // get underlying int array,
            // fill underlying int array via the get pixels method of Bitmap toWrite
        } catch (Exception e) {
            Log.e("update", "exception: " + e.toString());
            return;
        }
    }
}
