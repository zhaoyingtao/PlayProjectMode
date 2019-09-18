package com.snow.playl.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snow.playl.R;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/3
 * desc   : 阿里视频播放器 --view视频亮度声音显示
 */

public class VideoAttributeLayout extends RelativeLayout {
    private ImageView iv_center;
    private ProgressBar pb;
    private TextView textView;
    private HideRunnable mHideRunnable;
    private int duration = 100;

    public VideoAttributeLayout(Context context) {
        super(context);
        init(context);
    }

    public VideoAttributeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_video_play_attr, this);
        iv_center = findViewById(R.id.iv_center);
        pb = findViewById(R.id.pb);
        textView = findViewById(R.id.tv_time);
        mHideRunnable = new HideRunnable();
        this.setVisibility(GONE);
    }

    //显示
    public void show() {
        textView.setVisibility(View.GONE);
        setVisibility(VISIBLE);
        removeCallbacks(mHideRunnable);
        postDelayed(mHideRunnable, duration);
    }

    //显示
    public void showPrggressDialog() {
        textView.setVisibility(View.VISIBLE);
        setVisibility(VISIBLE);
        removeCallbacks(mHideRunnable);
        postDelayed(mHideRunnable, duration);
    }

    //设置进度
    public void setTvTime(String time) {
        textView.setText(time);
    }

    //设置进度
    public void setProgress(int progress) {
        pb.setProgress(progress);
    }

    //设置持续时间
    public void setDuration(int duration) {
        this.duration = duration;
    }

    //设置显示图片
    public void setImageResource(int resource) {
        iv_center.setImageResource(resource);
    }

    //隐藏自己的Runnable
    private class HideRunnable implements Runnable {
        @Override
        public void run() {
            VideoAttributeLayout.this.setVisibility(GONE);
        }
    }
}
