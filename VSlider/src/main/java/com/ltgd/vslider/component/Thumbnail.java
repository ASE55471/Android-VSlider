package com.ltgd.vslider.component;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.ltgd.vslider.Slider.Orientation;

import static com.ltgd.vslider.util.CommonUtil.*;

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
        setPositionInternal(centerX, centerY, true);
    }

    private void setPositionInternal(float centerX, float centerY, boolean formUser) {

        switch (orientation) {
            default:
            case undefined:
                break;
            case vertical:
                if (formUser)
                    progress = getProgressFromPosition(availableArea.top, availableArea.bottom, centerY, max);
                centerY = getPositionFromProgress(availableArea.top, availableArea.bottom, progress, max);
                centerX = this.centerX;
                break;
            case horizontal:
                if (formUser)
                    progress = getProgressFromPosition(availableArea.right, availableArea.left, centerX, max);
                centerX = getPositionFromProgress(availableArea.right, availableArea.left, progress, max);
                centerY = this.centerY;
                break;
        }

        if (availableArea != null) {
            centerX = valueCut(centerX, availableArea.right, availableArea.left);
            centerY = valueCut(centerY, availableArea.bottom, availableArea.top);
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

    public void setProgress(int progress) {
        this.progress = progress;
        updatePosition();
    }

    private void updatePosition() {
        setPositionInternal(centerX, centerY, false);
    }


}
