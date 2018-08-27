package com.tj.drawwithfriends2.Input;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;

import com.tj.drawwithfriends2.Utils.Pair;
import com.tj.drawwithfriends2.Utils.Triple;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TJ on 8/14/2018.
 */

public abstract class Input extends Drawable implements InputSaver {
    public static final int PENCIL_INPUT = 1;

    // Note: these will always represents UltimateCoordinates
    HashMap<HashPoint, Integer> points;
}
