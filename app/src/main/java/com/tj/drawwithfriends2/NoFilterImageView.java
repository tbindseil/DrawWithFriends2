package com.tj.drawwithfriends2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by TJ on 9/25/2018.
 */

public class NoFilterImageView extends AppCompatImageView {
    private static final DrawFilter DRAW_FILTER =
            new PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0);

    public NoFilterImageView(Context context) {
        super(context);
    }

    public NoFilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoFilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawFilter oldDrawFilter = canvas.getDrawFilter();
        canvas.setDrawFilter(DRAW_FILTER);
        super.onDraw(canvas);
        canvas.setDrawFilter(oldDrawFilter);
    }
}
