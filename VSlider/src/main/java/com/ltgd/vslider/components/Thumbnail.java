package com.ltgd.vslider.components;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.ltgd.vslider.utils.CommonUtil;

public class Thumbnail {

    Drawable drawable;
    float extraX, extraY;
    float centerX, centerY;
    float toCenterX, toCenterY;
    Lock lock;
    Rect centerActiveArea;

    public enum Lock {
        UnLock, LockX, LockY, LockXY
    }

    public Thumbnail(Drawable drawable) {
        this.drawable = drawable;
        this.extraX = 0;
        this.extraY = 0;
        this.centerX = 0;
        this.centerY = 0;
        this.lock = Lock.UnLock;
    }

    public void setRelativePosition(float x, float y) {
        setPosition(x - toCenterX, y - toCenterY);
    }

    public void setPosition(float centerX, float centerY) {
        switch (lock) {
            default:
            case UnLock:
                //Do Nothing
                break;
            case LockX:
                centerX = this.centerX;
                break;
            case LockY:
                centerY = this.centerY;
                break;
            case LockXY:
                centerX = this.centerX;
                centerY = this.centerY;
                break;
        }
        setPositionInternal(centerX, centerY);
    }

    private void setPositionInternal(float centerX, float centerY) {

        if (centerActiveArea != null) {
            centerX = CommonUtil.valueCut(centerX, centerActiveArea.right, centerActiveArea.left);
            centerY = CommonUtil.valueCut(centerY, centerActiveArea.bottom, centerActiveArea.top);
        }

        this.centerX = centerX;
        this.centerY = centerY;

        int left = Math.round((centerX * 2 - (float) drawable.getIntrinsicWidth()) / 2);
        int top = Math.round((centerY * 2 - (float) drawable.getIntrinsicHeight()) / 2);
        int right = Math.round((centerX * 2 - (float) drawable.getIntrinsicWidth()) / 2
                + (float) drawable.getIntrinsicWidth());
        int bottom = Math.round((centerY * 2 - (float) drawable.getIntrinsicHeight()) / 2
                + (float) drawable.getIntrinsicHeight());

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

    public void setCenterActiveArea(Rect centerActiveArea) {
        this.centerActiveArea = centerActiveArea;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }
}
