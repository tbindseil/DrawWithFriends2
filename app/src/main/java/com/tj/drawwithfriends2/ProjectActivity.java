package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.Serializable;

public class ProjectActivity extends AppCompatActivity {
    private ProjectFile currProject;

    private ConstraintLayout normalLayout;

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

        Serializable ser = getIntent().getSerializableExtra("ProjectFile");
        Toolbar myToolbar = findViewById(R.id.projectToolBar);
        if (ser instanceof ProjectFile) {
            currProject = (ProjectFile)ser;
            myToolbar.setTitle(currProject.getTitle());
            }
        else {
            myToolbar.setTitle("error");
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
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    // this assumes normalLayout is whole screen i think
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        currFocus.getGlobalVisibleRect(viewRect);
        if (viewRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
            return super.dispatchTouchEvent(ev);
        }

        currFocus.setVisibility(View.INVISIBLE);
        currFocus = normalLayout;
        currFocus.setVisibility(View.VISIBLE);
        return true;
    }

    private void updateColorSample() {
        int color = ((int)(0xff << 24)) | ((int)(red << 16)) | ((int)(green << 8)) | ((int)(blue));
        colorButton.setBackgroundColor(color);

        color = ~color;
        color |= (0xff000000);
        colorButton.setTextColor(color);

        colorButton.invalidate();
    }
}