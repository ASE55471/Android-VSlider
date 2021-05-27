package com.ltgd.vslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ltgd.vslider.component.ProgressDrawable;
import com.ltgd.vslider.component.Thumbnail;
import com.ltgd.vslider.util.CommonUtil;

public class Slider extends View {

    public final static String TAG = "Slider";

    //attrs
    int mMax;
    Orientation mOrientation; // 0:Vertical 1:Horizontal
    ProgressDrawable mProgressDrawable;
    int mProgress;
    int mProgressStart;
    ProgressDrawableDisplayType mProgressDrawableDisplayType; //0:Start 1:Middle 2:End
    Thumbnail mThumbnail;
    Thumbnail mThumbnailPress;
    float mProgressDrawableMinWidth;
    float mTouchAreaRatio;

    //variables
    Rect mMainRect;
    boolean mIsSliding;
    OnSliderChangeListener onSliderChangeListener;

    //enum
    public enum Orientation {
        vertical(0), horizontal(1), undefined(2);
        int id;

        Orientation(int id) {
            this.id = id;
        }

        static Orientation fromId(int id) {
            for (Orientation f : values()) {
                if (f.id == id) return f;
            }
            throw new IllegalArgumentException();
        }
    }

    public enum ProgressDrawableDisplayType {
        start(0), middle(1), end(2);
        int id;

        ProgressDrawableDisplayType(int id) {
            this.id = id;
        }

        static ProgressDrawableDisplayType fromId(int id) {
            for (ProgressDrawableDisplayType f : values()) {
                if (f.id == id) return f;
            }
            throw new IllegalArgumentException();
        }
    }

    //listener
    public interface OnSliderChangeListener {
        void onProgressChanged(Slider slider, int progress, boolean fromUser);

        void onStartTrackingTouch(Slider slider);

        void onStopTrackingTouch(Slider slider);
    }

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

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Slider, defStyle, 0);

        mMax = a.getInt(R.styleable.Slider_max, 100);
        mOrientation = Orientation.fromId(a.getInt(R.styleable.Slider_orientation, 0));

        if (a.getDrawable(R.styleable.Slider_progressDrawable) != null)
            mProgressDrawable = new ProgressDrawable(a.getDrawable(R.styleable.Slider_progressDrawable));
        else {

            if (mOrientation == Orientation.vertical)
                mProgressDrawable = new ProgressDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.seekbar_progress_drawable_default_vertical));
            else
                mProgressDrawable = new ProgressDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.seekbar_progress_drawable_default_horizontal));
        }


        mProgress = a.getInt(R.styleable.Slider_progress, 70);
        mProgressStart = a.getInt(R.styleable.Slider_progressStart, 0);
        mProgressDrawableDisplayType = ProgressDrawableDisplayType.
                fromId(a.getInt(R.styleable.Slider_progressDrawableDisplayType, 0));

        if (a.getDrawable(R.styleable.Slider_thumbnail) != null)
            mThumbnail = new Thumbnail(a.getDrawable(R.styleable.Slider_thumbnail));
        else
            mThumbnail = new Thumbnail(ContextCompat.getDrawable(getContext(),
                    R.drawable.seekbar_thumbnail_default));

        if (a.getDrawable(R.styleable.Slider_thumbnailPress) != null)
            mThumbnailPress = new Thumbnail(a.getDrawable(R.styleable.Slider_thumbnailPress));
        else
            mThumbnailPress = new Thumbnail(ContextCompat.getDrawable(getContext(),
                    R.drawable.seekbar_thumbnail_press_default));

        mProgressDrawableMinWidth = a.getDimension(R.styleable.Slider_progressDrawableMinWidth,
                CommonUtil.convertDpToPx(20, metrics.density));

        mTouchAreaRatio = a.getFloat(R.styleable.Slider_touchAreaRatio,
                1.0f);

        a.recycle();

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

        //init main area
        mMainRect = new Rect(topLeftX, topLeftY, bottomRightX, bottomRightY);

        //init progressDrawable
        mProgressDrawable.setAvailableArea(mMainRect);
        mProgressDrawable.setPosition(mCenterX, mCenterY);
        mProgressDrawable.setMinWidth(mProgressDrawableMinWidth);
        mProgressDrawable.setProgressStart(mProgressStart);
        mProgressDrawable.setOrientation(mOrientation);
        mProgressDrawable.setMax(mMax);
        mProgressDrawable.setProgressDrawableDisplayType(mProgressDrawableDisplayType);
        mProgressDrawable.setProgress(mProgress);

        //init thumbnail
        mThumbnail.setAvailableArea(mMainRect); //restrict active area
        mThumbnail.setMax(mMax);
        mThumbnail.setPosition(mCenterX, mCenterY);
        mThumbnail.setOrientation(mOrientation);
        mThumbnail.setProgress(mProgress);
        mThumbnail.setTouchAreaRatio(mTouchAreaRatio);
        mThumbnail.setOnThumbnailChangeListener(new Thumbnail.OnThumbnailChangeListener() {
            @Override
            public void onProgressChanged(float centerX, float centerY, int progress) {
                if (onSliderChangeListener != null)
                    onSliderChangeListener.onProgressChanged(Slider.this, progress, true);
                mProgress = progress;
                mProgressDrawable.setProgress(mProgress);
                postInvalidate();
            }
        });

        //init thumbnailPress
        mThumbnailPress.setAvailableArea(mMainRect); //restrict active area
        mThumbnailPress.setOrientation(Orientation.undefined);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgressDrawable.getDrawable().draw(canvas);

        if (mIsSliding) {
            mThumbnailPress.getDrawable().draw(canvas);
        } else {
            mThumbnail.getDrawable().draw(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mThumbnail.isTouched(x, y)) {
                    if (onSliderChangeListener != null)
                        onSliderChangeListener.onStartTrackingTouch(this);
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
                    if (onSliderChangeListener != null)
                        onSliderChangeListener.onStopTrackingTouch(this);
                    postInvalidate();
                }
                break;
        }
        return true;
    }

    public void setOnSliderChangeListener(OnSliderChangeListener onSliderChangeListener) {
        this.onSliderChangeListener = onSliderChangeListener;
    }

}
