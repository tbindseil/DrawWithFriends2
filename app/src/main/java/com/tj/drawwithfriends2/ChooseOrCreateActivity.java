package com.tj.drawwithfriends2;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class ChooseOrCreateActivity extends AppCompatActivity {
    LinearLayout fileListLayout;
    ConstraintLayout newProjectLayout;
    ConstraintLayout editProjectLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_or_create);

        fileListLayout = findViewById(R.id.fileListLayout);
        newProjectLayout = findViewById(R.id.newProjectLayout);
        editProjectLayout = findViewById(R.id.editProjectLayout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chooseOrCreateToolBar);
        myToolbar.setTitle("Let's Get Started!");
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // No going back from here... must logout explicitly
        getActionBar().setDisplayHomeAsUpEnabled(false);

        displayProjects();
    }

    private void displayProjects() {
        if (fileListLayout.getVisibility() == View.INVISIBLE) {
            Log.e("ChooseOrCreate", "displaying projects when fileListLayout is invivsible");
        }

        File[] appRoot = this.getApplicationContext().getFilesDir().listFiles();

        fileListLayout.removeAllViews();

        for (File projectFile: appRoot) {
            try {
                ProjectListButton next = new ProjectListButton(this, projectFile);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleProjectChosen(v);
                    }
                });
                fileListLayout.addView(next);
            } catch (Exception e) {
                Log.e("ChooseOrCreateActivity", "exception " + e.getMessage() + " in displayProejcts");
            }
        }
    }

    private void handleProjectEdit(View view) {
        fileListLayout.setVisibility(View.INVISIBLE);
        newProjectLayout.setVisibility(View.INVISIBLE);
        editProjectLayout.setVisibility(View.VISIBLE);
    }

    private void launchPaintingActivity(ProjectFile toLaunch) {
        Log.e("ChooseOrCreateActivity", "launching " + toLaunch.getTitle());
    }

    private void handleProjectChosen(View view) {
        if (view instanceof ProjectListButton) {
            launchPaintingActivity((((ProjectListButton) view).getProjectFile()));
        }
        else {
            Log.e("ChooseOrCreate", "handleProjectChosen called with non ProjectListButton");
            return;
        }
    }

    private void handleNewProject() {
        fileListLayout.setVisibility(View.INVISIBLE);
        editProjectLayout.setVisibility(View.INVISIBLE);
        newProjectLayout.setVisibility(View.VISIBLE);
    }

    private void launchListLayout() {
        editProjectLayout.setVisibility(View.INVISIBLE);
        newProjectLayout.setVisibility(View.INVISIBLE);
        fileListLayout.setVisibility(View.VISIBLE);

        displayProjects();
    }

    public void handleCancelButton(View view) {
        launchListLayout();
    }

    public void handleCreateButton(View view) {
        TextView projectTitleText = findViewById(R.id.nameStdText);
        String projectTitle = projectTitleText.getText().toString();
        ProjectFile newProject = null;
        try {
            newProject = new ProjectFile(new File(projectTitle));
        } catch (Exception e) {
            Log.e("ChooseOrCreateActivity", "failed to create new project File");
            Log.e("ChooseOrCreateActivity", "Exception says: " + e.getMessage());
            return;
        }
        launchPaintingActivity(newProject);
    }

    /**
     * the menu layout has the 'add/new' menu item
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_or_create_action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                handleNewProject();
                break;
            case R.id.action_refresh:
                displayProjects();
            default:
                break;
        }
        return true;
    }
}