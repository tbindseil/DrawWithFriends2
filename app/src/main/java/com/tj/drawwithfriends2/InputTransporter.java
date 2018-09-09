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
    private LocalPixelArray localPixels;
    private ProjectFiles projectFiles;
    private Thread backgroundThread;

    private InputTransporter() {
        inputs = new ArrayList<>();
        nextInput = new Input();

        toSave = null;
        ultimatePixels = null;
        localPixels = null;
    }

    public static InputTransporter getInstance() {
        return instance;
    }

    public void startTransporter(final UltimatePixelArray ultimatePixels,
                                 final LocalPixelArray localPixels,
                                 ProjectFiles projectFiles,
                                 Queue<Input> inputs) {
        this.ultimatePixels = ultimatePixels;
        this.localPixels = localPixels;
        this.projectFiles = projectFiles;
        toSave = inputs;

        // create background task
        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Input next = toSave.peek();
                    ultimatePixels.handleInput(next);
                    localPixels.handleInput(next);
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

    public Drawable produceDrawable() {
        // get from local pix, add inputs one by one, add curr input lastly
        List<Drawable> beingProduced = new ArrayList<>();
        beingProduced.add(localPixels.getBitmapDrawable());
        for (int i = 0; i < inputs.size(); i++) {
            beingProduced.add(inputs.get(i));
        }
        beingProduced.add(nextInput);
        Drawable[] drawables = new Drawable[beingProduced.size()];
        beingProduced.toArray(drawables);
        LayerDrawable ret = new LayerDrawable(drawables);
        return ret;
    }
}
