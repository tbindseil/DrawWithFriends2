package com.tj.drawwithfriends2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

public class ChooseOrCreateActivity extends AppCompatActivity {
    List<File> projectDirectories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_or_create);

        // No going back from here... must logout explicitly
        //getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void populateProjectList() {
        File[] appRoot = this.getApplicationContext().getFilesDir().listFiles();

        //DEBUGLog.e("ListPaintingsActivity::populateProjectList", "appRoot.length is " + appRoot.length);
        for (File projFile: appRoot) {
            //DEBUGLog.e("ListPaintingsActivity::populateProjectList", "appRoot[i].getName() is " + appRoot[i].getName());
            projectDirectories.add(projFile);
        }

    }

    private void handleProjectEdit(View view) {

    }

    private void handleProjectChosen(View view) {

    }

    private void handleNewProject(View view) {

    }
}
