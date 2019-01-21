package com.tj.drawwithfriends2.Input;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tj.drawwithfriends2.Activities.PaintingImageView;
import com.tj.drawwithfriends2.R;
import com.tj.drawwithfriends2.Settings.ProjectFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by TJ on 8/26/2018.
 */
public class InputTransporter {
    private static final InputTransporter instance = new InputTransporter();

    private List<Input> inputs;
    // next input might have to be a per writer thing
    //private Input nextInput;

    private Queue<Input> toSave;
    private ProjectFiles projectFiles;
    private Thread saveInputThread;

    private ThreadPoolExecutor commandThreadPool;

    Boolean writeToA;
    private Input auxilaryInputA;
    private Input auxilaryInputB;
    private Thread updatePaintingThread;
    private Handler mHanler;
    public static final int REDRAW = 0; // only message being passed so far just means refresh

    private InputTransporter() {
        inputs = new ArrayList<>();
        //nextInput = new Input();

        toSave = null;

        writeToA = true;
        auxilaryInputA = new Input(true);
        auxilaryInputB = new Input(true);
    }

    public static InputTransporter getInstance() {
        return instance;
    }

    public void setProjectFiles(ProjectFiles projectFiles) {
        this.projectFiles = projectFiles;
        inputs = projectFiles.loadInputs();
    }

    public void startTransporter(Queue<Input> inputs, final PaintingImageView painting) {
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

                    SystemClock.sleep(60000); // TODO send updates when input finishes
                }
            }
        });
        saveInputThread.start();

        LinkedBlockingQueue<Runnable> commandQueue = new LinkedBlockingQueue<>();
        commandThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(), 10000000, TimeUnit.SECONDS,
                commandQueue);

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

                    // painting.invalidate(); can only happen on ui thread
                    Message updateMessage = mHanler.obtainMessage(REDRAW, null);
                    updateMessage.sendToTarget();

                    SystemClock.sleep(17);
                }
            }
        });
        updatePaintingThread.start();

        // start handler to run on ui thread to update painting
        mHanler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                int state = message.what;
                if (state == REDRAW) {
                    painting.setBitmap(projectFiles.getBitmap());
                    painting.invalidate();
                } else {
                    Log.e("Debug", "error! got invalid state " + state + " on handler");
                }
            }
        };
    }

    // drawing stuff below
    public void queueDrawPoint(final int x, final int y, final int c) {
        commandThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                addPoint(x, y, c);
            }
        });
    }

    private void addPoint(int x, int y, int c) {
        /*synchronized (nextInput) {
            addPoint(x, y, c, nextInput);
        }*/

        synchronized (writeToA) {
            if (writeToA) {
                addPoint(x, y, c, auxilaryInputA);
            } else {
                addPoint(x, y, c, auxilaryInputB);
            }
        }
    }

    private void addPoint(int x, int y, int c, Input addTo) {
        addTo.addPoint(x, y, c);
    }

    public void queueDrawCircle(final int x, final int y, final int radius, final int c) {
        commandThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                drawCircle(x, y, radius, c);
            }
        });
    }

    private void drawCircle(int x, int y, int radius, int c) {
        /*synchronized (nextInput) {
            drawCircle(x, y, radius, c, nextInput);
        }*/

        synchronized (writeToA) {
            if (writeToA) {
                drawCircle(x, y, radius, c, auxilaryInputA);
            } else {
                drawCircle(x, y, radius, c, auxilaryInputB);
            }
        }
    }

    private void drawCircle(int x0, int y0, int radius, int color, Input addTo) {
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
        //addPoint(x0, y0, color);
    }

    public void queueFillCircle(final int x0, final int y0, final int radius, final int color) {
        commandThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                fillCircle(x0, y0, radius, color);
            }
        });
    }

    private void fillCircle(int x0, int y0, int radius, int color) {
        /*synchronized (nextInput) {
            fillCircle(x0, y0, radius, color, nextInput);
        }*/

        synchronized (writeToA) {
            if (writeToA) {
                fillCircle(x0, y0, radius, color, auxilaryInputA);
            } else {
                fillCircle(x0, y0, radius, color, auxilaryInputB);
            }
        }
    }

    private  void fillCircle(int x0, int y0, int radius, int color, Input addTo) {
        int radiusSqrd = radius * radius;
        for (int x = x0 - radius, deltaX = 0 - radius; x < x0 + radius; x++, deltaX++) {
            for (int y = y0 - radius, deltaY = 0 - radius; y < y0 + radius; y++, deltaY++) {
                if (deltaY * deltaY + deltaX * deltaX < radiusSqrd) {
                    addTo.addPoint(x, y, color);
                }
            }
        }
    }

    private void coolPattern(int x0, int y0, int radius, int color, Input addTo) {
        drawCircle(x0, y0, radius, color, addTo);

        if (radius > 0) {
            fillCircle(x0, y0, radius - 1, color, addTo);
        }
    }

    public void queueFillLine(final Point currPoint, final Point lastPoint, final int color, final int thickness) {
        commandThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                fillLine(currPoint, lastPoint, color, thickness);
            }
        });
    }

    private void fillLine(Point currPoint, Point lastPoint, int color, int thickness) {
        /*synchronized (nextInput) {
            fillLine(currPoint, lastPoint, color, thickness, nextInput);
        }*/

        synchronized (writeToA) {
            if (writeToA) {
                fillLine(currPoint, lastPoint, color, thickness, auxilaryInputA);
            } else {
                fillLine(currPoint, lastPoint, color, thickness, auxilaryInputB);
            }
        }
    }

    private void fillLine(Point currPoint, Point lastPoint, int color, int thickness, Input addTo) {
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

    public void queueDrawLine(final Point currPoint, final Point lastPoint, final int color) {
        commandThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                drawLine(currPoint, lastPoint, color);
            }
        });
    }

    private void drawLine(Point currPoint, Point lastPoint, int color) {
        /*synchronized (nextInput) {
            drawLine(currPoint, lastPoint, color, nextInput);
        }*/

        synchronized (writeToA) {
            if (writeToA) {
                drawLine(currPoint, lastPoint, color, auxilaryInputA);
            } else {
                drawLine(currPoint, lastPoint, color, auxilaryInputB);
            }
        }
    }

    private void drawLine(Point currPoint, Point lastPoint, int color, Input addTo) {
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
        // at the time of input completion, how many more draw commands are needed to complete the input
        int activeCount = commandThreadPool.getActiveCount();
        int queuedCount = commandThreadPool.getQueue().size();

        /*inputs.add(nextInput);
        toSave.add(nextInput);
        nextInput = new Input();*/
    }

    public void popInput() {
        // TODO
    }

    public void clearInputs() {
        inputs.clear();
        // nextInput = new Input();
    }
}
