package com.ltgd.vslider;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ltgd.vslider.Slider.Orientation;

import static com.ltgd.vslider.CommonUtil.*;

class Thumbnail {

    private Drawable drawable;
    private float extraX, extraY;
    private float centerX, centerY;
    private float toCenterX, toCenterY;
    private Rect availableArea;
    private Orientation orientation;
    private int progress;
    private int max;
    private float drawableRatio;
    private OnThumbnailChangeListener onThumbnailChangeListener;

    Thumbnail(Drawable drawable, float drawableRatio) {
        this.drawable = drawable;
        this.drawableRatio = drawableRatio;
        this.extraX = 0;
        this.extraY = 0;
        this.orientation = Orientation.undefined;
    }

    //listener
    public interface OnThumbnailChangeListener {
        void onProgressChanged(float centerX, float centerY, int progress, boolean isFromUser);
    }

    void setRelativePosition(float x, float y) {
        setPosition(x - toCenterX, y - toCenterY);
    }

    void setPosition(float centerX, float centerY) {
        setPositionInternal(centerX, centerY, true);
    }

    private void setPositionInternal(float centerX, float centerY, boolean formUser) {

        if (availableArea != null) {
            centerX = valueCut(centerX, availableArea.right, availableArea.left);
            centerY = valueCut(centerY, availableArea.bottom, availableArea.top);
        }

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

        this.centerX = centerX;
        this.centerY = centerY;

        if (onThumbnailChangeListener != null)
            onThumbnailChangeListener.onProgressChanged(this.centerX, this.centerY, progress, formUser);

        int left = Math.round(centerX - (float) drawable.getIntrinsicWidth() / 2);
        int top = Math.round(centerY - (float) drawable.getIntrinsicHeight() / 2);
        int right = Math.round(centerX + (float) drawable.getIntrinsicWidth() / 2);
        int bottom = Math.round(centerY + (float) drawable.getIntrinsicHeight() / 2);

        setBounds(left, top, right, bottom);
    }

    boolean isTouched(float x, float y) {
        return isTouched(x, y, true);
    }

    boolean isTouched(float x, float y, boolean setDisToCenter) {
        toCenterX = 0;
        toCenterY = 0;
        if (x >= (float) drawable.getBounds().left - extraX &&
                x <= (float) drawable.getBounds().right + extraX &&
                y >= (float) drawable.getBounds().top - extraY &&
                y <= (float) drawable.getBounds().bottom + extraY) {
            if (setDisToCenter) {
                toCenterX = x - centerX;
                toCenterY = y - centerY;
            }
            return true;
        } else {
            return false;
        }
    }

    void setTouchAreaRatio(float ratio) {
        extraX = ((float) drawable.getIntrinsicWidth() * ratio
                - drawable.getIntrinsicWidth()) / 2;
        extraY = ((float) drawable.getIntrinsicHeight() * ratio
                - drawable.getIntrinsicHeight()) / 2;
    }

    void setDrawableRatio(float drawableRatio) {
        this.drawableRatio = drawableRatio;
    }

    void setAvailableArea(Rect availableArea) {
        this.availableArea = availableArea;
    }

    void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    void setOnThumbnailChangeListener(OnThumbnailChangeListener onThumbnailChangeListener) {
        this.onThumbnailChangeListener = onThumbnailChangeListener;
    }

    void setMax(int max) {
        this.max = max;
    }

    Drawable getDrawable() {
        return drawable;
    }

    float getCenterX() {
        return centerX;
    }

    float getCenterY() {
        return centerY;
    }

    void setProgress(int progress) {
        progress = (int) CommonUtil.valueCut(progress, max, 0);
        this.progress = progress;
        updatePosition();
    }

    private void setBounds(int left, int top, int right, int bottom) {

        left -= (getWidth() - drawable.getIntrinsicWidth()) / 2;
        top -= (getHeight() - drawable.getIntrinsicHeight()) / 2;
        right += (getWidth() - drawable.getIntrinsicWidth()) / 2;
        bottom += (getHeight() - drawable.getIntrinsicHeight()) / 2;

        drawable.setBounds(left, top, right, bottom);
    }

    private int getWidth() {
        return Math.round(drawable.getIntrinsicWidth() * drawableRatio);
    }

    private int getHeight() {
        return Math.round(drawable.getIntrinsicHeight() * drawableRatio);
    }

    private void updatePosition() {
        setPositionInternal(centerX, centerY, false);
    }

}
