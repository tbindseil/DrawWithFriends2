package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.InputTool;
import com.tj.drawwithfriends2.Input.PencilInput;
import com.tj.drawwithfriends2.Input.PencilInputTool;

import junit.framework.Assert;

import java.io.Serializable;

@TargetApi(23)
public class ProjectActivity extends AppCompatActivity {
    private ProjectFiles currProject;

    private ConstraintLayout normalLayout;
    private PaintingImageView projectPicture;

    private InputTool currTool;

    private SeekBar thicknessBar;
    private int thickness;

    private LinearLayout colorSeekBars;
    private Button colorButton;
    private int red, green, blue;

    private View currFocus;

    // cuts first two integers off the byte array and returns it
    private byte[] fakeProjectFilesPart(byte[] bytes) {
        if (bytes.length >= 8) {
            byte[] retBytes = new byte[bytes.length - 8];
            for (int i = 0; i < retBytes.length; i++) {
                retBytes[i] = bytes[i + 8];
            }

            return retBytes;
        }

        bytes = new byte[0];
        return bytes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Serializable ser = getIntent().getSerializableExtra("ProjectFiles");
        Toolbar myToolbar = findViewById(R.id.projectToolBar);
        if (ser instanceof ProjectFiles) {
            currProject = (ProjectFiles) ser;
            if (currProject == null) {
                myToolbar.setTitle("null");
            } else {
                myToolbar.setTitle(currProject.getTitle());
                try {
                    Log.e("ProjectActivity", "loading");
                    currProject.loadEdits();
                    Log.e("ProjectActivity", "loading done");
                } catch (Exception e) {
                    Log.e("ProjectActivity", "error loading edits");
                }
            }
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

        currTool = new PencilInputTool(0);

        projectPicture = findViewById(R.id.mainCanvas);
        projectPicture.setLayerDrawable(currProject.getEdits());

        // pretty rad, a new update is produced by the currTool, ownership of this newUpdate is
        // taken by the currProject (which will hopefully serialize things to sqlite soon), and
        // then when this newUpdate changes via the input tool, those updates are automatically
        // seen on the image view
        projectPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Input newUpdate = currTool.handleTouch(motionEvent);
                if (newUpdate != null) { currProject.addEdit(newUpdate); }
                projectPicture.setLayerDrawable(currProject.getEdits());
                projectPicture.invalidate();
                return true;
            }
        });
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
                currProject.saveEdits();
                break;
            default:
                break;
        }
        return true;
    }
}
