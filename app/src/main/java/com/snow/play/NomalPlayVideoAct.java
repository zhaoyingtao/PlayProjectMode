package com.snow.play;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.snow.playl.interfaze.VideoPlayStateListenerHelper;
import com.snow.playl.manager.PlayVideoManager;
import com.snow.playl.view.BaseVideoPlayView;


/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-06-10
 * desc   :播放视频
 */
public class NomalPlayVideoAct extends AppCompatActivity {
    BaseVideoPlayView videoPlayView;

    public static final String TEST_VIDEO = "https://cdn.changguwen.com/cms/media/20181024/0dbfb433-d976-4c54-9026-272ca2967f46-1540367091203-sd.mp4";
    public static final String TEST_AUDIO = "https://cdn.changguwen.com/cms/media/201894/48304e17-f4ef-423e-b593-236987ce79e0-1536040343800.mp3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_nomal_play_video);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initData();
    }

    private void initData() {

        videoPlayView = findViewById(R.id.video_play_view);

        String videoCover = getIntent().getStringExtra("videoCover");
//        String videoUrl = getIntent().getStringExtra("videoUrl");
        String videoName = getIntent().getStringExtra("videoName");
        PlayVideoManager.init().playVideo(TEST_VIDEO);
        PlayVideoManager.init().setMediaPLayerPlayListener(new VideoPlayStateListenerHelper(){
            @Override
            public void startPlayMusic() {
                videoPlayView.hindVideoCoverLoading();
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
}
