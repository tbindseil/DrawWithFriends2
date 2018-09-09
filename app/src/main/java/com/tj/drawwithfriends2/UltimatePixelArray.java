package com.tj.drawwithfriends2;

import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.Zoom;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by TJ on 8/26/2018.
 */

// owned by project files
public class UltimatePixelArray extends PixelArray {
    File file;

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
            return;
        } else {
            // load
        }
    }

    public void fillPixels(int[] pixelArrray, Zoom currZoom) {
        // zoom effects localpixels bitmap width

        // many file pixel to one bitmap pixel? i guess that would mean the
        // change on the bitmap pixel would happen to all file pixels, this is thickness!
        // not making this thickness, thickness needs to radiate evenly from the touchpoint

        // pixels are bitmap pixels, the ones i make. So one bitmap pixel could take up multiple
        // led pixels, this doesn't matter, if the user touches within a bitmap pixels perimeter
        // then the bitmap pixel is affected. When the bitmap pixel is effected it will always be
        // be mapped to one ultimate pixel.

        // so.. seek, read whats needed, repeat
    }

    // save, read some bytes, modify if needed, write those bytes, repeat till eof
    void update(LocalPixelArray localPixelArray) {
        Zoom currZoom = localPixelArray.getZoom();
        try {
            int[] toWrite = new int[currZoom.getNumPixels()];
            localPixelArray.fillPixels(toWrite);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(picture));
            DataOutputStream workingStream = new DataOutputStream(bufferedOutputStream);
            for (int i = 0; i < currZoom.height; i++) {
                workingStream.
                for (int j = 0; j < currZoom.width; j++) {
                    workingStream.writeInt(toWrite[i * currZoom.width + j]);
                }
            }

            workingStream.flush();
            workingStream.close();
        } catch (Exception e) {
            Log.e("update", "exception: " + e.toString());
            return;
        }
    }

    public int getZoomStartOffset(int x, int y, Zoom currZoom) {
        return getPixelsWide() * currZoom.yOffset + currZoom.xOffset;
    }
}
