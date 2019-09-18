package com.snow.playl.manager;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.snow.playl.interfaze.MusicPlayStateListener;

import java.io.IOException;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/2
 * desc   : 音频播放管理类
 */

public class PlayAudioManager {
    private static PlayAudioManager avManager;
    //不写静态会一直是空
    private static MusicPlayStateListener playerListener;
    //音频播放器
    private static MediaPlayer mPlayer;

    public static PlayAudioManager init() {
        if (avManager == null) {
            synchronized (PlayAudioManager.class) {
                if (avManager == null) {
                    avManager = new PlayAudioManager();
                }
            }
        }
        return avManager;
    }

    public PlayAudioManager() {
        initPlayer();
    }

    /**
     * 初始化音乐播放器
     */
    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;//默认返回false
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (playerListener != null) {
                    playerListener.playComplement();
                }
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (playerListener != null) {
                    if (mPlayer != null) {
                        mPlayer.start();
                        startLinstener();
                    }
                    playerListener.startPlayMusic();
                }
            }
        });
        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //缓冲进度
                if (playerListener != null) {
                    playerListener.bufferAndProgress(percent);
                }
            }
        });
    }

    /**
     * 获得播放MediaPlayer
     *
     * @return
     */
    public MediaPlayer getMusicPlayer() {
        if (mPlayer == null) {
            initPlayer();
        }
        return mPlayer;
    }

    /**
     * 播放音乐
     *
     * @param mp3Url 可以是url也可以是本地的文件路径
     */
    public void playMusic(final String mp3Url) {
        mPlayer.reset();
        if (TextUtils.isEmpty(mp3Url)) {
            return;
        }
        try {
            mPlayer.setDataSource(mp3Url);
            mPlayer.prepareAsync();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mPlayer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否在播放音乐
     */
    public boolean isPlaying() {
        if (mPlayer == null) {
            return false;
        }
        return mPlayer.isPlaying();
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mPlayer.reset();
        handler.removeMessages(9999);
    }

    /**
     * 暂停音乐
     */
    public void pauseMusic() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.pause();
        handler.removeMessages(9999);
    }

    /**
     * 继续播放音乐
     */
    public void continueMusic() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.getCurrentPosition() < 0) {//音乐并不是暂停后的，需要重新播放
            if (playerListener != null) {
                playerListener.againPlay();
            }
        } else {
            mPlayer.start();
            startLinstener();
        }
    }

    /**
     * 快进
     */
    public void seekTo(int targetTime) {
        if (mPlayer != null && targetTime >= 0) {
            mPlayer.seekTo(targetTime);
        }
    }

    /**
     * 播放或者暂停
     */
    public void playOrPause() {
        if (isPlaying()) {
            pauseMusic();
        } else {
            continueMusic();
        }
    }

    /**
     * 获取总时长
     *
     * @return
     */
    public long getPlayDuration() {
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    private void startLinstener() {
        handler.removeMessages(9999);
        handler.sendEmptyMessage(9999);
    }

    //播放进度监听
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 9999:
                    if (playerListener != null) {
                        playerListener.playProgress(mPlayer.getCurrentPosition());
                    }
                    handler.sendEmptyMessageDelayed(9999, 1000);
                    break;
            }
            return false;
        }
    });

    /**
     * 音乐播放相关监听
     *
     * @param playListener
     */
    public void setMediaPLayerPlayListener(MusicPlayStateListener playListener) {
        playerListener = playListener;
    }
}
