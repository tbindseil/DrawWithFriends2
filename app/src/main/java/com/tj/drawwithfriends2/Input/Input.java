package com.tj.drawwithfriends2.Input;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TJ on 8/14/2018.
 */

@TargetApi(26)
public class Input implements InputSaver {
    // Note: these will always represents UltimateCoordinates
    Map<HashPoint, Integer> pointToColorMap;

    public Input() {
        pointToColorMap = new HashMap<>();
    }

    public void addPoint(int x, int y, int color) {
        pointToColorMap.put(new HashPoint(x, y), color);
    }

    public Bitmap imprintOnto(Bitmap underlying) {
        Bitmap mutable = underlying.copy(Bitmap.Config.ARGB_8888, true);
        for (Point p: pointToColorMap.keySet()) {
            mutable.setPixel(p.x, p.y, pointToColorMap.get(p));
        }
        return mutable;
    }

    @Override
    public void toOutputStream(DataOutputStream out) {
/*TODO
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
       */
    }

    @Override
    public void fromInputStream(DataInputStream in) {
        /* TODO
        try {

            int numRects = in.readInt();
            rects.clear();
            for (int i = 0; i < numRects; i++) {
                rects.add(new RotatedRect(new Rect(in.readInt(), in.readInt(), in.readInt(), in.readInt()), in.readInt(), in.readDouble()));
            }
        } catch (Exception e) {
            Log.e("fromInputStream", "exeption: " + e.toString());
        }*/
    }
}
