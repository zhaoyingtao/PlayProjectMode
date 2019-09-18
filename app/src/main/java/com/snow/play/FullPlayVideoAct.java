package com.snow.play;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.snow.playl.interfaze.VideoPlayStateListenerHelper;
import com.snow.playl.manager.PlayVideoManager;
import com.snow.playl.seekbar.MusicSeekBar;
import com.snow.playl.seekbar.SeekChangeListenerHelper;
import com.snow.playl.view.BaseVideoPlayView;


/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-06-10
 * desc   :横 全屏 播放视频
 */
public class FullPlayVideoAct extends AppCompatActivity {
    BaseVideoPlayView videoPlayView;

    public static final String TEST_VIDEO = "https://cdn.changguwen.com/cms/media/20181024/0dbfb433-d976-4c54-9026-272ca2967f46-1540367091203-sd.mp4";
    public static final String TEST_AUDIO = "https://cdn.changguwen.com/cms/media/201894/48304e17-f4ef-423e-b593-236987ce79e0-1536040343800.mp3";
    private TextView tv_now_time;
    private TextView tv_total_time;
    private MusicSeekBar sbProgress;
    private int totalDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_full_play_video);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        videoPlayView = findViewById(R.id.video_play_view);
        tv_now_time = findViewById(R.id.tv_now_time);
        sbProgress = findViewById(R.id.sb_progress);
        tv_total_time = findViewById(R.id.tv_total_time);

        initData();
        initListener();
    }

    private void initListener() {
        sbProgress.setOnSeekChangeListener(new SeekChangeListenerHelper() {
            @Override
            public void onStartTrackingTouch(MusicSeekBar seekBar) {
                super.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(MusicSeekBar seekBar) {
                sbProgress.setCanUpdate(true);
                PlayVideoManager.init().seekTo(seekBar.getProgress());
            }
        });
        videoPlayView.setBaseVideoPlayListener(new BaseVideoPlayView.BaseVideoPlayViewListener() {
            @Override
            public void doubleTapGesture() {
                if (PlayVideoManager.init().isPlaying()) {
                    PlayVideoManager.init().pause();
                } else {
                    PlayVideoManager.init().playOrPause();
                }
            }

            @Override
            public void singleTapGesture() {

            }

            @Override
            public void onEndMoveAction(int newProgress) {

            }
        });
    }

    private void initData() {
        PlayVideoManager.init().playVideo(TEST_VIDEO);
        PlayVideoManager.init().setMediaPLayerPlayListener(new VideoPlayStateListenerHelper() {
            @Override
            public void startPlayMusic() {
                totalDuration = (int) PlayVideoManager.init().getPlayDuration();
                tv_total_time.setText(formatTime(totalDuration));
                videoPlayView.hindVideoCoverLoading();
            }

            @Override
            public void playProgress(long currentPlayTime) {
                showVideoProgressInfo(currentPlayTime);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayVideoManager.init().stop();
    }

    /**
     * 更新进度
     */

    private void showVideoProgressInfo(long curPosition) {
        tv_now_time.setText(formatTime((int) curPosition));
        sbProgress.setMax(totalDuration);
        sbProgress.setCurrentProgress((int) curPosition);
    }

    public String formatTime(int ms) {
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
