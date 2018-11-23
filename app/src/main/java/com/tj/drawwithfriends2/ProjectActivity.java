package com.tj.drawwithfriends2;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tj.drawwithfriends2.Input.CircleInputTool;
import com.tj.drawwithfriends2.Input.Input;
import com.tj.drawwithfriends2.Input.PencilInputTool;

import java.io.File;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

// cool idea, quick tap toggles top row, so we could
// have more space

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
                // todo, why do i need this check?
                // todo make curr project, this is with project setting stuff
                if (projectPicture != null) {
                    thickness = progress + 1;
                    Log.e("thickness", "thickness is " + (progress + 1));
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
                currProject.setZoomLevel(progress);

                zoomImage.invalidate();
            }
        });

        projectPicture = findViewById(R.id.mainCanvas);

        projectPicture.setContext(this.getApplicationContext());
        projectPicture.setCurrZoom(currProject.getCurrZoom());
        zoomImage.setCurrZoom(currProject.getCurrZoom());
        projectPicture.setBitmap(currProject.getBitmap());
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
        // propogate new dimensions to currZoom
        // this is where we learn of the dimensions of projectPicture
        projectPicture.notifyOfWidthAndHeight();
        zoomImage.notifyOfWidthAndHeight();
    }

    public void handleColorClick(View view) {
        colorSeekBars.setVisibility(View.VISIBLE);
        currFocus = colorSeekBars;
    }

    public void handlePencilClick(View view) {
        projectPicture.setInputTool(new PencilInputTool(currProject.getCurrZoom()));
        // why is the below needed? is it needed?
        projectPicture.notifyOfWidthAndHeight();
        projectPicture.setThickness(thickness);
    }

    public void handleThicknessClick(View view) {
        thicknessBar.setVisibility(View.VISIBLE);
        currFocus = thicknessBar;
    }

    public void handleShapeClick(View view) {
        projectPicture.setInputTool(new CircleInputTool(currProject.getCurrZoom()));
        // why is the below needed? is it needed?
        projectPicture.notifyOfWidthAndHeight();
        projectPicture.setThickness(thickness);
    }

    public void handleZoomClick(View view) {
        zoomLayout.setVisibility(View.VISIBLE);
        currFocus = zoomLayout;

        zoomImage.launch(currProject.getCurrZoom(), this, currProject.getBitmap());
        zoomSeekBar.setProgress(currProject.getZoomLevel());
    }

    public void handleZoomOkClick(View view) {
        zoomImage.save();

        resetCurrFocus();
    }

    public void handleZoomCancelClick(View view) {
        zoomImage.cancel();

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

        // todo move with settings stuff
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
                currProject.erase();
                projectPicture.updatePaintingImage();
                // todo implement a flush method or something
                // currProject.saveInputs();
                break;
            case R.id.action_cp: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("currPoint x,y:");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String str = input.getText().toString();
                        String[] tokens = str.split(",");

                        if (tokens.length != 2) {
                            return;
                        }

                        int currX = Integer.parseInt(tokens[0]);
                        int currY = Integer.parseInt(tokens[1]);

                        InputTransporter.getInstance().addPoint(currX, currY, Color.BLUE);
                        projectPicture.updatePaintingImage();
                    }
                });

                builder.show();
            }
            break;
            case R.id.action_tp: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("TouchPoint x,y:");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String str = input.getText().toString();
                        String[] tokens = str.split(",");

                        if (tokens.length != 2) {
                            return;
                        }

                        float touchX = Float.parseFloat(tokens[0]);
                        float touchY = Float.parseFloat(tokens[1]);

                        MotionEvent e = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, touchX, touchY, 0);
                        projectPicture.onTouchEvent(e);
                    }
                });

                builder.show();
            }
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
