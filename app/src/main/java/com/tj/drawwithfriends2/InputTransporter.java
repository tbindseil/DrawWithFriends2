package com.tj.drawwithfriends2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.tj.drawwithfriends2.Input.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by TJ on 8/26/2018.
 */

public class InputTransporter {
    private static final InputTransporter instance = new InputTransporter();

    private List<Input> inputs;
    private Input nextInput;

    private Queue<Input> toSave;
    private UltimatePixelArray ultimatePixels;
    private ProjectFiles projectFiles;
    private Thread backgroundThread;

    private InputTransporter() {
        inputs = new ArrayList<>();
        nextInput = new Input();

        toSave = null;
        ultimatePixels = null;
    }

    public static InputTransporter getInstance() {
        return instance;
    }

    public void setProjectFiles(ProjectFiles projectFiles) {
        this.projectFiles = projectFiles;
        inputs = projectFiles.loadInputs();
    }

    public void startTransporter(Queue<Input> inputs) {
        toSave = inputs;

        // create background task
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Input next = toSave.peek();
                    if (next == null) {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            continue;
                        }
                        continue;
                    }
                    projectFiles.handleInput(next);
                    toSave.remove();
                }
            }
        });
        backgroundThread.start();
    }

    public void addRect(Rect r, int c, double rotation) {
        nextInput.addRect(r, c, rotation);
    }

    public void finishInput() {
        toSave.add(nextInput);
        nextInput = new Input();
    }

    public void popInput() {
        // TODO
    }

    // base needs to come in from project activity ow we need to save a context instance
    public Bitmap produceDrawable(Bitmap drawTo) {
        Input[] toIt = new Input[toSave.size()];
        toSave.toArray(toIt);

        for (int i = 0; i < toIt.length; i++) {
            drawTo = toIt[i].imprintOnto(drawTo);
        }

        return drawTo;
    }
}
