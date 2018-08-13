package com.tj.drawwithfriends2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import android.support.v7.widget.AppCompatImageView;
/**
 * Created by TJ on 8/11/2018.
 */

@TargetApi(23)
public class PaintingImageView extends AppCompatImageView {
    private LayerDrawable inputs;

    public PaintingImageView(Context context) {
        super(context);
    }

    public PaintingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PaintingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //public PaintingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //super(context, attrs, defStyleAttr, defStyleRes);
    //}

    public void setLayerDrawable(LayerDrawable ld) {
        inputs = ld;
        setImageDrawable(inputs);
    }
}
