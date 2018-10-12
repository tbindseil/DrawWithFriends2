package com.tj.drawwithfriends2;

import android.graphics.Bitmap;
import android.util.Log;

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
    private ProjectFiles projectFiles;
    private Thread backgroundThread;

    private InputTransporter() {
        inputs = new ArrayList<>();
        nextInput = new Input();

        toSave = null;
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
                    projectFiles.processInput(next);
                    // note this line caused a no such element exception after testing
                    // that no drawing occurred when i used more than one finger. then
                    // i think i touched the back button
                    toSave.remove();
                }
            }
        });
        backgroundThread.start();
    }

    public void addPoint(int x, int y, int c) {
        nextInput.addPoint(x, y, c);
    }

    public void finishInput() {
        toSave.add(nextInput);
        nextInput = new Input();
    }

    public void popInput() {
        // TODO
    }

    public Bitmap produceBitmapToDraw(Bitmap drawTo) {
        Input[] toIt = new Input[toSave.size()];
        toSave.toArray(toIt);
        Log.e("tosave.lenth is", " " + toIt.length);
        for (int i = 0; i < toIt.length; i++) {
            drawTo = toIt[i].imprintOnto(drawTo);
        }

        drawTo = nextInput.imprintOnto(drawTo);

        return drawTo;
    }
}
