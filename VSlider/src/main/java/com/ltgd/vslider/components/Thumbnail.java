package com.ltgd.vslider.components;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ltgd.vslider.Slider;
import com.ltgd.vslider.Slider.Orientation;
import com.ltgd.vslider.utils.CommonUtil;

public class Thumbnail {

    Drawable drawable;
    float extraX, extraY;
    float centerX, centerY;
    float toCenterX, toCenterY;
    Rect availableArea;
    Orientation orientation;
    int progress;
    int max;
    OnThumbnailChangeListener onThumbnailChangeListener;

    public Thumbnail(Drawable drawable) {
        this.drawable = drawable;
        this.extraX = 0;
        this.extraY = 0;
        this.orientation = Orientation.undefined;
    }

    //listener
    public interface OnThumbnailChangeListener {
        void onProgressChanged(float centerX, float centerY, int progress);
    }

    public void setRelativePosition(float x, float y) {
        setPosition(x - toCenterX, y - toCenterY);
    }

    public void setPosition(float centerX, float centerY) {
        setPositionInternal(centerX, centerY);
    }

    private void setPositionInternal(float centerX, float centerY) {

        switch (orientation) {
            default:
            case undefined:
                break;
            case vertical:
                progress = getProgressFromPosition(availableArea.top, availableArea.bottom, centerY);
                centerY = getPositionFromProgress(availableArea.top, availableArea.bottom, progress);
                centerX = this.centerX;
                break;
            case horizontal:
                progress = getProgressFromPosition(availableArea.right, availableArea.left, centerX);
                centerX = getPositionFromProgress(availableArea.right, availableArea.left, progress);
                centerY = this.centerY;
                break;
        }

        if (availableArea != null) {
            centerX = CommonUtil.valueCut(centerX, availableArea.right, availableArea.left);
            centerY = CommonUtil.valueCut(centerY, availableArea.bottom, availableArea.top);
        }

        this.centerX = centerX;
        this.centerY = centerY;

        if (onThumbnailChangeListener != null)
            onThumbnailChangeListener.onProgressChanged(this.centerX, this.centerY, progress);

        int left = Math.round(centerX - (float) drawable.getIntrinsicWidth() / 2);
        int top = Math.round(centerY - (float) drawable.getIntrinsicHeight() / 2);
        int right = Math.round(centerX + (float) drawable.getIntrinsicWidth() / 2);
        int bottom = Math.round(centerY + (float) drawable.getIntrinsicHeight() / 2);

        drawable.setBounds(left, top, right, bottom);
    }

    public boolean isTouched(float x, float y) {
        return isTouched(x, y, true);
    }

    public boolean isTouched(float x, float y, boolean setDisToCenter) {
        if (setDisToCenter) {
            toCenterX = x - centerX;
            toCenterY = y - centerY;
        }
        return x >= (float) drawable.getBounds().left - extraX && x <= (float) drawable.getBounds().right + extraX &&
                y >= (float) drawable.getBounds().top - extraY && y <= (float) drawable.getBounds().bottom + extraY;
    }

    public void setTouchAreaRatio(float ratio) {
        extraX = ((float) drawable.getIntrinsicWidth() * ratio
                - drawable.getIntrinsicWidth()) / 2;
        extraY = ((float) drawable.getIntrinsicHeight() * ratio
                - drawable.getIntrinsicHeight()) / 2;
    }

    public void setAvailableArea(Rect availableArea) {
        this.availableArea = availableArea;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setOnThumbnailChangeListener(OnThumbnailChangeListener onThumbnailChangeListener) {
        this.onThumbnailChangeListener = onThumbnailChangeListener;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    private int getProgressFromPosition(float p1, float p2, float pT) {
        return Math.round(((p2 - pT) / (p2 - p1)) * (float) max);
    }

    public float getPositionFromProgress(float p1, float p2, int progress) {
        return -((float) progress / (float) max * (p2 - p1)) + p2;
    }

}
