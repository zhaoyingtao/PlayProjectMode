package com.snow.playl.view;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snow.playl.R;
import com.snow.playl.manager.PlayVideoManager;
import com.snow.playl.util.BrightnessHelper;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-06-10
 * desc   :播放控件View
 */
public class BaseVideoPlayView extends VideoGestureRelativeLayout
        implements VideoGestureRelativeLayout.VideoGestureListener {

    TextureView textureView;
    public View maskView;//遮层
    ImageView ivCover;
    RelativeLayout rlLoading;
    VideoAttributeLayout valView;
    LinearLayout ivReplay;
    RelativeLayout replayRl;
    RelativeLayout rlRoot;
    private Context mContext;

    //声音管理
    private AudioManager systemAudioManager;
    //亮度管理
    private BrightnessHelper mBrightnessHelper;
    private int maxVolume = 0;
    private int oldVolume = 0;
    private int newProgress = 0, oldProgress = 0;
    private float brightness = 1;
    private int movePosition = 0;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;

    public BaseVideoPlayView(Context context) {
        this(context, null);
    }

    public BaseVideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
        initListener();
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_base_video_play_layout, this);
        initView(view);
        //初始化获取音量属性
        systemAudioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        maxVolume = systemAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //初始化亮度调节
        mBrightnessHelper = new BrightnessHelper(mContext);
        //下面这是设置当前APP亮度的方法配置
        mWindow = ((Activity) mContext).getWindow();
        mLayoutParams = mWindow.getAttributes();
        brightness = mLayoutParams.screenBrightness;

        ivReplay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayVideoManager.init().rePlay();
                replayRl.setVisibility(GONE);
            }
        });
    }


    private void initView(View view) {
        textureView = view.findViewById(R.id.textureView);
        maskView = view.findViewById(R.id.mask_view);
        ivCover = view.findViewById(R.id.iv_cover);
        rlLoading = view.findViewById(R.id.rl_loading);
        valView = view.findViewById(R.id.val_view);
        ivReplay = view.findViewById(R.id.iv_replay);
        replayRl = view.findViewById(R.id.replay_rl);
        rlRoot = view.findViewById(R.id.rl_root);
    }

    /**
     * 开始播放视频
     */
    public void playVideo(String playUrl) {
        PlayVideoManager.init().playVideo(playUrl);
    }

    /**
     * 显示视频封面===因为需要加载图片所以取消
     */
    private void showVideoCover(String VideoCover) {
        rlLoading.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(VideoCover)) {
//            ImageUtil.imageLoad(mContext, VideoCover, ivCover);
        }
    }

    /**
     * 隐藏视频封面和加载提示
     */
    public void hindVideoCoverLoading() {
        rlLoading.setVisibility(GONE);
    }

    /**
     * 隐藏再次播放的提示按钮
     */
    public void hindReplayView() {
        replayRl.setVisibility(GONE);
    }

    /**
     * 播放完成显示
     */
    public void playComplementShowView() {
        replayRl.setVisibility(VISIBLE);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        setVideoGestureListener(this);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface s = new Surface(surface);
                PlayVideoManager.init().getAliyunVodPlayer().setSurface(s);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                PlayVideoManager.init().getAliyunVodPlayer().redraw();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                PlayVideoManager.init().getAliyunVodPlayer().setSurface(null);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        dealBrightnes(e1, e2);
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        dealVolume(e1, e2);
    }

    @Override
    public void onFF_REWGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!PlayVideoManager.init().isPlaying()) {
            return;
        }
        float offset = e2.getX() - e1.getX();
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
            newProgress = (int) (oldProgress + offset / rlRoot.getWidth() * 100);
            if (newProgress > 100) {
                newProgress = 100;
            }
        } else {
            newProgress = (int) (oldProgress + offset / rlRoot.getWidth() * 100);
            if (newProgress < 0) {
                newProgress = 0;
            }
        }
        int duration = (int) PlayVideoManager.init().getPlayDuration();
        String durationTime = formatTime(duration);
        double pro = newProgress / 100.00D;
        movePosition = (int) (duration * pro);
        String moveTime = formatTime(movePosition);
        valView.setProgress(newProgress);
        valView.setTvTime(moveTime + "/" + durationTime);
        valView.showPrggressDialog();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        if (viewListener != null) {
            viewListener.singleTapGesture();
        }
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        if (viewListener != null) {
            viewListener.doubleTapGesture();
        }
    }

    @Override
    public void onDown(MotionEvent e) {
        if (PlayVideoManager.init().getPlayDuration() > 0) {
            oldProgress = (int) (PlayVideoManager.init().getCurrentPosition() / (PlayVideoManager.init().getPlayDuration() * 1.0f) * 100);
        }
        movePosition = 0;
        oldVolume = systemAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        brightness = mLayoutParams.screenBrightness;
        if (brightness == -1) {
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            brightness = mBrightnessHelper.getBrightness() / 255f;
        }
    }

    @Override
    public void onEndFF_REW(MotionEvent e) {
        if (newProgress != 0) {
            if (viewListener != null) {
                viewListener.onEndMoveAction(newProgress);
            }
//            sbProgress.setCurrentProgress(newProgress);
        }
        if (textureView != null && movePosition != 0) {
            PlayVideoManager.init().seekTo(movePosition);
//            mHandler.sendEmptyMessage(3);
        }
    }

    public void setBaseVideoPlayListener(BaseVideoPlayViewListener viewListener) {
        this.viewListener = viewListener;
    }

    private BaseVideoPlayViewListener viewListener;

    public interface BaseVideoPlayViewListener {
        void doubleTapGesture();

        void singleTapGesture();

        void onEndMoveAction(int newProgress);
    }

    /**
     * 处理亮度
     *
     * @param e1
     * @param e2
     */
    private void dealBrightnes(MotionEvent e1, MotionEvent e2) {
        //下面这是设置当前APP亮度的方法
        float newBrightness = (e1.getY() - e2.getY()) / rlRoot.getHeight();
        newBrightness += brightness;

        if (newBrightness < 0) {
            newBrightness = 0;
        } else if (newBrightness > 1) {
            newBrightness = 1;
        }
        mLayoutParams.screenBrightness = newBrightness;
        mWindow.setAttributes(mLayoutParams);
        valView.setProgress((int) (newBrightness * 100));
        valView.setImageResource(R.mipmap.ic_video_play_brightness);
        valView.show();
    }

    /**
     * 处理音量
     *
     * @param e1
     * @param e2
     */
    private void dealVolume(MotionEvent e1, MotionEvent e2) {
        int value = rlRoot.getHeight() / maxVolume;
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);
        systemAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
        //要强行转Float类型才能算出小数点，不然结果一直为0
        int volumeProgress = (int) (newVolume / Float.valueOf(maxVolume) * 100);
        if (volumeProgress >= 50) {
            valView.setImageResource(R.mipmap.icon_videoplay_volume_higher);
        } else if (volumeProgress > 0) {
            valView.setImageResource(R.mipmap.icon_videoplay_volume_lower);
        } else {
            valView.setImageResource(R.mipmap.icon_videoplay_volume_off);
        }
        valView.setProgress(volumeProgress);
        valView.show();
    }

    /**
     * 时间转化
     *
     * @param ms
     * @return
     */
    private String formatTime(int ms) {
        int totalSeconds = ms / 1000;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 60 / 60;
        String timeStr = "";
        if (hours > 9) {
            timeStr += hours + ":";
        } else if (hours > 0) {
            timeStr += "0" + hours + ":";
        }
        if (minutes > 9) {
            timeStr += minutes + ":";
        } else if (minutes > 0) {
            timeStr += "0" + minutes + ":";
        } else {
            timeStr += "00:";
        }
        if (seconds > 9) {
            timeStr += seconds;
        } else if (seconds > 0) {
            timeStr += "0" + seconds;
        } else {
            timeStr += "00";
        }

        return timeStr;
    }
}
