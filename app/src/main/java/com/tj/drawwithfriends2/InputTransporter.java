package com.tj.drawwithfriends2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import com.tj.drawwithfriends2.Input.HashPoint;
import com.tj.drawwithfriends2.Input.Input;

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

    public void drawCircle(int x, int y, int radius, int c) {
        drawCircle(x, y, radius, c, nextInput);
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
    }
/*
    public void fillCircle(int x0, int y0, int radius, int color, Input addTo) {
        /*Input extra = new Input();
        drawCircle(x0, y0, radius, color, extra);

        Point center = new Point(x0, y0);

        Map<HashPoint, Integer> points = extra.getPointToColorMap();
        Log.e("fillCircle", "radius is " + radius);
        Log.e("fillCircle", "x0 is " + x0 + " and y0 is " + y0);
        for (Point p: points.keySet()) {
            Log.e("fillCircle", "drawing to, x " + p.x + " y " + p.y);
            drawLine(center, p, color);
        }
        addPoint(center.x, center.y, Color.BLUE);

        drawCircle(x0, y0, radius, color, addTo);

        if (radius > 0) {
            fillCircle(x0, y0, radius - 1, color, addTo);
        }
    }
*/

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
            //addPoint(p.x, p.y, color);
        }
    }

    public void drawLine(Point currPoint, Point lastPoint, int color) {
        drawLine(currPoint, lastPoint, color, nextInput);
    }

    public void drawLine(Point currPoint, Point lastPoint, int color, Input addTo) {
        Log.e("drawLine##", "drawing line btw " + currPoint.x + ", " + currPoint.y + " and " + lastPoint.x + ", " + lastPoint.y);

        // the algo doesn't do the end point for some reason
        //addTo.addPoint(currPoint.x, currPoint.y, Color.GREEN);
        //addTo.addPoint(lastPoint.x, lastPoint.y, Color.GREEN);

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
            Log.e("drawLine#####", "adding point " + x + ", " + y);
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
            Log.e("drawLine#####", "adding point " + x + ", " + y);
            addTo.addPoint(x, y, color);
            if (D > 0)
                x = x + xi;
            D = D - 2 * dy;
            D = D + 2 * dx;
        }
    }

    public void finishInput() {
        toSave.add(nextInput);
        nextInput = new Input();
    }

    public void popInput() {
        // TODO
    }

    public Bitmap drawQueuedInputs(Bitmap drawTo) {
        Input[] toIt = new Input[toSave.size()];
        toSave.toArray(toIt);
        for (int i = 0; i < toIt.length; i++) {
            drawTo = toIt[i].imprintOnto(drawTo);
        }

        drawTo = nextInput.imprintOnto(drawTo);

        return drawTo;
    }
}
