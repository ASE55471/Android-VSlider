package com.ltgd.vslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ltgd.vslider.components.Thumbnail;
import com.ltgd.vslider.utils.CommonUtil;

/**
 * TODO: document your custom view class.
 */
public class Slider extends View {

    public final static String TAG = "Slider";

    //attrs
    int mMax;
    int mOrientation; // 0:Vertical 1:Horizontal
    Drawable mProgressDrawable;
    int mProgress;
    int mProgressDrawableDisplayType; //0:Start 1:Middle 2:End
    Thumbnail mThumbnail;
    Thumbnail mThumbnailPress;

    //variables
    Rect mMainRect;
    float mProgressPercentage;
    boolean mIsSliding;
    int mCenterX, mCenterY;

    public Slider(Context context) {
        super(context);
        init(null, 0);
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Slider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Slider, defStyle, 0);

        mMax = a.getInt(R.styleable.Slider_max, 100);
        mOrientation = a.getInt(R.styleable.Slider_orientation, 0);
        mProgressDrawable = a.getDrawable(R.styleable.Slider_progressDrawable);
        mProgress = a.getInt(R.styleable.Slider_progress, 0);
        mProgressDrawableDisplayType = a.getInt(R.styleable.Slider_progressDrawableDisplayType,
                0);
        if (a.getDrawable(R.styleable.Slider_thumbnail) != null)
            mThumbnail = new Thumbnail(a.getDrawable(R.styleable.Slider_thumbnail));
        else
            mThumbnail = new Thumbnail(ContextCompat.getDrawable(getContext(),
                    R.drawable.seekbar_thumbnail_default));

        if (a.getDrawable(R.styleable.Slider_thumbnail) != null)
            mThumbnailPress = new Thumbnail(a.getDrawable(R.styleable.Slider_thumbnailPress));
        else
            mThumbnailPress = new Thumbnail(ContextCompat.getDrawable(getContext(),
                    R.drawable.seekbar_thumbnail_press_default));

        a.recycle();

        //Test
        mThumbnail.setTouchAreaRatio(2.0f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int topLeftX = paddingLeft;
        int topLeftY = paddingTop;
        int bottomRightX = getWidth() - paddingRight;
        int bottomRightY = getHeight() - paddingBottom;

        int mCenterX = (topLeftX + bottomRightX) / 2;
        int mCenterY = (topLeftY + bottomRightY) / 2;

        mProgressPercentage = (float) mProgress / (float) mMax;

        //init main area
        mMainRect = new Rect(topLeftX, topLeftY, bottomRightX, bottomRightY);

        //init thumbnail start position
        mThumbnail.setPosition(mCenterX, mCenterY);
        mThumbnail.setLock(Thumbnail.Lock.LockX); //const coordinate x
        mThumbnail.setCenterActiveArea(mMainRect); //restrict active area
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsSliding) {
            mThumbnailPress.getDrawable().draw(canvas);
        } else {
            mThumbnail.getDrawable().draw(canvas);
        }


       /* // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mThumbnail.isTouched(x, y)) {
                    mIsSliding = true;
                }
            case MotionEvent.ACTION_MOVE:
                if (mIsSliding) {
                    mThumbnail.setRelativePosition(x, y);
                    mThumbnailPress.setPosition(mThumbnail.getCenterX(), mThumbnail.getCenterY());
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsSliding) {
                    mIsSliding = false;
                    mThumbnail.setRelativePosition(x, y);
                    postInvalidate();
                }
                break;
        }
        return true;
    }

}
