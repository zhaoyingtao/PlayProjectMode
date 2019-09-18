package com.snow.playl.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/2
 * desc   : 阿里视频播放器 --view手势监听层
 */

public class VideoGestureRelativeLayout extends RelativeLayout {

    private static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2, FF_REW = 3;
    private
    @ScrollMode
    int mScrollMode = NONE;

    @IntDef({NONE, VOLUME, BRIGHTNESS, FF_REW})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollMode {
    }

    private VideoPlayerOnGestureListener mOnGestureListener;
    private GestureDetector mGestureDetector;
    private VideoGestureListener mVideoGestureListener;
    //横向偏移检测，让快进快退不那么敏感
    private int offsetX = 1;
    private boolean hasFF_REW = false;

    public VideoGestureRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    public VideoGestureRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (hasFF_REW) {
//                ToastUtils.getInstance().showToast("ACTION_UP");
                if (mVideoGestureListener != null) {
                    mVideoGestureListener.onEndFF_REW(event);
                }
                hasFF_REW = false;
            }

        }
        //监听触摸事件
        return mGestureDetector.onTouchEvent(event);
    }

    private void init(Context context) {
        mOnGestureListener = new VideoPlayerOnGestureListener(this);
        mGestureDetector = new GestureDetector(context, mOnGestureListener);
        //取消长按，不然会影响滑动
        mGestureDetector.setIsLongpressEnabled(false);
    }

    public class VideoPlayerOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private VideoGestureRelativeLayout videoGestureRelativeLayout;

        public VideoPlayerOnGestureListener(VideoGestureRelativeLayout videoGestureRelativeLayout) {
            this.videoGestureRelativeLayout = videoGestureRelativeLayout;
        }

        @Override
        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown: ");
            hasFF_REW = false;
            //每次按下都重置为NONE
            mScrollMode = NONE;
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onDown(e);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            switch (mScrollMode) {
                case NONE:
//                    Log.d(TAG, "NONE: ");
                    //offset是让快进快退不要那么敏感的值
                    if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
                        mScrollMode = FF_REW;
                    } else {
                        if (e1.getX() < getWidth() / 2) {
                            mScrollMode = BRIGHTNESS;
                        } else {
                            mScrollMode = VOLUME;
                        }
                    }
                    break;
                case VOLUME:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                    }
//                    Log.d(TAG, "VOLUME: ");
                    break;
                case BRIGHTNESS:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                    }
//                    Log.d(TAG, "BRIGHTNESS: ");
                    break;
                case FF_REW:
                    if (mVideoGestureListener != null) {
                        mVideoGestureListener.onFF_REWGesture(e1, e2, distanceX, distanceY);
                    }
                    hasFF_REW = true;
//                    Log.d(TAG, "FF_REW: ");
                    break;
            }
            return true;
        }


        @Override
        public boolean onContextClick(MotionEvent e) {
//            Log.d(TAG, "onContextClick: ");
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Log.d(TAG, "onDoubleTap: ");
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onDoubleTapGesture(e);
            }
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            Log.d(TAG, "onLongPress: ");
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
//            Log.d(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.d(TAG, "onFling: ");
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public void onShowPress(MotionEvent e) {
//            Log.d(TAG, "onShowPress: ");
            super.onShowPress(e);
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.d(TAG, "onSingleTapConfirmed: ");
            if (mVideoGestureListener != null) {
                mVideoGestureListener.onSingleTapGesture(e);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public void setVideoGestureListener(VideoGestureListener videoGestureListener) {
        mVideoGestureListener = videoGestureListener;
    }

    /**
     * 用于提供给外部实现的视频手势处理接口
     */

    public interface VideoGestureListener {
        //亮度手势，手指在Layout左半部上下滑动时候调用
        void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        //音量手势，手指在Layout右半部上下滑动时候调用
        void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        //快进快退手势，手指在Layout左右滑动的时候调用
        void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        //单击手势，确认是单击的时候调用
        void onSingleTapGesture(MotionEvent e);

        //双击手势，确认是双击的时候调用
        void onDoubleTapGesture(MotionEvent e);

        //按下手势，第一根手指按下时候调用
        void onDown(MotionEvent e);

        //快进快退执行后的松开时候调用
        void onEndFF_REW(MotionEvent e);
    }
}
