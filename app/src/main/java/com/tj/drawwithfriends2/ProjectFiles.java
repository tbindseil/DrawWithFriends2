package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.Zoom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by TJ on 7/28/2018.
 */

@TargetApi(26)
public class ProjectFiles implements Serializable {
    private File dir;
    private File config; // TODO lock config file... and probably others... in fact, let this TODO represent all synchronization
    private File inputsFile;
    private UltimatePixelArray ultimatePixelArray;

    private static final String CONFIG_FILE_NAME = "config";
    private static final String INPUTS_FILE_NAME = "inputsFile";
    private static final String ULTIMATE_FILE_NAME = "UltimatePixels";
    private static final int DEFAULT_WIDTH = 4096;
    private static final int DEFAULT_HEIGHT = 7020;
    private static final Zoom DEFAULT_ZOOM = new Zoom(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);

    private String title;
    // TODO add these to config file
    private int width, height;
    private Zoom currZoom;

    private Bitmap mostRecent;

    // open existing
    public ProjectFiles(File dir) throws Exception {
        // save project root
        this.dir = dir;

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        currZoom = DEFAULT_ZOOM;

        // create file object instances
        this.config = new File(dir, CONFIG_FILE_NAME);
        this.inputsFile = new File(dir, INPUTS_FILE_NAME);
        File ultimatePixelsFile = new File(dir, ULTIMATE_FILE_NAME);
        try {
            ultimatePixelsFile.createNewFile();
            ultimatePixelsFile.setWritable(true);
        } catch (Exception e) {
            throw e;
        }

        ultimatePixelArray = new UltimatePixelArray(width, height, ultimatePixelsFile);

        // todo remove in a commit
        int[] pixelArray = new int[currZoom.width * currZoom.height];
        ultimatePixelArray.fillPixels(pixelArray, new Zoom(currZoom.xOffset, currZoom.yOffset, currZoom.width, currZoom.height));
        mostRecent = Bitmap.createBitmap(pixelArray, currZoom.width, currZoom.height, Bitmap.Config.ARGB_8888);
    }

    // create new
    public ProjectFiles(String title, File dir) throws Exception {
        Date date = new Date();
        Long seconds = date.getTime();
        String secondsStr = seconds.toString();

        this.dir = new File(dir, secondsStr);
        this.dir.mkdir();
        dir.setWritable(true);

        this.config = new File(this.dir, CONFIG_FILE_NAME);
        this.inputsFile = new File(this.dir, INPUTS_FILE_NAME);
        File ultimatePixelsFile = new File(this.dir, ULTIMATE_FILE_NAME);

        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        currZoom = DEFAULT_ZOOM;

        ultimatePixelsFile.createNewFile();
        ultimatePixelsFile.setWritable(true);
        ultimatePixelArray = new UltimatePixelArray(width, height, ultimatePixelsFile);
        // todo init file after removing zoom stuff

        setTitle(title);
        inputsFile.createNewFile();
        inputsFile.setWritable(true);
    }

    public String getTitle() {
        return title;
    }

    public Zoom getCurrZoom() {
        return currZoom;
    }

    public void setTitle(String newTitle) {
        title = newTitle;

        writeConfigChanges();
    }

    private void writeConfigChanges() {
        config.delete();
        try {
            config.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(config);
            String toWrite = "title:" + title;
            config.setWritable(true);
            fileOutputStream.write(toWrite.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("ProjectFiles", "failed to writeChanges!");
        }
    }

    public Bitmap getBitmap() {
        return mostRecent;
    }

    public void saveInput(Input toSave) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(inputsFile));
            DataOutputStream workingStream = new DataOutputStream(bufferedOutputStream);
            toSave.toOutputStream(workingStream);
            workingStream.flush();
            workingStream.close();
        } catch (Exception e) {
            Log.e("ProjectFiles", "failed to saveInputs");
            Log.e("ProjectFiles,", e.toString());
        }
    }

    public List<Input> loadInputs() {
        List<Input> inputs = new ArrayList<>();

        long pictureLen = inputsFile.length();

        if (pictureLen == 0) {
            Log.e("loadInputs", "no inputs to load");
            return inputs;
        }

        if (pictureLen > Integer.MAX_VALUE) {
            Log.e("loadInputs", "file too big");
        }

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(inputsFile));
            DataInputStream workingStream = new DataInputStream(bufferedInputStream);

            int totalBytes = 0;
            while (totalBytes < pictureLen) {
                totalBytes += workingStream.readInt();
                Input pi = new Input();
                pi.fromInputStream(workingStream);
                inputs.add(pi);
            }
        } catch (FileNotFoundException fne) {
            Log.e("ProjectFiles", "no inputs to load!");
        } catch (Exception e) {
            Log.e("Projectfiles", "exception: " + e.toString());
        }

        return inputs; // no inputs to load
    }

    public void handleInput(Input next) {
        // next draws itself to the bitmap in finalize
        next.finalize(mostRecent);

        saveInput(next);

        // TODO put the following on its own super fucking low prio thread
        ultimatePixelArray.update(mostRecent, currZoom);
    }
}
