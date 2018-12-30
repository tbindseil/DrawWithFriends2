package com.tj.drawwithfriends2.Input;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.View;

import com.tj.drawwithfriends2.Settings.ProjectFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private Thread saveInputThread;

    Boolean writeToA;
    private Input auxilaryInputA;
    private Input auxilaryInputB;
    private Thread updatePaintingThread;

    private InputTransporter() {
        inputs = new ArrayList<>();
        nextInput = new Input();

        toSave = null;

        writeToA = true;
        auxilaryInputA = new Input();
        auxilaryInputB = new Input();
    }

    public static InputTransporter getInstance() {
        return instance;
    }

    public void setProjectFiles(ProjectFiles projectFiles) {
        this.projectFiles = projectFiles;
        inputs = projectFiles.loadInputs();
    }

    public void startTransporter(Queue<Input> inputs, final View painting) {
        toSave = inputs;

        // create input saving task
        saveInputThread = new Thread(new Runnable() {
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
        saveInputThread.start();

        // create painting updating task(s?)
        updatePaintingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // just lock the whole method ?
                    // it has to honor the ordering
                    // i probably only have to lock until the boolean is flipped
                    Input relevantInput;

                    synchronized (writeToA) {
                        if (writeToA) {
                            relevantInput = auxilaryInputA;
                        } else {
                            relevantInput = auxilaryInputB;
                        }
                        writeToA = !writeToA;
                    }

                    projectFiles.updatePainting(relevantInput);
                    relevantInput.clear();

                    painting.invalidate();

                    SystemClock.sleep(17);
                }
            }
        });
    }

    public void addPoint(int x, int y, int c) {
        addPoint(x, y, c, nextInput);

        synchronized (writeToA) {
            if (writeToA) {
                addPoint(x, y, c, auxilaryInputA);
            } else {
                addPoint(x, y, c, auxilaryInputB);
            }
        }
    }

    public void addPoint(int x, int y, int c, Input addTo) {
        addTo.addPoint(x, y, c);
    }

    // sorry future self, this is kinda shitty but basically if an overload without an Input
    // is called i am assuming it is going to the next input and conversly if an overload is
    // to go to the next input, then the overload is called with no Input parameter

    public void drawCircle(int x, int y, int radius, int c) {
        drawCircle(x, y, radius, c, nextInput);

        synchronized (writeToA) {
            if (writeToA) {
                drawCircle(x, y, radius, c, auxilaryInputA);
            } else {
                drawCircle(x, y, radius, c, auxilaryInputB);
            }
        }
    }

    public void drawCircle(int x0, int y0, int radius, int color, Input addTo) {
        int x = radius - 1;
        int y = 0;
        int dx = 1;
        int dy = 1;
        int err = dx - (radius << 1);

        while (x >= y) {
            addTo.addPoint(x0 + x, y0 + y, color);
            addTo.addPoint(x0 + y, y0 + x, color);
            addTo.addPoint(x0 - y, y0 + x, color);
            addTo.addPoint(x0 - x, y0 + y, color);
            addTo.addPoint(x0 - x, y0 - y, color);
            addTo.addPoint(x0 - y, y0 - x, color);
            addTo.addPoint(x0 + y, y0 - x, color);
            addTo.addPoint(x0 + x, y0 - y, color);

            if (err <= 0) {
                y++;
                err += dy;
                dy += 2;
            }

            if (err > 0) {
                x--;
                dx += 2;
                err += dx - (radius << 1);
            }
        }
        addPoint(x0, y0, color);
    }

    public void fillCircle(int x0, int y0, int radius, int color) {
        fillCircle(x0, y0, radius, color, nextInput);

        synchronized (writeToA) {
            if (writeToA) {
                fillCircle(x0, y0, radius, color, auxilaryInputA);
            } else {
                fillCircle(x0, y0, radius, color, auxilaryInputB);
            }
        }
    }

    public void coolPattern(int x0, int y0, int radius, int color, Input addTo) {
        drawCircle(x0, y0, radius, color, addTo);

        if (radius > 0) {
            fillCircle(x0, y0, radius - 1, color, addTo);
        }
    }

    public  void fillCircle(int x0, int y0, int radius, int color, Input addTo) {
        int radiusSqrd = radius * radius;
        for (int x = x0 - radius, deltaX = 0 - radius; x < x0 + radius; x++, deltaX++) {
            for (int y = y0 - radius, deltaY = 0 - radius; y < y0 + radius; y++, deltaY++) {
                if (deltaY * deltaY + deltaX * deltaX < radiusSqrd) {
                    addTo.addPoint(x, y, color);
                }
            }
        }
    }

    public void drawLine(Point currPoint, Point lastPoint, int color, int thickness) {
        drawLine(currPoint, lastPoint, color, thickness, nextInput);
        synchronized (writeToA) {
            if (writeToA) {
                drawLine(currPoint, lastPoint, color, thickness, auxilaryInputA);
            } else {
                drawLine(currPoint, lastPoint, color, thickness, auxilaryInputB);
            }
        }
    }

    public void drawLine(Point currPoint, Point lastPoint, int color, int thickness, Input addTo) {
        Input start = new Input();

        fillCircle(currPoint.x, currPoint.y, thickness, color, addTo);
        fillCircle(lastPoint.x, lastPoint.y, thickness, color, addTo);

        drawLine(currPoint, lastPoint, color, start);

        int rise = lastPoint.y - currPoint.y;
        int run = lastPoint.x - currPoint.x;

        Map<HashPoint, Integer> startPoints = start.getPointToColorMap();

        // scale for thickness
        double deltaY, deltaX;
        if (run == 0) {
            deltaX = 0;
            deltaY = thickness;
        } else {
            deltaY = thickness * Math.sin(Math.atan(rise / run));
            deltaX = thickness * Math.cos(Math.atan(rise / run));
        }

        for (Point p: startPoints.keySet()) {
            Point lowPoint = new Point(p.x + 0 - (int)deltaY, p.y + (int)deltaX);
            Point endPoint = new Point(p.x + (int)deltaY, p.y + 0 - (int)deltaX);
            drawLine(lowPoint, endPoint, color, addTo);
        }
    }

    public void drawLine(Point currPoint, Point lastPoint, int color) {
        drawLine(currPoint, lastPoint, color, nextInput);

        synchronized (writeToA) {
            if (writeToA) {
                drawLine(currPoint, lastPoint, color, auxilaryInputA);
            } else {
                drawLine(currPoint, lastPoint, color, auxilaryInputB);
            }
        }
    }

    public void drawLine(Point currPoint, Point lastPoint, int color, Input addTo) {
        int x0 = lastPoint.x;
        int y0 = lastPoint.y;
        int x1 = currPoint.x;
        int y1 = currPoint.y;

        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1) {
                plotLineLow(x1, y1, x0, y0, color, addTo);
            } else {
                plotLineLow(x0, y0, x1, y1, color, addTo);
            }
        } else {
            if (y0 > y1) {
                plotLineHigh(x1, y1, x0, y0, color, addTo);
            } else {
                plotLineHigh(x0, y0, x1, y1, color, addTo);
            }
        }
    }

    // thanks wikipedia and Jack Bresenham
    private void plotLineLow(int x0, int y0, int x1, int y1, int color, Input addTo) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int D = 2 * dy - dx;
        int y = y0;

        for (int x = x0; x < x1; x++) {
            addTo.addPoint(x, y, color);
            if (D > 0) {
                y = y + yi;
                D = D - 2 * dx;
            }
            D = D + 2 * dy;
        }
    }

    private void plotLineHigh(int x0, int y0, int x1, int y1, int color, Input addTo) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int D = 2 * dx - dy;
        int x = x0;

        for (int y = y0; y < y1; y++) {
            addTo.addPoint(x, y, color);
            if (D > 0)
                x = x + xi;
            D = D - 2 * dy;
            D = D + 2 * dx;
        }
    }

    public void finishInput() {
        inputs.add(nextInput);
        toSave.add(nextInput);
        nextInput = new Input();
    }

    public void popInput() {
        // TODO
    }

    /**
     *
     * start here
     *
     * there are redundant imprintOnto's being called when we have a lot
     * of handletouches, this is leading to exceptions, not exceptions, just slow
     * and actually it is worse with bigger thickness regardless of size of painting,
     * so it is definetly the extra imprint ontos
     *
     * I would like to only draw queued inputs once,
     * this leads to a couple options, first we can have 2 bitmaps,
     * one is representative of the last time the png file was written,
     * and the other is representative of the last bitmap displayed on screen was
     *
     * this is a lot of memory, so I don't think its valid,
     *
     * as of now I have the chosen the first bitmap option, and this requiring the reduntant
     * draws spoken of at the beginning of this comment
     *
     * if I chose the second option, I will not have optomization in all the cases,
     * particularly if a long pencil draw happens, the input never fininshes, and therefore,
     * it is never valid to not draw it even in the second bitmap scenario
     *
     * this means things need to change if I am to cause this optimization to happen
     *
     * the first thing that comes to mind is to break apart the pencil input from what is
     * essentially now many lines and dots to one input per line or dot, and somehow group them
     * together using another means, the reason I want to group them together is so that they
     * can all be undone with a single undo
     *
     * another options is essentially doing what I had originally done on an earlier iteration,
     * and that is to draw the inputs directly to the screen bitmap, not have any sort of queueing
     * for inputs, (maybe queueing would be needed for the dots and lines of the inputs themselves)
     * and when its time to save intermittently, we just write the currently displayed bitmap,
     * in this scenario, the inputs would still be saved and managed how they are now (ie pencil
     * inputs are still all that is done until finger is lifted) and can still be undo in the
     * originally planned way.
     *
     * now, honestly I was thinking we were getting out of memory errors, but that doesn't make
     * sense so I'm going in search of the root of the exception
     *
     */

    public Bitmap drawQueuedInputs() {
        synchronized (projectFiles.getBitmap()) {
            Bitmap drawTo = projectFiles.getBitmap();
            Input[] toIt = new Input[toSave.size()];
            toSave.toArray(toIt);
            for (int i = 0; i < toIt.length; i++) {
                drawTo = toIt[i].imprintOnto(drawTo);
            }

            drawTo = nextInput.imprintOnto(drawTo);

            return drawTo;
        }
    }

    public void clearInputs() {
        inputs.clear();
        nextInput = new Input();
    }
}
