package com.snow.playl.seekbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.snow.playl.R;

import java.math.BigDecimal;


/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-09-17
 * desc   :
 */
public class MusicSeekBar extends ViewGroup {


    int colorFTrack;
    int colorSTrack;
    int colorTTrack;
    Paint paintFTrack;
    Paint paintSTrack;
    Paint paintTTrack;
    Path pathFTrack;
    Path pathSTrack;
    Path pathTTrack;


    int thum;
    int thumbWidth;
    int thumbHeight;
    float thumbLeft;
    float thumbTop;
    Bitmap thumbBitmap;

    int loadingSize;
    float loadingTop;
    float loadingLeft;


    int trackHeight;
    float trackWidth;

    float currentProgress = 0;
    float lastProgress;
    float max;
    float min;
    int model;


    int width;
    int height;

    int paddingLeft, paddingRigtht, paddingTop, paddingBottom;
    float mFaultTolerance = -1;//the tolerance for user seek bar touching
    int mScale = 1;

    float secondWidth;

    boolean isTouching;
    SeekChangeListener mSeekChangeListener;

    Context mContext;
    ImageView ivLoading;

    ObjectAnimator rotate;
    boolean canUpdate = true;


    public MusicSeekBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MusicSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initAttrs(attrs);
        init();
    }

    public MusicSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MusicSeekBar);
        trackHeight = (int) typedArray.getDimension(R.styleable.MusicSeekBar_trackSize, px2Dip(2));

        colorFTrack = typedArray.getColor(R.styleable.MusicSeekBar_trackFColor, Color.parseColor("#E0E0E0"));
        colorSTrack = typedArray.getColor(R.styleable.MusicSeekBar_trackSColor, Color.parseColor("#8EADCD"));
        colorTTrack = typedArray.getColor(R.styleable.MusicSeekBar_trackTColor, Color.parseColor("#FF8820"));

        thum = typedArray.getResourceId(R.styleable.MusicSeekBar_thum, 0);
        thumbWidth = (int) typedArray.getDimension(R.styleable.MusicSeekBar_thumWidth, 0);
        thumbHeight = (int) typedArray.getDimension(R.styleable.MusicSeekBar_thumHeight, 0);

        max = typedArray.getInt(R.styleable.MusicSeekBar_max, 0);
        min = typedArray.getInt(R.styleable.MusicSeekBar_min, 0);
        model = typedArray.getInt(R.styleable.MusicSeekBar_model, 0);


    }


    public void init() {
        setWillNotDraw(false);
//        Drawable drawable = getResources().getDrawable(R.drawable.play_thumb);
//        thumbBitmap = ((BitmapDrawable) drawable).getBitmap();
        if (thum != 0) {
            thumbBitmap = BitmapFactory.decodeResource(getResources(), thum);

        } else {
            thumbBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_music_seekbar_point);
        }
//        thumbBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.play_thumb );
//        Bitmap loadingBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.loading);
        ivLoading = new ImageView(mContext);
//        ivLoading.setImageBitmap(loadingBitmap);


        if (thumbWidth == 0) {
            thumbWidth = thumbBitmap.getWidth();
        }
        if (thumbHeight == 0) {
            thumbHeight = thumbBitmap.getHeight();
        }
//        loadingSize = loadingBitmap.getWidth();
        loadingSize = 10;


        initPaint();


    }


    public void initPaint() {
        paintFTrack = new Paint();
        paintFTrack.setAntiAlias(true);
        paintFTrack.setStrokeCap(Paint.Cap.ROUND);
        paintFTrack.setStrokeWidth(trackHeight);
        paintFTrack.setColor(colorFTrack);

        paintSTrack = new Paint();
        paintSTrack.setAntiAlias(true);
        paintSTrack.setStrokeCap(Paint.Cap.ROUND);
        paintSTrack.setStrokeWidth(trackHeight);
        paintSTrack.setColor(colorSTrack);


        paintTTrack = new Paint();
        paintTTrack.setAntiAlias(true);
        paintTTrack.setStrokeCap(Paint.Cap.ROUND);
        paintTTrack.setStrokeWidth(trackHeight);
        paintTTrack.setColor(colorTTrack);


        pathFTrack = new Path();
        pathSTrack = new Path();
        pathTTrack = new Path();


    }

    /**
     * 根据Model返回值
     *
     * @param value
     * @return
     */
    private int measure(int value) {
        int result = 0;
        int specMode = MeasureSpec.getMode(value);
        int specSize = MeasureSpec.getSize(value);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measure(widthMeasureSpec);
        height = measure(heightMeasureSpec);
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        paddingLeft = getPaddingLeft() + thumbWidth / 2;
        paddingTop = getPaddingTop();
        paddingRigtht = getPaddingRight() + thumbWidth / 2;
        paddingBottom = getPaddingBottom();
        trackWidth = width - paddingLeft - paddingRigtht;
        trackLeft = paddingLeft;

//        trackWidth = width;
//        trackLeft = 0;

        trackTop = (height) / 2;
        trackRight = paddingRigtht;
        trackBottom = trackTop;

        thumbTop = (height - thumbHeight) / 2;

        loadingTop = (height - loadingSize) / 2;

        loadingLeft = (thumbWidth - loadingSize) / 2;


    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int count = getChildCount();
        if (count == 0) {
            addView(ivLoading);
        }
        ivLoading.layout((int) (thumbLeft + loadingLeft), (int) loadingTop, (int) (thumbLeft + loadingLeft + loadingSize), (int) loadingTop + loadingSize);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.parseColor("#00000000"));

        if (thumbBitmap == null) {
            return;
        }
        if (width <= 0) {
            setVisibility(GONE);

            return;
        } else {
            setVisibility(VISIBLE);
        }

        drawFirstTrack(canvas);

        drawSecondTrack(canvas);

        drawThirdTrack(canvas);


    }


    float trackLeft, trackTop, trackRight, trackBottom;

    public void drawFirstTrack(Canvas canvas) {
        canvas.drawLine(trackLeft + trackHeight, trackTop, trackLeft + trackWidth - trackHeight, trackBottom, paintFTrack);
    }

    public void drawSecondTrack(Canvas canvas) {
        float secondValue;
        if ((trackLeft + secondWidth - trackHeight) < (trackLeft + trackHeight)) {
            secondValue = trackLeft + trackHeight;
        } else {
            secondValue = trackLeft + secondWidth - trackHeight;
        }
        canvas.drawLine(trackLeft + trackHeight, trackTop, secondValue, trackBottom, paintSTrack);
    }

    public void drawThirdTrack(Canvas canvas) {
        //两个坐标形成变量，规定了渐变的方向和间距大小，着色器为镜像
        LinearGradient linearGradient = new LinearGradient(trackLeft, trackTop, trackLeft + trackWidth, trackBottom, Color.parseColor("#FF8647"), Color.parseColor("#FF5E45"), Shader.TileMode.MIRROR);
//        paintTTrack.setShader(linearGradient);
        //取消渐变色，换成同一颜色
        paintTTrack.setColor(colorTTrack);
        Rect src = null;
        Rect dst = null;


        if (thumbLeft < (trackLeft)) {
            canvas.drawLine(trackLeft + trackHeight, trackTop, trackLeft + trackHeight, trackBottom, paintTTrack);
        } else {

            canvas.drawLine(trackLeft + trackHeight, trackTop, thumbLeft + trackLeft + 1, trackBottom, paintTTrack);
        }
        if (model == 0) {
            // draw thumb
            src = new Rect(0, 0, thumbBitmap.getWidth(), thumbBitmap.getHeight());
            dst = new Rect((int) (thumbLeft + trackLeft), (int) (thumbTop), (int) (thumbLeft + trackLeft + thumbWidth), (int) (thumbTop + thumbHeight));
        } else if (model == 1) {
            src = new Rect(0, 0, thumbBitmap.getWidth(), thumbBitmap.getHeight());
            dst = new Rect((int) (thumbLeft), (int) (thumbTop), (int) (thumbLeft + thumbWidth), (int) (thumbTop + thumbHeight));

        }
        canvas.drawBitmap(thumbBitmap, src, dst, null);


    }


    boolean mOnlyThumbDraggable;//only drag the seek bar's thumb can be change the progress

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float mX = event.getX();
                if (isTouchSeekBar(mX, event.getY())) {
                    if ((mOnlyThumbDraggable && !isTouchThumb(mX))) {
                        return false;
                    }
                    isTouching = true;
                    if (mSeekChangeListener != null) {
                        mSeekChangeListener.onStartTrackingTouch(this);
                    }
                    refreshSeekBar(event);
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                refreshSeekBar(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                if (mSeekChangeListener != null) {
                    mSeekChangeListener.onStopTrackingTouch(this);
                }
//                if (!autoAdjustThumb()) {
                showLoading();
                invalidate();

//                }

                break;
        }
        return super.onTouchEvent(event);
    }


    private boolean isTouchThumb(float mX) {
        float rawTouchX;
        refreshThumbCenterXByProgress(currentProgress);

        rawTouchX = thumbLeft;

        return rawTouchX - thumbWidth / 2f <= mX && mX <= rawTouchX + thumbWidth / 2f;
    }

    private void refreshThumbCenterXByProgress(float progress) {
        //ThumbCenterX
        if (model == 0) {
            thumbLeft = (int) ((progress / max) * ((trackWidth - thumbWidth) * 1.0f));
        } else if (model == 1) {
            thumbLeft = (int) ((progress / max) * ((trackWidth) * 1.0f));

        }

    }


    private boolean isTouchSeekBar(float mX, float mY) {
        if (mFaultTolerance == -1) {
            mFaultTolerance = px2Dip(5);
        }
        boolean inWidthRange = mX >= (paddingLeft - 2 * mFaultTolerance) && mX <= (width - paddingRigtht + 2 * mFaultTolerance);
        boolean inHeightRange = mY >= (trackTop - thumbHeight / 2 - mFaultTolerance) && mY <= (trackTop + thumbHeight / 2 + mFaultTolerance);
        return inWidthRange && inHeightRange;
    }


    public void setOnSeekChangeListener(@NonNull SeekChangeListener listener) {
        this.mSeekChangeListener = listener;
    }

    private void setSeekListener(boolean formUser) {
        if (mSeekChangeListener == null) {
            return;
        }
        if (progressChange()) {
            mSeekChangeListener.onSeeking(collectParams(formUser));
        }
    }

    private void refreshSeekBar(MotionEvent event) {
        refreshThumbCenterXByProgress(calculateProgress(calculateTouchX(adjustTouchX(event))));
        setSeekListener(true);
        requestLayout();
        showLoading();
        invalidate();
//        updateIndicator();
    }

    public void showLoading() {
        if (ivLoading != null) {
            ivLoading.setVisibility(VISIBLE);
            if (rotate == null) {
                rotate = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 359f).setDuration(1000);
                rotate.setInterpolator(new LinearInterpolator());
                rotate.setRepeatCount(ObjectAnimator.INFINITE);
                rotate.start();
            }
            requestLayout();
        }

    }

    public void hideLoading() {
        if (rotate != null) {
            rotate.cancel();
            if (ivLoading != null) {
                ivLoading.setVisibility(GONE);
            }
        }
    }

    private float adjustTouchX(MotionEvent event) {
        float mTouchXCache;
        if (event.getX() < paddingLeft) {
            mTouchXCache = 0;
        } else if (event.getX() > trackWidth) {
            mTouchXCache = trackWidth;
        } else {
            mTouchXCache = event.getX();
        }
        return mTouchXCache;
    }

    private float calculateProgress(float touchX) {
        lastProgress = currentProgress;
        currentProgress = (min + (getAmplitude())) * (touchX) / (trackWidth * 1.0f);
        Log.e("xw", "seeke currentProgress:" + currentProgress);
        return currentProgress;
    }


    private float calculateTouchX(float touchX) {
        float touchXTemp = touchX;
        //make sure the seek bar to seek smoothly always
        // while the tick's count is less than 3(tick's count is 1 or 2.).

        return touchXTemp;
    }

    private float getAmplitude() {
        return (max - min) > 0 ? (max - min) : 1;
    }


    private boolean progressChange() {
        return lastProgress != currentProgress;

    }

    SeekParams mSeekParams;

    private SeekParams collectParams(boolean formUser) {
        if (mSeekParams == null) {
            mSeekParams = new SeekParams(this);
        }
        mSeekParams.progress = getProgress();
        mSeekParams.progressFloat = getProgressFloat();
        mSeekParams.fromUser = formUser;
        //for discrete series seek bar

        return mSeekParams;
    }

    public int getProgress() {
        return Math.round(currentProgress);
    }

    public synchronized float getProgressFloat() {
        BigDecimal bigDecimal = BigDecimal.valueOf(currentProgress);
        return bigDecimal.setScale(mScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }


    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(float currentProgress) {
        if (canUpdate) {
            this.currentProgress = currentProgress;
            refreshThumbCenterXByProgress(currentProgress);
            invalidate();
        }
    }

    public ImageView getLoadingView() {
        return ivLoading;
    }

    public void setLoadingView(ImageView ivLoading) {
        this.ivLoading = ivLoading;
    }

    public void setSecondProgress(int percent) {
        secondWidth = ((trackWidth) * (percent / 100.0f));
        invalidate();
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    /**
     * 数字转化为dp
     * @param px
     * @return
     */
    private int px2Dip(int px) {
        return getContext().getResources().getDimensionPixelSize(R.dimen.base_dip) * px;
    }
}
