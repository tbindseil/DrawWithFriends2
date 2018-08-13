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

/**
 * Created by TJ on 8/10/2018.
 */

public class PencilInput extends Drawable {
    Point[] points;
    int color;

    public PencilInput(Point[] points, int color) {
        this.points = points;
        this.color = color;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);

        if (points.length == 1) {
            canvas.drawPoint(points[0].x, points[0].y, paint);
        }
        else if (points.length == 2) {
            canvas.drawLine(points[0].x, points[0].y, points[1].x, points[1].y, paint);
        }
        else {
            Log.e("PencilInput", "Too many points or zero!");
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
