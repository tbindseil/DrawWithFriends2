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
import java.io.EOFException;
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
            try {
                mutable.setPixel(p.x, p.y, pointToColorMap.get(p));
            } catch (Exception e) {
                Log.e("imprintOnto", "exception: " + e.toString());
                Log.e("imprintOnto", "stackTrace: ");
                e.printStackTrace();
            }
        }
        return mutable;
    }

    // format, 4 bytes num colors, 4 bytes a color, 4 bytes for num points of this color, 8 bytes coord, 8 bytes coord...,
    // 4 bytes color, 4 bytes for num of this color, 8 bytes coord etc
    @Override
    public void toOutputStream(DataOutputStream out) {
        // prepare data
        List<Integer> colorsEncountered = new ArrayList<>();
        List<List<Point>> pointsOfColor = new ArrayList<>();
        for (HashPoint p: pointToColorMap.keySet()) {
            int color = pointToColorMap.get(p);
            int index = 0;
            for (; index < colorsEncountered.size(); index++) {
                int c = colorsEncountered.get(index);
                if (c == color) {
                    pointsOfColor.get(index).add(p);
                    break;
                }
            }
            if (index == colorsEncountered.size()) {
                colorsEncountered.add(color);
                pointsOfColor.add(new ArrayList<Point>());
                pointsOfColor.get(index).add(p);
            }
        }

        // pack data
        try {
            out.writeInt(colorsEncountered.size());
            Log.e("debug", "writing colorsEncountered.size(): " + colorsEncountered.size());
            for (int index = 0; index < colorsEncountered.size(); index++) {
                out.writeInt(colorsEncountered.get(index));
                Log.e("debug", "writing color " + index + " : " + colorsEncountered.get(index));
                out.writeInt(pointsOfColor.get(index).size());
                Log.e("debug", "writing numcolors of " + index + " : " + pointsOfColor.get(index).size());
                for (Point p: pointsOfColor.get(index)) {
                    out.writeInt(p.x);
                    Log.e("debug", "writing p.x: " + p.x);
                    out.writeInt(p.y);
                    Log.e("debug", "writing p.y " + p.y);
                }
            }
        } catch (Exception e) {
            Log.e("toOutputStream", "exception: " + e.toString());
        }
    }

    @Override
    public void fromInputStream(DataInputStream in) throws EOFException {
        pointToColorMap = new HashMap<>();
        try {
            int numColors = in.readInt();
            Log.e("debug", "reading numColors: " + numColors);
            for (int i = 0; i < numColors; i++) {
                int currColor = in.readInt();
                Log.e("debug", "reading currColor: " + currColor);
                int numOfCurrColor = in.readInt();
                Log.e("debug", "reading numOfCurrColor: " + numOfCurrColor);
                for (int j = 0; j < numOfCurrColor; j++) {
                    int x = in.readInt();
                    int y = in.readInt();
                    Log.e("debug", "reading p.x: " + x);
                    Log.e("debug", "reading p.y: " + y);
                    pointToColorMap.put(new HashPoint(x, y), currColor);
                }
            }
        } catch (EOFException eof) {
            throw eof;
        } catch (Exception e) {
            Log.e("fromInputStream", "execption: " + e.toString());
        }
    }
}