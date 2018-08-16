package com.tj.drawwithfriends2.Input;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/10/2018.
 */

public class PencilInput extends Input {
    List<Point> points;
    List<Line> lines;
    int color;

    public PencilInput(int color) {
        points = new ArrayList<>();
        lines = new ArrayList<>();
        this.color = color;
    }

    public void addToThis(Point[] points, int color) {
        if (points.length == 1) {
            this.points.add(points[0]);
        }
        else if (points.length == 2) {
            lines.add(new Line(points[0].x, points[0].y, points[1].x, points[1].y));
        }
        else {
            Log.e("PencilInput", "Too many points or zero!");
        }
        this.color = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);

        for (Point p: points) {
            canvas.drawPoint(p.x, p.y, paint);
        }

        for (Line l: lines) {
            canvas.drawLine(l.x1, l.y1, l.x2, l.y2, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        Log.e("PencilInput", "setAlpha was called... you should find out why");
        color &= 0x00fffffff;
        color |= ((alpha & 0xff) << 24);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}

class Line {
    public int x1, x2, y1, y2;
    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}