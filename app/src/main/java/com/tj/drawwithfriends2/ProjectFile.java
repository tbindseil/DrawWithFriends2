package com.tj.drawwithfriends2;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

/**
 * Created by TJ on 7/28/2018.
 */

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

    public ProjectFile(String title) throws Exception {
        Date date = new Date();
        Long seconds = date.getTime();
        String secondsStr = seconds.toString();

        try {
            file = new File(secondsStr);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("title:" + title);
        } catch (Exception e) {
            throw e;
        }
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
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("title:" + title);
        } catch (Exception e) {
            Log.e("ProjectFile", "failed to writeChanges!");
        }
    }
}
