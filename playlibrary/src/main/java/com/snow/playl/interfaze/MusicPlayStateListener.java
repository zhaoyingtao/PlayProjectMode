package com.snow.playl.interfaze;


/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/2
 * desc   : 音频播放器监听器
 */
public interface MusicPlayStateListener {
    void bufferAndProgress(long percent);

    void playComplement();

    void playProgress(long currentPlayTime);

    void startPlayMusic();

    void againPlay();
}
