package com.snow.play;

import android.content.Intent;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snow.playl.interfaze.MusicPlayStateListenerHelper;
import com.snow.playl.manager.PlayAudioManager;
import com.snow.playl.seekbar.MusicSeekBar;
import com.snow.playl.seekbar.SeekChangeListenerHelper;

public class MainActivity extends AppCompatActivity {
    public static final String TEST_AUDIO = "https://cdn.changguwen.com/cms/media/201894/48304e17-f4ef-423e-b593-236987ce79e0-1536040343800.mp3";
    ImageView imageView;


    private TextView tv_now_time;
    private TextView tv_total_time;
    private MusicSeekBar sbProgress;
    private int totalDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NomalPlayVideoAct.class));
            }
        });
        findViewById(R.id.button02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FullPlayVideoAct.class));
            }
        });


        PlayAudioManager.init().playMusic(TEST_AUDIO);

        //音乐播放器相关
        imageView = findViewById(R.id.iv_music);
        tv_now_time = findViewById(R.id.tv_now_time);
        sbProgress = findViewById(R.id.sb_progress);
        tv_total_time = findViewById(R.id.tv_total_time);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayAudioManager.init().isPlaying()) {
                    PlayAudioManager.init().pauseMusic();
                    imageView.setBackgroundResource(R.mipmap.ic_play_yellow_pause);
                } else {
                    imageView.setBackgroundResource(R.mipmap.ic_play_yellow_play);
                    PlayAudioManager.init().continueMusic();
                }
            }
        });
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
                PlayAudioManager.init().seekTo(seekBar.getProgress());
            }
        });
        PlayAudioManager.init().setMediaPLayerPlayListener(new MusicPlayStateListenerHelper() {
            @Override
            public void startPlayMusic() {
                totalDuration = (int) PlayAudioManager.init().getPlayDuration();
                tv_total_time.setText(formatTime(totalDuration));
            }

            @Override
            public void playProgress(long currentPlayTime) {
                showVideoProgressInfo(currentPlayTime);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayAudioManager.init().pauseMusic();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
