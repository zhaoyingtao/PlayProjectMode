package com.snow.playl.seekbar;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-09-17
 * desc   :
 */
public class SeekParams {

    public SeekParams(MusicSeekBar seekBar) {
        this.seekBar = seekBar;
    }

    //for continuous series seek bar
    // The SeekBar whose progress has changed
    public MusicSeekBar seekBar;
    //The current progress level.The default value for min is 0, max is 100.
    public int progress;
    //The current progress level.The default value for min is 0.0, max is 100.0.
    public float progressFloat;
    //True if the progress change was initiated by the user, otherwise by setProgress() programmatically.
    public boolean fromUser;

    //for discrete series seek bar
    //the thumb location on tick when the section changed, continuous series will be zero.
    public int thumbPosition;
    //the text below tick&thumb when the section changed.
    public String tickText;
}
