package com.ltgd.vslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class Slider extends View {

    public final static String TAG = "Slider";

    //attrs
    private int mMax;
    private Orientation mOrientation; // 0:Vertical 1:Horizontal
    private ProgressDrawable mProgressDrawable;
    private int mProgress;
    private int mProgressStart;
    private ProgressDrawableDisplayType mProgressDrawableDisplayType; //0:Start 1:Middle 2:End
    private Thumbnail mThumbnail, mThumbnailPress;
    private float mProgressDrawableMinWidth;
    private float mTouchAreaRatio, mThumbnailDrawableRatio, mThumbnailPressDrawableRatio;
    private boolean mRelativeTouchPoint;
    private Drawable mProgressD, mThumbnailD, mThumbnailPressD;
    private float mProgressAreaOffset;
    private int mSmallStep, mStep;

    //variables
    private Rect mMainRect;
    private boolean mIsSliding, isTouched;
    private boolean mIsInit;
    private OnSliderChangeListener onSliderChangeListener;

    //enum
    enum Orientation {
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

    enum ProgressDrawableDisplayType {
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
        mTouchAreaRatio = a.getFloat(R.styleable.Slider_touchAreaRatio,
                1.0f);
        mThumbnailDrawableRatio = a.getFloat(R.styleable.Slider_thumbnailDrawableRatio,
                1.0f);
        if (a.hasValue(R.styleable.Slider_thumbnailPressDrawableRatio))
            mThumbnailPressDrawableRatio = a.getFloat(R.styleable.Slider_thumbnailPressDrawableRatio,
                    1.0f);
        else
            mThumbnailPressDrawableRatio = mThumbnailDrawableRatio;

        mProgressD = a.getDrawable(R.styleable.Slider_progressDrawable);
        if (mProgressD == null) {
            if (mOrientation == Orientation.vertical)
                mProgressD = ContextCompat.getDrawable(getContext(), R.drawable.seekbar_progress_drawable_default_vertical);
            else
                mProgressD = ContextCompat.getDrawable(getContext(), R.drawable.seekbar_progress_drawable_default_horizontal);
        }

        mProgress = a.getInt(R.styleable.Slider_progress, 0);
        mProgressStart = a.getInt(R.styleable.Slider_progressStart, 0);
        mProgressDrawableDisplayType = ProgressDrawableDisplayType.
                fromId(a.getInt(R.styleable.Slider_progressDrawableDisplayType, 0));

        mThumbnailD = a.getDrawable(R.styleable.Slider_thumbnail);
        if (mThumbnailD == null)
            mThumbnailD = ContextCompat.getDrawable(getContext(),
                    R.drawable.seekbar_thumbnail_default);

        mThumbnailPressD = a.getDrawable(R.styleable.Slider_thumbnailPress);
        if (mThumbnailPressD == null)
            //deep copy drawable
            mThumbnailPressD = mThumbnailD.getConstantState().newDrawable().mutate();

        mProgressDrawableMinWidth = a.getDimension(R.styleable.Slider_progressDrawableMinWidth,
                CommonUtil.convertDpToPx(20, metrics.density));

        mRelativeTouchPoint = a.getBoolean(R.styleable.Slider_relativeTouchPoint, false);

        mProgressAreaOffset = a.getDimension(R.styleable.Slider_progressAreaOffset, 1.0f);

        mStep = a.getInteger((R.styleable.Slider_step), 1);
        mSmallStep = a.getInteger((R.styleable.Slider_smallStep), 1);

        a.recycle();

    }

    private void init() {

        mProgressDrawable = new ProgressDrawable(mProgressD);
        mThumbnail = new Thumbnail(mThumbnailD, mThumbnailDrawableRatio);
        mThumbnailPress = new Thumbnail(mThumbnailPressD, mThumbnailPressDrawableRatio);

        int progressAreaOffset = Math.round(mProgressAreaOffset);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int left = paddingLeft;
        int top = paddingTop;
        int right = getWidth() - paddingRight;
        int bottom = getHeight() - paddingBottom;

        int mCenterX = (left + right) / 2;
        int mCenterY = (top + bottom) / 2;

        int thumbPressHalfHeight = Math.round((mThumbnailPress.getDrawable().getIntrinsicHeight() / 2f) * mThumbnailPressDrawableRatio);
        int thumbPressHalfWidth = Math.round((mThumbnailPress.getDrawable().getIntrinsicWidth() / 2f) * mThumbnailPressDrawableRatio);

        mProgress = (int) CommonUtil.valueCut(mProgress, mMax, 0);

        //init main area
        if (mOrientation == Orientation.vertical) {
            mMainRect = new Rect(left, top + thumbPressHalfHeight + progressAreaOffset,
                    right, bottom - thumbPressHalfHeight - progressAreaOffset);
        } else {
            mMainRect = new Rect(left + thumbPressHalfWidth + progressAreaOffset, top,
                    right - thumbPressHalfWidth - progressAreaOffset, bottom);
        }

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
        mThumbnail.setDrawableRatio(mThumbnailDrawableRatio);
        mThumbnail.setMax(mMax);
        mThumbnail.setStep(mStep);
        mThumbnail.setPosition(mCenterX, mCenterY);
        mThumbnail.setOrientation(mOrientation);
        mThumbnail.setTouchAreaRatio(mTouchAreaRatio);
        mThumbnail.setOnThumbnailChangeListener(new Thumbnail.OnThumbnailChangeListener() {
            @Override
            public void onProgressChanged(float centerX, float centerY, int progress, boolean isFromUser) {
                if (onSliderChangeListener != null && (/*!mIsInit ||*/ mProgress != progress)) {
                    onSliderChangeListener.onProgressChanged(Slider.this, progress, isFromUser);
                }
                mProgress = progress;
                mProgressDrawable.setProgress(mProgress);
                postInvalidate();
            }
        });
        mThumbnail.setProgress(mProgress);

        //init thumbnailPress
        mThumbnailPress.setAvailableArea(mMainRect); //restrict active area
        mThumbnailPress.setDrawableRatio(mThumbnailPressDrawableRatio);
        mThumbnailPress.setOrientation(Orientation.undefined);

        mIsInit = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgressDrawable.getDrawable().draw(canvas);

        if (isTouched) {
            mThumbnailPress.getDrawable().draw(canvas);
        } else {
            mThumbnail.getDrawable().draw(canvas);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minW;
        int minH;

        if (mOrientation == Orientation.vertical) {
            minH = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
            minW = Math.round(mThumbnailPressD.getIntrinsicWidth() * mThumbnailPressDrawableRatio + getPaddingLeft() + getPaddingRight());
        } else {
            minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
            minH = Math.round(mThumbnailPressD.getIntrinsicHeight() * mThumbnailPressDrawableRatio + getPaddingBottom() + getPaddingTop());
        }

        int w = resolveSizeAndState(minW, widthMeasureSpec, 0);
        int h = resolveSizeAndState(minH, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mThumbnail.isTouched(x, y)) {
                    if (onSliderChangeListener != null)
                        onSliderChangeListener.onStartTrackingTouch(this);
                    isTouched = true;
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                    updateThumbnailPosition(x, y);
                    postInvalidate();
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouched) {
                    mIsSliding = true;
                    updateThumbnailPosition(x, y);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsSliding) {
                    performClick();
                }
            case MotionEvent.ACTION_CANCEL:
                if (mIsSliding || isTouched) {
                    mIsSliding = false;
                    isTouched = false;
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    updateThumbnailPosition(x, y);
                    postInvalidate();
                    if (onSliderChangeListener != null)
                        onSliderChangeListener.onStopTrackingTouch(this);
                }
                break;
        }
        return true;
    }

    void updateThumbnailPosition(float x, float y) {
        if (mRelativeTouchPoint)
            mThumbnail.setRelativePosition(x, y);
        else
            mThumbnail.setPosition(x, y);
        mThumbnailPress.setPosition(mThumbnail.getCenterX(), mThumbnail.getCenterY());
    }

    public void setOnSliderChangeListener(OnSliderChangeListener onSliderChangeListener) {
        this.onSliderChangeListener = onSliderChangeListener;
    }

    public void setProgress(int progress) {
        if (mIsInit) {
            mThumbnail.setProgress(progress);
            postInvalidate();
        } else {
            //already have a initial call mThumbnail.setProgress(progress) in init() method.
            //so only have to replace mProgress with new progress cause mThumbnail haven't init yet.
            mProgress = progress;
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMax() {
        return mMax;
    }
}
