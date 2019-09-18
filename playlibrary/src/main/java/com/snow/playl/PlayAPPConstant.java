package com.snow.playl;

import android.content.Context;

/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019-09-17
 * desc   :
 */
public class PlayAPPConstant {
    private static PlayAPPConstant playAPPConstant;
    private static Context mContext;

    public static PlayAPPConstant init() {
        if (playAPPConstant == null) {
            synchronized (PlayAPPConstant.class) {
                if (playAPPConstant == null) {
                    playAPPConstant = new PlayAPPConstant();
                }
            }
        }
        return playAPPConstant;
    }

    public void setPlayContext(Context context) {
        mContext = context;
    }

    public Context getPlayContext() {
        return mContext;
    }
}
