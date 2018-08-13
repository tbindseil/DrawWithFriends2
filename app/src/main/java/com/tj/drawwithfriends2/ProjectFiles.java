package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by TJ on 7/28/2018.
 */

@TargetApi(26)
public class ProjectFiles implements Serializable {
    private File dir;
    private File config; // TODO lock config file... and probably others... in fact, let this TODO represent all synchronization
    private static final String CONFIG_FILE_NAME = "config";
    private String title;

    // will be sqlitified
    // todo init this much better
    private LayerDrawable edits = null;//new LayerDrawable(new Drawable[0]);

    public ProjectFiles(File dir) throws Exception {
        this.dir = dir;
        this.config = new File(dir, CONFIG_FILE_NAME);

        try {
            BufferedReader br = new BufferedReader(new FileReader(config));
            String titleLine = br.readLine();
            if (titleLine.startsWith("title:")) {
                title = titleLine.substring(6); // "title: is 6 chars
            } else {
                throw new Exception("invalid project dir format");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public ProjectFiles(String title, File dir) throws Exception {
        // Log.e("ProjectFiles", "title is " + title + " and dir is " + dir.getAbsolutePath());
        Date date = new Date();
        Long seconds = date.getTime();
        String secondsStr = seconds.toString();
        this.dir = new File(dir, secondsStr);
        this.dir.mkdir();
        dir.setWritable(true);
        this.config = new File(this.dir, CONFIG_FILE_NAME);
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;

        writeChanges();
    }

    private void writeChanges() {
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

    public void loadEdits() { edits = new LayerDrawable(new Drawable[0]); }
    public LayerDrawable getEdits() { return edits; }

    public void addEdit(LayerDrawable next) { edits.addLayer(next); }
}
