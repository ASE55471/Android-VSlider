package com.ltgd.vslider.components;

import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

import com.ltgd.vslider.R;
import com.ltgd.vslider.Slider.Orientation;
import com.ltgd.vslider.utils.CommonUtil;

public class ProgressDrawable {

    Drawable drawable;
    Rect availableArea;
    float minWidth;
    float centerX, centerY;
    Orientation orientation;
    float progressPercentage;
    boolean isInitProgressPercentage;

    public ProgressDrawable(Drawable drawable) {
        this.drawable = drawable;
        orientation = Orientation.vertical;
    }

    public void setPosition(float centerX, float centerY) {
        float width;
        this.centerX = centerX;
        this.centerY = centerY;
        int left, top, right, bottom;

        if (orientation == Orientation.vertical) {
            width = Math.max(minWidth, (float) drawable.getIntrinsicWidth());

            left = Math.round(centerX - width / 2);
            top = availableArea.top;
            right = Math.round(centerX + width / 2);
            bottom = availableArea.bottom;
        } else {
            width = Math.max(minWidth, (float) drawable.getIntrinsicHeight());

            left = availableArea.left;
            top = Math.round(centerY - width / 2);
            right = availableArea.right;
            bottom = Math.round(centerY + width / 2);
        }

        drawable.setBounds(left, top, right, bottom);
    }

    public void setAvailableArea(Rect availableArea) {
        this.availableArea = availableArea;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
        setPosition(centerX, centerY);
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        setPosition(centerX, centerY);
    }

    public void setProgressPercentage(float progressPercentage) {
        LayerDrawable layerDrawable = (LayerDrawable) drawable;
        ClipDrawable progressDrawable = (ClipDrawable) layerDrawable.getDrawable(1);
        progressDrawable.setLevel(Math.round(10000.0f * progressPercentage ));

        setPosition(centerX, centerY);
    }

    public Drawable getDrawable() {
        return drawable;
    }

}
