package com.snow.play;

import android.app.Application;

import com.snow.playl.PlayAPPConstant;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-09-17
 * desc   :
 */
public class AppApliction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlayAPPConstant.init().setPlayContext(this);
    }
}
