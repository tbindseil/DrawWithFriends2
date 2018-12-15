package com.tj.drawwithfriends2.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.tj.drawwithfriends2.Settings.ProjectFiles;

import java.io.File;

/**
 * Created by TJ on 7/28/2018.
 */

public class ProjectListButton extends android.support.v7.widget.AppCompatButton {
    ProjectFiles projectFile;

    public ProjectListButton(Context context, File file) throws Exception {
        super(context);

        try {
            projectFile = new ProjectFiles(file);
        } catch (Exception e) {
            throw e;
        }

        this.setText(projectFile.getTitle());
    }

    public ProjectListButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("ProjectListButton", "Wrong constructor called");
    }

    public ProjectListButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e("ProjectListButton", "Wrong constructor called");
    }

    public ProjectFiles getProjectFile() { return projectFile; }
}
