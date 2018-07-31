package com.tj.drawwithfriends2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ProjectActivity extends AppCompatActivity {
    private ProjectFile currProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        currProject = getIntent().getParcelableExtra("ProjectFile");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.projectToolBar);
        if (currProject == null) {
            myToolbar.setTitle("Error opening project!");
        }
        else {
            myToolbar.setTitle(currProject.getTitle());
        }
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // TODO force save on navigation away from page
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
