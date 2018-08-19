package com.tj.drawwithfriends2.Input;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/10/2018.
 */

public class PencilInput extends Input {
    List<ColorAnd<Point>> points;
    List<ColorAnd<Line>> lines;

    public PencilInput() {
        points = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public void addToThis(Point[] points, int color) {
        if (points.length == 1) {
            this.points.add(new ColorAnd<Point>(points[0], color));
        }
        else if (points.length == 2) {
            lines.add(new ColorAnd<Line>(new Line(points[0].x, points[0].y, points[1].x, points[1].y), color));
        }
        else {
            Log.e("PencilInput", "Too many points or zero!");
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint paint = new Paint();

        for (ColorAnd<Point> p: points) {
            paint.setColor(p.color);
            canvas.drawPoint(p.thing.x, p.thing.y, paint);
        }

        for (ColorAnd<Line> l: lines) {
            paint.setColor(l.color);
            canvas.drawLine(l.thing.x1, l.thing.y1, l.thing.x2, l.thing.y2, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        Log.e("PencilInput", "setAlpha was called... you should find out why");
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void toOutputStream(DataOutputStream out) {
        // 4 bytes for numBytes, 4 bytes for type,
        // 4 bytes for num lines, 4 bytes for each of x1,x2,y1,y2,color for each line,
        // 4 bytes for num points, 4 bytes for each of x,y,color for each point
        int numBytes = 4 + 4 + 4 + (((4 * 4) + 4) * lines.size()) + 4 + (((2 * 4) + 4) * points.size());

        try {
            out.writeInt(numBytes);
            out.writeInt(Input.PENCIL_INPUT);

            out.writeInt(lines.size());
            for (ColorAnd<Line> l : lines) {
                out.writeInt(l.thing.x1);
                out.writeInt(l.thing.y1);
                out.writeInt(l.thing.x2);
                out.writeInt(l.thing.y2);
                out.writeInt(l.color);
            }

            out.writeInt(points.size());
            for (ColorAnd<Point> p : points) {
                out.writeInt(p.thing.x);
                out.writeInt(p.thing.y);
                out.writeInt(p.color);
            }
        } catch (Exception e) {
            Log.e("toOutputStream", "exception caught " + e.toString());
        }
    }

    @Override
    public void fromInputStream(DataInputStream in) {
        try {
            int numLines = in.readInt();
            lines = new ArrayList<>(numLines);
            for (int i = 0; i < numLines; i++) {
                lines.add(new ColorAnd<Line>(new Line(in.readInt(), in.readInt(), in.readInt(), in.readInt()), in.readInt()));
            }

            int numPoints = in.readInt();
            points = new ArrayList<>(numPoints);
            for (int i = 0; i < numPoints; i++) {
                points.add(new ColorAnd(new Point(in.readInt(), in.readInt()), in.readInt()));
            }
        } catch (Exception e) {
            Log.e("fromBytes", "error reading byte packet");
            Log.e("fromBytes", e.toString());
        }
    }

    public boolean equals(Object otherPencilInput) {
        if (otherPencilInput instanceof PencilInput) {
            int len1 = points.size();
            int len2 = points.size();
            if (len1 != len2) {
                return false;
            }

            for (int i = 0; i < len1; i++) {
                if (!points.get(i).equals(((PencilInput) otherPencilInput).points.get(i))) {
                    return false;
                }
            }

            len1 = lines.size();
            len2 = lines.size();
            if (len1 != len2) {
                return false;
            }

            for (int i = 0; i < len1; i++) {
                if (!lines.get(i).equals(((PencilInput) otherPencilInput).lines.get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}

class Line {
    public int x1, y1, x2, y2;
    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean equals(Object otherLine) {
        if (otherLine instanceof Line) {
            return x1 == ((Line)otherLine).x1 &&
                    y1 == ((Line) otherLine).y1 &&
                    x2 == ((Line) otherLine).x2 &&
                    y2 == ((Line) otherLine).y2;
        }

        return false;
    }
}

class ColorAnd<T> {
    public int color;
    public T thing;
    public ColorAnd (T thing, int color) {
        this.color = color;
        this.thing = thing;
    }

    public boolean equals(Object otherColorAnd) {
        if (otherColorAnd instanceof ColorAnd<?>) {
            return color == ((ColorAnd) otherColorAnd).color &&
                    thing.equals(((ColorAnd) otherColorAnd).thing);
        }

        return false;
    }
}
