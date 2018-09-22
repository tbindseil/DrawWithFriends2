package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJ on 8/14/2018.
 */

@TargetApi(26)
public class Input extends Drawable implements InputSaver {
    // Note: these will always represents UltimateCoordinates
    private List<RotatedRect> rects;

    public Input() {
        rects = new ArrayList<>();
    }

    public void addRect(Rect r, int color, double rotation) {
        rects.add(new RotatedRect(r, color, rotation));
    }

    @Override
    public void draw(@NonNull Canvas drawTo) {
        Paint paint = new Paint();

        Log.e("draw", "canvas height is " + drawTo.getHeight() + "and width is " + drawTo.getWidth());

        for (RotatedRect rotatedRect : rects) {
            paint.setColor(rotatedRect.c);
            drawTo.save();
            drawTo.rotate((float) rotatedRect.rotation, rotatedRect.r.left, rotatedRect.r.top);
            drawTo.drawRect(rotatedRect.r, paint);
            drawTo.restore();
        }
    }

    public Bitmap imprintOnto(Bitmap underlying) {
        Canvas drawTo = new Canvas();
        Bitmap mutable = underlying.copy(Bitmap.Config.ARGB_8888, true);
        drawTo.setBitmap(mutable);
        draw(drawTo);

        return mutable;
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
        int totalBytes = 1 + 1 + (rects.size() * (2 + 1 + 4));
        try {
            out.writeInt(totalBytes);
            out.writeInt(rects.size());
            for (RotatedRect rect: rects) {
                out.writeInt(rect.r.left);
                out.writeInt(rect.r.top);
                out.writeInt(rect.r.width());
                out.writeInt(rect.r.height());
                out.writeInt(rect.c);
                out.writeDouble(rect.rotation);
            }
        } catch (Exception e) {
            Log.e("toOutputStream", "excecpion: " + e.toString());
        }
    }

    @Override
    public void fromInputStream(DataInputStream in) {
        try {
            int numRects = in.readInt();
            rects.clear();
            for (int i = 0; i < numRects; i++) {
                rects.add(new RotatedRect(new Rect(in.readInt(), in.readInt(), in.readInt(), in.readInt()), in.readInt(), in.readDouble()));
            }
        } catch (Exception e) {
            Log.e("fromInputStream", "exeption: " + e.toString());
        }
    }
}
