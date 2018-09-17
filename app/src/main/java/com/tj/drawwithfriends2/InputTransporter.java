package com.tj.drawwithfriends2;

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
                    projectFiles.handleInput(next);
                    toSave.remove();
                }
            }
        });
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
    public Drawable produceDrawable(Drawable base) {
        // get from local pix, add inputs one by one, add curr input lastly
        List<Drawable> beingProduced = new ArrayList<>();
        beingProduced.add(base);

        Input[] toIt = new Input[toSave.size()];
        toSave.toArray(toIt);

        for (int i = 0; i < toIt.length; i++) {
            beingProduced.add(toIt[i]);
        }
        beingProduced.add(nextInput);
        Drawable[] drawables = new Drawable[beingProduced.size()];
        beingProduced.toArray(drawables);
        LayerDrawable ret = new LayerDrawable(drawables);
        return ret;
    }
}
