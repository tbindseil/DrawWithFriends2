package com.tj.drawwithfriends2.Input;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.tj.drawwithfriends2.InputTransporter;
import com.tj.drawwithfriends2.Zoom;

/**
 * Created by TJ on 10/17/2018.
 */

public class CircleInputTool extends InputTool {
    public CircleInputTool(Zoom currZoom) {
        super(currZoom);
        this.color = Color.RED;
        this.thickness = 10;
    }

    @Override
    public void handleTouch(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            // draw circle
            int currX = pixelXToCurrX(event.getX());
            int currY = pixelYToCurrY(event.getY());
            int ultimateX = currZoom.currXToUltimateX(currX);
            int ultimateY = currZoom.currYToUltimateY(currY);
            InputTransporter.getInstance().drawCircle(ultimateX, ultimateY, thickness, color);
            InputTransporter.getInstance().addPoint(currX, currY, color);
            InputTransporter.getInstance().finishInput();
        }
    }
}
