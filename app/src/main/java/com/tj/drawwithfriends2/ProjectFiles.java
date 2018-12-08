package com.tj.drawwithfriends2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TJ on 7/28/2018.
 */

public class ProjectFiles implements Serializable {
    private File dir;
    private File config; // TODO lock config file... and probably others... in fact, let this TODO represent all synchronization
    private File inputsFile;
    private UltimatePixelArray ultimatePixelArray;

    private static final String CONFIG_FILE_NAME = "config";
    private static final String INPUTS_FILE_NAME = "inputsFile";
    private static final String ULTIMATE_FILE_NAME = "UltimatePixels";
    private static final int DEFAULT_WIDTH = 192;//784;
    private static final int DEFAULT_HEIGHT = 256;//1024;
    private static final int DEFAULT_XOFFSET = 0;
    private static final int DEFAULT_YOFFSET = 0;
    private static final int DEFAULT_ZOOM_LEVEL = 0;

    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_THICKNESS = 1;

    public static final int MAX_SHRINKAGE = 16;

    private String title;
    private BasicSettings basicSettings;
    private static final String BASIC_SETTINGS_TAG = "basicSettings";
    // TODO add these to config file
    private final Zoom currZoom;

    //private Map<String, ConfigurableI> settings;

    // open existing
    public ProjectFiles(File dir) throws Exception {
        // save project root
        this.dir = dir;

        int xOffset = DEFAULT_XOFFSET;
        int yOffset = DEFAULT_YOFFSET;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        currZoom = new Zoom(xOffset, yOffset, width, height, -1, -1, DEFAULT_ZOOM_LEVEL);

        // create file object instances
        this.config = new File(dir, CONFIG_FILE_NAME);
        this.inputsFile = new File(dir, INPUTS_FILE_NAME);

        basicSettings = new BasicSettings(BASIC_SETTINGS_TAG);
        loadSettings();
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

        int xOffset = DEFAULT_XOFFSET;
        int yOffset = DEFAULT_YOFFSET;
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;
        currZoom = new Zoom(xOffset, yOffset, width, height, -1, -1, DEFAULT_ZOOM_LEVEL);

        basicSettings = new BasicSettings(BASIC_SETTINGS_TAG);
        setTitle(title);
        inputsFile.createNewFile();
        inputsFile.setWritable(true);
    }

    private void defaultSettings() {
        basicSettings.returnToDefault();
    }

    private void loadSettings() {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(config));
            String nextConfigurableTag = fr.readLine();
            // TODO maybe a map of tags to configurables
            basicSettings.init(fr);
        } catch (Exception e) {
            Log.e("readConfigFile", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * config layout
     *
     * ConfigurableTag::
     * ConfigurationTag:ConfigurationValue
     * ...
     * ConfigurableTag::
     * ConfigurationTag:ConfigurationValue
     * ...
     * ...
     */
    private void saveSettings() {
        config.delete();
        try {
            config.createNewFile();
            config.setWritable(true);
            FileOutputStream fileOutputStream = new FileOutputStream(config);
            basicSettings.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("ProjectFiles", "failed to writeChanges!");
            Log.e("ProjectFiles", e.toString());
            e.printStackTrace();
        }
    }

    public File getDir() {
        return dir;
    }

    public String getTitle() {
        return basicSettings.getTitle();
    }

    public Zoom getCurrZoom() {
        return currZoom;
    }

    public void setTitle(String newTitle) {
        basicSettings.setTitle(newTitle);

        saveSettings();
    }

    public int getZoomLevel() {
        return currZoom.getZoomLevel();
    }

    public void setZoomLevel(int level) {
        currZoom.setZoomLevel(level);
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

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(inputsFile));
            DataInputStream workingStream = new DataInputStream(bufferedInputStream);

            while (true) {
                Input pi = new Input();
                pi.fromInputStream(workingStream);
                inputs.add(pi);
            }
        } catch (EOFException eof) {
            return inputs;
        } catch (FileNotFoundException fne) {
            Log.e("ProjectFiles", "no file found!");
        } catch (Exception e) {
            Log.e("Projectfiles", "exception: " + e.toString());
        }

        return inputs; // no inputs to load
    }

    public void init() throws Exception {
        // create file
        File ultimatePixelsFile = new File(this.dir, ULTIMATE_FILE_NAME);
        ultimatePixelArray = new UltimatePixelArray(currZoom.getUltimateWidth(), currZoom.getUltimateHeight(), ultimatePixelsFile.getAbsolutePath());
        ultimatePixelArray.init();

        // this is whack yo
        currZoom.setUltimateWidth(ultimatePixelArray.getWidth());
        currZoom.setUltimateHeight(ultimatePixelArray.getHeight());
        ultimatePixelArray.setAlpha();
    }

    public void processInput(Input next) {
        saveInput(next);

        // TODO put the following on its own super fucking low prio thread
        ultimatePixelArray.update(next);
    }

    public void erase() {
        ultimatePixelArray.erase();
        InputTransporter.getInstance().clearInputs();
    }
}
