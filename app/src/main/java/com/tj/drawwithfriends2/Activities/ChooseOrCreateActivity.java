package com.tj.drawwithfriends2.Activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tj.drawwithfriends2.R;
import com.tj.drawwithfriends2.Settings.ProjectFiles;

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
        ab.setDisplayHomeAsUpEnabled(false);

        displayProjects();
    }

    @Override
    protected void onResume() {
        super.onResume();

        launchListLayout();
    }

    private void displayProjects() {
        if (fileListLayout.getVisibility() == View.INVISIBLE) {
            Log.e("ChooseOrCreate", "displaying projects when fileListLayout is invivsible");
        }

        File[] appRoot = this.getApplicationContext().getFilesDir().listFiles();

        fileListLayout.removeAllViews();

        for (File projectFile: appRoot) {
            try {
                RelativeLayout r = new RelativeLayout(this);

                ProjectListButton next = new ProjectListButton(this, projectFile);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleProjectChosen(v);
                    }
                });

                ProjectDeleteButton delete = new ProjectDeleteButton(this, projectFile);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProject(v);
                    }
                });
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                delete.setLayoutParams(lp);
                delete.setId(View.generateViewId());

                lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.LEFT_OF, delete.getId());
                next.setLayoutParams(lp);

                r.addView(next);
                r.addView(delete);

                fileListLayout.addView(r);
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

    private void launchPaintingActivity(ProjectFiles toLaunch) {
        Intent i = new Intent(ChooseOrCreateActivity.this, ProjectActivity.class);
        i.putExtra("ProjectFilesDirName", toLaunch.getDir());
        startActivity(i);
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

    private void deleteProject(View view) {
        if (view instanceof ProjectDeleteButton) {
            ((ProjectDeleteButton)view).getProjectFile().delete();
            displayProjects();
        } else {
            Log.e("ChooseOrCreate", "bad argument");
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
        ProjectFiles newProject = null;
        try {
            newProject = new ProjectFiles(projectTitle, this.getApplicationContext().getFilesDir());
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
                launchListLayout();
                break;
            default:
                break;
        }
        return true;
    }
}