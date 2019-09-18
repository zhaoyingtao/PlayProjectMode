package com.snow.playl.seekbar;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-09-17
 * desc   :
 */
public interface SeekChangeListener {

    void onSeeking(SeekParams seekParams);

    void onStartTrackingTouch(MusicSeekBar seekBar);

    void onStopTrackingTouch(MusicSeekBar seekBar);


}