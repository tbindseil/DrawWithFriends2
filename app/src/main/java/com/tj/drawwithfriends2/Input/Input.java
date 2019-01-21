package com.tj.drawwithfriends2.Input;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.util.TimingLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.nanoTime;

/**
 * Created by TJ on 8/14/2018.
 */

public class Input implements InputSaver {
    // Note: these will always represents UltimateCoordinates
    Map<HashPoint, Integer> pointToColorMap;

    private boolean isAux;

    public Input() {
        pointToColorMap = new HashMap<>();
        isAux = false;
    }

    public Input(boolean isAux) {
        pointToColorMap = new HashMap<>();
        this.isAux = isAux;
    }

    public void addPoint(int x, int y, int color) {
        pointToColorMap.put(new HashPoint(x, y), color);
    }

    public void clear() {
        pointToColorMap.clear();
    }

    public Map<HashPoint, Integer> getPointToColorMap() { return pointToColorMap; }

    public Bitmap imprintOnto(Bitmap underlying) {
        Bitmap mutable = underlying.copy(Bitmap.Config.ARGB_8888, true);
        for (Point p: pointToColorMap.keySet()) {
            try {
                mutable.setPixel(p.x, p.y, pointToColorMap.get(p));
            } catch (Exception e) {
                Log.e("imprintOnto", "exception: " + e.toString());
                //Log.e("imprintOnto", "stackTrace: ");
                //e.printStackTrace();
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
            for (int index = 0; index < colorsEncountered.size(); index++) {
                out.writeInt(colorsEncountered.get(index));
                out.writeInt(pointsOfColor.get(index).size());
                for (Point p: pointsOfColor.get(index)) {
                    out.writeInt(p.x);
                    out.writeInt(p.y);
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
            for (int i = 0; i < numColors; i++) {
                int currColor = in.readInt();
                int numOfCurrColor = in.readInt();
                for (int j = 0; j < numOfCurrColor; j++) {
                    int x = in.readInt();
                    int y = in.readInt();
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