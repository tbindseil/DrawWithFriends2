package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.PencilInput;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
    private File picture;
    private static final String CONFIG_FILE_NAME = "config";
    private static final String PICTURE_FILE_NAME = "picture";
    private String title;

    List<Input> inputs;
    private LayerDrawable edits = null;

    public ProjectFiles(File dir) throws Exception {
        this.dir = dir;
        this.config = new File(dir, CONFIG_FILE_NAME);
        this.picture = new File(dir, PICTURE_FILE_NAME);

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
        this.picture = new File(this.dir, PICTURE_FILE_NAME);
        setTitle(title);
    }

    public String getTitle() {
        return title;
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

    public void saveEdits() {
        picture.delete();
        try {
            picture.createNewFile();
            picture.setWritable(true);

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(picture));
            DataOutputStream workingStream = new DataOutputStream(bufferedOutputStream);
            for (Input i : inputs) {
                i.toOutputStream(workingStream);
            }
            workingStream.flush();
            workingStream.close();
        } catch (Exception e) {
            Log.e("ProjectFiles", "failed to saveEdits");
            Log.e("ProjectFiles,", e.toString());
        }
    }

    public void loadEdits() throws Exception {
        inputs = new ArrayList<>();

        long pictureLen = picture.length();

        if (pictureLen == 0) {
            Log.e("loadEdits", "no inputs to load");
            resetEdits();
            return;
        }

        if (pictureLen > Integer.MAX_VALUE) {
            Log.e("loadEdits", "file too big");
            resetEdits();
        }

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(picture));
            DataInputStream workingStream = new DataInputStream(bufferedInputStream);

            int totalBytes = 0;
            while (totalBytes < pictureLen) {
                totalBytes += workingStream.readInt();
                int type = workingStream.readInt();
                switch (type) {
                    case Input.PENCIL_INPUT:
                        PencilInput pi = new PencilInput();
                        pi.fromInputStream(workingStream);
                        inputs.add(pi);
                        break;
                    default:
                        Log.e("load edits", "unknown type!!!!");
                        break;
                }
            }
        } catch (FileNotFoundException fne) {
            Log.e("ProjectFiles", "no inputs to load!");
            return; // no inputs to load
        } catch (Exception e) {
            throw e;
        }

        resetEdits();
    }

    public LayerDrawable getEdits() {
        return edits;
    }

    public List<Input> getInputs() { return inputs; }

    // remove this when not testing
    //public void clearInputs() {
      //  inputs.clear();
    //}

    public void addEdit(Input next) {
        inputs.add(next);
        resetEdits();
    }

    private void resetEdits() {
        Input[] dr = new Input[inputs.size()];
        inputs.toArray(dr);
        edits = new LayerDrawable(dr);
    }
}
