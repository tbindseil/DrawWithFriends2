package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.InputTool;
import com.tj.drawwithfriends2.Input.PencilInputTool;

import java.io.File;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@TargetApi(23)
public class ProjectActivity extends AppCompatActivity {
    private ProjectFiles currProject;

    private ConstraintLayout normalLayout;
    private NoFilterImageView projectPicture;

    private InputTool currTool;

    private SeekBar thicknessBar;
    private int thickness;

    private LinearLayout colorSeekBars;
    private Button colorButton;
    private int red, green, blue;

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
                thickness = progress;
            }
        });

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

        projectPicture = findViewById(R.id.mainCanvas);

        // start input transporter
        InputTransporter.getInstance().setProjectFiles(currProject);
        Queue<Input> toSave = new LinkedBlockingQueue<>();
        InputTransporter.getInstance().startTransporter(toSave);
        updatePaintingImage();

        currTool = new PencilInputTool(currProject.getWidth(), currProject.getHeight());

        projectPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currTool.handleTouch(motionEvent);
                updatePaintingImage();
                return true;
            }
        });
    }

    private void updatePaintingImage() {
        Bitmap toDraw = currProject.getBitmap();
        toDraw = InputTransporter.getInstance().produceBitmapToDraw(toDraw);
        Drawable result = new BitmapDrawable(ProjectActivity.super.getResources(), toDraw);
        projectPicture.setImageDrawable(result);
        projectPicture.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // see handleInput TODO currProject.saveInputs();
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        currTool.setMaxXY(projectPicture.getWidth(), projectPicture.getHeight());
    }

    public void handleColorClick(View view) {
        colorSeekBars.setVisibility(View.VISIBLE);
        currFocus = colorSeekBars;
    }

    public void handleThicknessClick(View view) {
        thicknessBar.setVisibility(View.VISIBLE);
        currFocus = thicknessBar;
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

        currFocus.setVisibility(View.INVISIBLE);
        currFocus = normalLayout;
        currFocus.setVisibility(View.VISIBLE);
        return true;
    }

    private void updateColorSample() {
        int color = ((int) (0xff << 24)) | ((int) (red << 16)) | ((int) (green << 8)) | ((int) (blue));
        colorButton.setBackgroundColor(color);

        currTool.setColor(color);

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
