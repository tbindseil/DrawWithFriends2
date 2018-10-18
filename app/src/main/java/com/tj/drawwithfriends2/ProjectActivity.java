package com.tj.drawwithfriends2;

import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.tj.drawwithfriends2.Input.CircleInputTool;
import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.PencilInputTool;

import java.io.File;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProjectActivity extends AppCompatActivity {
    private ProjectFiles currProject;

    private ConstraintLayout normalLayout;
    private PaintingImageView projectPicture;

    private SeekBar thicknessBar;
    private int thickness;

    private LinearLayout colorSeekBars;
    private Button colorButton;
    private int red, green, blue;

    private LinearLayout zoomLayout;
    private ZoomImageView zoomImage;
    private SeekBar zoomSeekBar;

    private View currFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Serializable ser = getIntent().getSerializableExtra("ProjectFilesDirName");
        Toolbar myToolbar = findViewById(R.id.projectToolBar);
        try {
            File dirName = (File) ser;
            currProject = new ProjectFiles(dirName);
            myToolbar.setTitle(currProject.getTitle());
            currProject.init();
        } catch (Exception e) {
            myToolbar.setTitle("error");
            Log.e("ProjectActivity", "exception loading edits or getting title");
            e.printStackTrace();
        }

        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        normalLayout = findViewById(R.id.projectConstraintLayout);
        currFocus = normalLayout;

        // TODO force save on navigation away from page
        ab.setDisplayHomeAsUpEnabled(true);

        thicknessBar = findViewById(R.id.thicknessSeekBar);
        thicknessBar.setOnSeekBarChangeListener(new SeekBarInterface() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (projectPicture != null) {
                    projectPicture.setThickness(progress + 1);
                }
            }
        });
        thicknessBar.setMax(30);

        colorSeekBars = findViewById(R.id.colorLayout);

        SeekBar s = findViewById(R.id.redSeekBar);
        s.setMax(255);
        s.setOnSeekBarChangeListener(new SeekBarInterface() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                red = progress;
                updateColorSample();
            }
        });

        s = findViewById(R.id.greenSeekBar);
        s.setMax(255);
        s.setOnSeekBarChangeListener(new SeekBarInterface() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                green = progress;
                updateColorSample();
            }
        });

        s = findViewById(R.id.blueSeekBar);
        s.setMax(255);
        s.setOnSeekBarChangeListener(new SeekBarInterface() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blue = progress;
                updateColorSample();
            }
        });

        colorButton = findViewById(R.id.colorButton);

        zoomLayout = findViewById(R.id.zoomLayout);
        zoomImage = findViewById(R.id.zoomImage);
        zoomSeekBar = findViewById(R.id.zoomSeekBar);
        zoomSeekBar.setMax(ProjectFiles.MAX_SHRINKAGE - 1); // note: 0 is possible, adding one to accomplish a shift
        zoomSeekBar.setOnSeekBarChangeListener(new SeekBarInterface() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress + 1; // see zoomSeekBar.setMax()
                currProject.setCurrWidth(currProject.getWidth() / progress);
                currProject.setCurrHeight(currProject.getHeight() / progress);

                zoomImage.invalidate();
            }
        });

        projectPicture = findViewById(R.id.mainCanvas);

        projectPicture.setContext(this.getApplicationContext());
        projectPicture.setProjectFiles(currProject);
        // TODO read this value from files
        projectPicture.setInputTool(new PencilInputTool(currProject.getCurrZoom()));

        // start input transporter
        InputTransporter.getInstance().setProjectFiles(currProject);
        Queue<Input> toSave = new LinkedBlockingQueue<>();
        InputTransporter.getInstance().startTransporter(toSave);
        projectPicture.updatePaintingImage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // see processInput TODO currProject.saveInputs();
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        projectPicture.notifyOfWidthAndHeight();
        //zoomImage
    }

    public void handleColorClick(View view) {
        colorSeekBars.setVisibility(View.VISIBLE);
        currFocus = colorSeekBars;
    }

    public void handlePencilClick(View view) {
        projectPicture.setInputTool(new PencilInputTool(currProject.getCurrZoom()));
        projectPicture.notifyOfWidthAndHeight();
    }

    public void handleThicknessClick(View view) {
        thicknessBar.setVisibility(View.VISIBLE);
        currFocus = thicknessBar;
    }

    public void handleShapeClick(View view) {
        projectPicture.setInputTool(new CircleInputTool(currProject.getCurrZoom()));
        projectPicture.notifyOfWidthAndHeight();
    }

    public void handleZoomClick(View view) {
        zoomLayout.setVisibility(View.VISIBLE);
        currFocus = zoomLayout;

        zoomImage.launch(currProject.getCurrZoom(), this, currProject.getBitmap());
        zoomSeekBar.setProgress((currProject.getWidth() / currProject.getCurrWidth()));
    }

    public void handleZoomOkClick(View view) {
        currProject.setCurrZoom(zoomImage.getCurrZoom());
        resetCurrFocus();
    }

    public void handleZoomCancelClick(View view) {
        currProject.setCurrZoom(zoomImage.getSaveZoom());
        resetCurrFocus();
    }

    private abstract class SeekBarInterface implements SeekBar.OnSeekBarChangeListener {
        @Override
        public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    // this assumes normalLayout is whole screen i think
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        currFocus.getGlobalVisibleRect(viewRect);
        if (viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            return super.dispatchTouchEvent(ev);
        }

        resetCurrFocus();
        return true;
    }

    private void resetCurrFocus() {
        currFocus.setVisibility(View.INVISIBLE);
        currFocus = normalLayout;
        projectPicture.invalidate();
        currFocus.setVisibility(View.VISIBLE);
    }

    private void updateColorSample() {
        int color = ((int) (0xff << 24)) | ((int) (red << 16)) | ((int) (green << 8)) | ((int) (blue));
        colorButton.setBackgroundColor(color);

        projectPicture.setColor(color);

        color = ~color;
        color |= (0xff000000);
        colorButton.setTextColor(color);

        colorButton.invalidate();
    }

    /**
     * the menu layout has the 'add/new' menu item
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // todo implement a flush method or something
                // currProject.saveInputs();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return true;
    }
}
