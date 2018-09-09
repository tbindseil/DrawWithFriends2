package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Zoom;

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
public class UltimatePixelArray extends PixelArray {
    File file;
    MappedByteBuffer[] buffs;

    public UltimatePixelArray(int width, int height, File projectRoot) throws Exception {
        super(width, height);

        file = new File(projectRoot, "UltimatePixelArray");

        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setWritable(true);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public void fillPixels(int[] pixelArrray, Zoom currZoom) {
        if (pixelArrray.length < currZoom.width * currZoom.height) {
            Log.e("fillPixels", "array too small!");
            return;
        }

        try {
            buffs = new MappedByteBuffer[currZoom.height];
            FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE);
            for (int i = 0; i < buffs.length; i++) {
                buffs[i] = channel.map(FileChannel.MapMode.READ_WRITE, getZoomStartOffset(i, currZoom), currZoom.width);
                buffs[i].load();
                IntBuffer toCopy = buffs[i].asIntBuffer();
                toCopy.get(pixelArrray, currZoom.width * i, currZoom.width);
            }
        } catch (Exception e) {
            Log.e("fillPixels", "exception: " + e.toString());
        }
    }

    // NOTE: assumes buffs is already mapped and the right dimensions
    void update(LocalPixelArray localPixelArray) {
        Zoom currZoom = localPixelArray.getZoom();
        try {
            Bitmap fillFrom = localPixelArray.getBitmap();
            for (int row = 0; row < currZoom.height; row++) {
                IntBuffer copyTo = buffs[row].asIntBuffer();
                // Note: I'm not even gonna bother with hasArray stuff
                // just gonna use a small transfer array
                int[] toFill = new int[currZoom.width];
                for (int col = 0; col < currZoom.width; col++) {
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

    public int getZoomStartOffset(int rowNum, Zoom currZoom) {
        return getPixelsWide() * (currZoom.yOffset + rowNum) + currZoom.xOffset;
    }
}
