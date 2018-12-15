package com.tj.drawwithfriends2.Settings;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.InputTransporter;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by TJ on 7/28/2018.
 */

public class ProjectFiles implements Serializable {
    // top level directory
    private File dir;

    // data files
    private static final String ULTIMATE_FILE_NAME = "UltimatePixels";
    private UltimatePixelArray ultimatePixelArray;
    private static final String INPUTS_FILE_NAME = "inputsFile";
    private File inputsFile;

    // project defaults
    private static final int DEFAULT_WIDTH = 192;//784;
    private static final int DEFAULT_HEIGHT = 256;//1024;

    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_THICKNESS = 1;
    public static final int MAX_SHRINKAGE = 16;

    // settings stuff
    private static final String CONFIG_FILE_NAME = "config";
    private Map<String, Configurable> configurablesMap;
    private File config; // TODO lock config file... and probably others... in fact, let this TODO represent all synchronization
    private static final String BASIC_SETTINGS_TAG = "basicSettings";
    private static final String ZOOM_SETTING_TAG = "zoomSettings";
    private BasicSettings basicSettings;
    // TODO add these to config file
    private Zoom currZoom;

    // open existing
    public ProjectFiles(File dir) throws Exception {
        // save project root
        this.dir = dir;

        initFiles();

        quickStart();
    }

    // create new
    public ProjectFiles(String title, File dir) throws Exception {
        Date date = new Date();
        Long seconds = date.getTime();
        String secondsStr = seconds.toString();

        this.dir = new File(dir, secondsStr);
        this.dir.mkdir();
        dir.setWritable(true);

        initFiles();
        inputsFile.createNewFile();
        inputsFile.setWritable(true);

        quickStart();
        setTitle(title);
    }

    private void initFiles() {
        // create file object instances
        this.config = new File(dir, CONFIG_FILE_NAME);
        this.inputsFile = new File(dir, INPUTS_FILE_NAME);
    }

    private void initConfigurablesMap() throws Exception {
        basicSettings = new BasicSettings();
        currZoom = new Zoom(ultimatePixelArray.getWidth(), ultimatePixelArray.getHeight());

        configurablesMap = new HashMap<>();

        configurablesMap.put(BASIC_SETTINGS_TAG, basicSettings);
        configurablesMap.put(ZOOM_SETTING_TAG, currZoom);
    }

    private void defaultSettings() {
        // todo
        //basicSettings.returnToDefault();
    }

    // there is a weird dependency issue,
    // the ultimate pixel array is too big to create a bunch of them
    // when listing projects, so just load basic settings until one
    // project is chosen
    private void loadBasicSettings() {
        if (basicSettings == null) {
            Log.e("loadBasicSettings", "error, basic settings should be instantiated");
            return;
        }
        try {
            BufferedReader fr = new BufferedReader(new FileReader(config));
            String nextConfigurableTag = fr.readLine();
            while (!nextConfigurableTag.equals(BASIC_SETTINGS_TAG + "::") &&
                    !nextConfigurableTag.equals("")) {
                fr.readLine();
            }
            if (!nextConfigurableTag.equals("")) {
                basicSettings.init(fr);
            }
        } catch(Exception e){
            Log.e("loadBasicSettings", e.toString());
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(config));
            String nextConfigurableTag = fr.readLine();
            while (!nextConfigurableTag.equals("")) {
                if (nextConfigurableTag.endsWith("::")) {
                    Configurable currEntry = configurablesMap.get(nextConfigurableTag.substring(0, nextConfigurableTag.length() - 2));
                    nextConfigurableTag = currEntry.init(fr);
                } else {
                    Log.e("loadSettings", "error: read string: " + nextConfigurableTag + " not a tag");
                    return;
                }
            }
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
    public void saveSettings() {
        config.delete();
        try {
            config.createNewFile();
            config.setWritable(true);
            FileOutputStream fileOutputStream = new FileOutputStream(config);
            Iterator it = configurablesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Configurable> curr = (Map.Entry<String, Configurable>)it.next();
                curr.getValue().write(curr.getKey(), fileOutputStream);
            }
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

    public void quickStart() throws Exception {
        basicSettings = new BasicSettings();

        configurablesMap = new HashMap<>();

        configurablesMap.put(BASIC_SETTINGS_TAG, basicSettings);

        loadBasicSettings();
    }

    public void fullStart() throws Exception {
        // create file
        File ultimatePixelsFile = new File(this.dir, ULTIMATE_FILE_NAME);

        if (ultimatePixelsFile.length() == 0) {
            ultimatePixelArray = new UltimatePixelArray(DEFAULT_WIDTH, DEFAULT_HEIGHT, ultimatePixelsFile);
        } else {
            ultimatePixelArray = new UltimatePixelArray(ultimatePixelsFile);
        }

        initConfigurablesMap();

        loadSettings();
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
