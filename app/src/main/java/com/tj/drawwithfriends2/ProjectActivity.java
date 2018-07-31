package com.tj.drawwithfriends2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.projectToolBar);
        myToolbar.setTitle("TODO display title");
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // TODO force save on navigation away from page
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
