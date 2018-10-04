package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;

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
    private static final int DEFAULT_WIDTH = 196;
    private static final int DEFAULT_HEIGHT = 256;
    private static final int DEFAULT_ZOOM_WIDTH = DEFAULT_WIDTH;
    private static final int DEFAULT_ZOOM_HEIGHT = DEFAULT_HEIGHT;
    private static final int DEFAULT_XOFFSET = 0;
    private static final int DEFAULT_YOFFSET = 0;

    public static final int MIN_WIDTH = DEFAULT_WIDTH / 32;
    public static final int MIN_HEIGHT = DEFAULT_HEIGHT / 32;

    private String title;
    // TODO add these to config file
    private Zoom currZoom;

    // open existing
    public ProjectFiles(File dir) throws Exception {
        // save project root
        this.dir = dir;

        int zoomWidth = DEFAULT_ZOOM_WIDTH;
        int zoomHeight = DEFAULT_ZOOM_HEIGHT;
        int xOffset = DEFAULT_XOFFSET;
        int yOffset = DEFAULT_YOFFSET;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        currZoom = new Zoom(xOffset, yOffset, zoomWidth, zoomHeight, width, height);

        // create file object instances
        this.config = new File(dir, CONFIG_FILE_NAME);
        this.inputsFile = new File(dir, INPUTS_FILE_NAME);

        ReadConfigFile();
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

        int zoomWidth = DEFAULT_ZOOM_WIDTH;
        int zoomHeight = DEFAULT_ZOOM_HEIGHT;
        int xOffset = DEFAULT_XOFFSET;
        int yOffset = DEFAULT_YOFFSET;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        currZoom = new Zoom(xOffset, yOffset, zoomWidth, zoomHeight, width, height);

        setTitle(title);
        inputsFile.createNewFile();
        inputsFile.setWritable(true);
    }

    public File getDir() {
        return dir;
    }

    public String getTitle() {
        return title;
    }

    public Zoom getCurrZoom() { return currZoom; }

    public void setTitle(String newTitle) {
        title = newTitle;

        writeConfigChanges();
    }

    public int getWidth() { return currZoom.getUltimateWidth(); }
    public int getHeight() { return currZoom.getUltimateHeight(); }
    public int getCurrWidth() { return currZoom.getCurrWidth(); }
    public int getCurrHeight() { return currZoom.getCurrHeight(); }
    public int getXOffset() { return currZoom.getxOffset(); }
    public int getYOffset() { return currZoom.getyOffset(); }

    public void setCurrWidth(int currWidth) { currZoom.setCurrWidth(currWidth); }
    public void setCurrHeight(int currHeight) { currZoom.setCurrHeight(currHeight); }
    public void setXOffset(int xOffset) { currZoom.setxOffset(xOffset); }
    public void setYOffset(int yOffset) { currZoom.setyOffset(yOffset); }

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

    private void ReadConfigFile() {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(config));
            String line = fr.readLine();
            String[] arr = line.split(":");
            if (arr.length > 1) {
                title = arr[1];
            } else {
                Log.e("readConfigFile", "invalid format, couldn't find title");
            }
        } catch (Exception e) {
            Log.e("readConfigFile", e.toString());
        }
    }

    public Bitmap getBitmap() {
        return ultimatePixelArray.getBitmap();
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

    public void init() throws Exception {
        File ultimatePixelsFile = new File(this.dir, ULTIMATE_FILE_NAME);
        ultimatePixelArray = new UltimatePixelArray(currZoom.getUltimateWidth(), currZoom.getUltimateHeight(), ultimatePixelsFile.getAbsolutePath());
        ultimatePixelArray.init();
    }

    public void processInput(Input next) {
        saveInput(next);

        // TODO put the following on its own super fucking low prio thread
        ultimatePixelArray.update(next);
    }
}
