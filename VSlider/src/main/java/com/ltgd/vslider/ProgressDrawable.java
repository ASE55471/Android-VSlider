package com.ltgd.vslider;

import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.ltgd.vslider.Slider.ProgressDrawableDisplayType;
import com.ltgd.vslider.Slider.Orientation;

import static com.ltgd.vslider.CommonUtil.getPositionFromProgress;

class ProgressDrawable {

    LayerDrawable layerDrawable;
    Rect availableArea;
    float minWidth;
    float centerX, centerY;
    Orientation orientation;
    int max;
    int progressStart;
    ProgressDrawableDisplayType progressDrawableDisplayType;
    static final float clipLevelMax = 10000.0f;

    ProgressDrawable(Drawable drawable) {
        this.layerDrawable = (LayerDrawable) drawable;
        this.orientation = Orientation.undefined;
    }

    void setPosition(float centerX, float centerY) {
        float width;
        this.centerX = centerX;
        this.centerY = centerY;
        int left, top, right, bottom;

        if (orientation == Orientation.vertical) {
            width = Math.max(minWidth, (float) layerDrawable.getIntrinsicWidth());

            left = Math.round(centerX - width / 2);
            top = availableArea.top;
            right = Math.round(centerX + width / 2);
            bottom = availableArea.bottom;
        } else {
            width = Math.max(minWidth, (float) layerDrawable.getIntrinsicHeight());

            left = availableArea.left;
            top = Math.round(centerY - width / 2);
            right = availableArea.right;
            bottom = Math.round(centerY + width / 2);
        }

        layerDrawable.setBounds(left, top, right, bottom);
    }

    void setProgress(int progress) {
        switch (progressDrawableDisplayType) {
            default:
            case start:
                ClipDrawable progressDrawableStart = (ClipDrawable) layerDrawable.getDrawable(1);
                progressDrawableStart.setLevel(Math.round(clipLevelMax * ((float) progress / (float) max)));
                break;
            case middle:

                int delta = progress - progressStart;
                int upMax = max - progressStart;
                int downMax = progressStart;

                float topPercent = Math.max(0, (float) delta / (float) upMax);
                float downPercent = Math.max(0, -((float) delta / (float) downMax));

                ClipDrawable progressDrawableMiddleUp = (ClipDrawable) layerDrawable.getDrawable(1);
                progressDrawableMiddleUp.setLevel(Math.round(clipLevelMax * topPercent));

                ClipDrawable progressDrawableMiddleDown = (ClipDrawable) layerDrawable.getDrawable(2);
                progressDrawableMiddleDown.setLevel(Math.round(clipLevelMax * downPercent));

                break;
            case end:
                ClipDrawable progressDrawableEnd = (ClipDrawable) layerDrawable.getDrawable(1);
                progressDrawableEnd.setLevel(Math.round(clipLevelMax - (clipLevelMax * ((float) progress / (float) max))));
                break;
        }

        updatePosition();
    }

    void setProgressDrawableDisplayType(ProgressDrawableDisplayType progressDrawableDisplayType) {
        this.progressDrawableDisplayType = progressDrawableDisplayType;

        switch (progressDrawableDisplayType) {

            default:
            case start:
                break;

            case middle:

                if (orientation == Orientation.vertical) {
                    float width = Math.max(minWidth, (float) layerDrawable.getIntrinsicWidth());

                    int left = Math.round(centerX - width / 2);
                    int right = Math.round(centerX + width / 2);

                    int startPosition =
                            Math.round(getPositionFromProgress(availableArea.top, availableArea.bottom, progressStart, max));
                    int top = availableArea.top;
                    int bottom = startPosition;

                    ClipDrawable progressDrawableMiddleUp = (ClipDrawable) layerDrawable.getDrawable(1);
                    progressDrawableMiddleUp.setBounds(left, top, right, bottom);

                    top = startPosition;
                    bottom = availableArea.bottom;

                    ClipDrawable progressDrawableMiddleDown = (ClipDrawable) layerDrawable.getDrawable(2);
                    progressDrawableMiddleDown.setBounds(left, top, right, bottom);

                } else {
                    float width = Math.max(minWidth, (float) layerDrawable.getIntrinsicHeight());

                    int top = Math.round(centerY - width / 2);
                    int bottom = Math.round(centerY + width / 2);

                    int startPosition =
                            Math.round(getPositionFromProgress(availableArea.right, availableArea.left, progressStart, max));


                    int left = startPosition;
                    int right = availableArea.right;

                    ClipDrawable progressDrawableMiddleUp = (ClipDrawable) layerDrawable.getDrawable(1);
                    progressDrawableMiddleUp.setBounds(left, top, right, bottom);

                    left = availableArea.left;
                    right = startPosition;

                    ClipDrawable progressDrawableMiddleDown = (ClipDrawable) layerDrawable.getDrawable(2);
                    progressDrawableMiddleDown.setBounds(left, top, right, bottom);

                }
                break;

            case end:
                break;
        }

        updatePosition();
    }

    Drawable getDrawable() {
        return (Drawable) layerDrawable;
    }

    private void updatePosition() {
        setPosition(centerX, centerY);
    }

    void setMax(int max) {
        this.max = max;
    }

    void setProgressStart(int progressStart) {
        this.progressStart = progressStart;
    }

    void setAvailableArea(Rect availableArea) {
        this.availableArea = availableArea;
    }

    void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
        updatePosition();
    }

    void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        updatePosition();
    }

}
