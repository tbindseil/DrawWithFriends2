package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by TJ on 7/28/2018.
 */

@TargetApi(26)
public class ProjectFile {
    private File file;
    private String title;

    public ProjectFile(File file) throws Exception {
        this.file = file;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String titleLine = br.readLine();
            if (titleLine.startsWith("title:")) {
                title = titleLine.substring(6); // "title: is 6 chars
            }
            else {
                throw new Exception("invalid project file format");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public ProjectFile(String title, File dir) throws Exception {
        // Log.e("ProjectFile", "title is " + title + " and dir is " + dir.getAbsolutePath());
        Date date = new Date();
        Long seconds = date.getTime();
        String secondsStr = seconds.toString();
        file = new File(dir, secondsStr);
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
        file.delete();
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            String toWrite = "title:" + title;
            file.setWritable(true);
            fileOutputStream.write(toWrite.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e("ProjectFile", "failed to writeChanges!");
        }
    }
}
