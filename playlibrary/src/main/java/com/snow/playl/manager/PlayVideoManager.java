package com.snow.playl.manager;

import android.text.TextUtils;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.snow.playl.PlayAPPConstant;
import com.snow.playl.interfaze.VideoPlayStateListener;


/**
 * author : zyt
 * e-mail : 632105276@qq.com
 * date   : 2019/1/2
 * desc   : 视频管理
 */

public class PlayVideoManager {
    private static PlayVideoManager videoManager;
    //ali视频播放器
    private AliPlayer aliyunVodPlayer;
    //不写静态会一直是空
    private static VideoPlayStateListener playerListener;
    private int currentPlayState;//当前播放状态
    private long currentPlayPosition;//当前播放进度
    private String lastPlayUrl;

    private PlayVideoManager() {
        initVodPlayer();
    }

    public static PlayVideoManager init() {
        if (videoManager == null) {
            synchronized (PlayVideoManager.class) {
                if (videoManager == null) {
                    videoManager = new PlayVideoManager();
                }
            }
        }
        return videoManager;
    }

    // 初始化播放器
    private void initVodPlayer() {
        aliyunVodPlayer = AliPlayerFactory.createAliPlayer(PlayAPPConstant.init().getPlayContext());
        //循环播放
//        aliyunVodPlayer.setOnCircleStartListener(new MyCircleStartListener(this));
        //准备播放监听
        aliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                if (playerListener != null) {
                    aliyunVodPlayer.start();
                }
            }
        });
        //第一帧画面展示
        aliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                if (playerListener != null) {
                    playerListener.startPlayMusic();
                }
            }
        });
        //播放错误
        aliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                //出错时处理，查看接口文档中的错误码和错误消息
            }
        });
        //播放完成监听
        aliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                if (playerListener != null) {
                    playerListener.playComplement();
                }
            }
        });
        aliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //seek完成时触发
            }
        });
        aliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int i) {
                //使用stop功能时触发
            }
        });
        aliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int newState) {
                //播放器状态改变事件
//            int prepared = 2;
//            int started = 3;
//            int paused = 4;
//            int stopped = 5;
                currentPlayState = newState;
            }
        });
        /**
         * 获取播放信息===进度、缓冲进度
         */
        aliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {//获取播放进度
                    currentPlayPosition = infoBean.getExtraValue();
                    if (playerListener != null) {
                        playerListener.playProgress(currentPlayPosition);
                    }
                } else if (infoBean.getCode() == InfoCode.BufferedPosition) {//缓冲进度
                    if (playerListener != null) {
                        playerListener.bufferAndProgress(infoBean.getExtraValue());
                    }
                }
            }
        });
        //设置画面缩放模式：宽高比填充，宽高比适应，拉伸填充
        aliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FIT);
    }

    /**
     * 播放
     */
    public void playVideo(String playUrl) {
        try {
            if (null == aliyunVodPlayer || TextUtils.isEmpty(playUrl)) {
                return;
            }
            //必须先调用stop然后在调用reset，最后调用prepareAsync，否则有时会崩溃
            aliyunVodPlayer.stop();
            aliyunVodPlayer.reset();
            UrlSource urlSource = new UrlSource();
            lastPlayUrl = playUrl;
            urlSource.setUri(playUrl);
            aliyunVodPlayer.setDataSource(urlSource);
            aliyunVodPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 精确跳转seekTo
     *
     * @return
     */
    public void seekTo(long targetPosition) {
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.seekTo(targetPosition, IPlayer.SeekMode.Accurate);
        }
    }

    /**
     * 开始或者暂停
     */
    public void playOrPause() {
        if (currentPlayState == 3) {//正在播放
            aliyunVodPlayer.pause();
        } else if (currentPlayState == 4) {//暂停
            aliyunVodPlayer.start();
        } else {
            if (playerListener != null) {
                rePlay();
                playerListener.againPlay();
            }
        }
    }

    /**
     * 获得播放player
     *
     * @return
     */
    public AliPlayer getAliyunVodPlayer() {
        if (aliyunVodPlayer == null) {
            aliyunVodPlayer = AliPlayerFactory.createAliPlayer(PlayAPPConstant.init().getPlayContext());
        }
        return aliyunVodPlayer;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return currentPlayState == 3 || currentPlayState == 2;//正在准备或者正在播放
    }


    /**
     * 暂停
     */
    public void pause() {
        if (null != aliyunVodPlayer) {
            aliyunVodPlayer.pause();
        }
    }

    /**
     * 恢复
     */
    public void resume() {
        if (null != aliyunVodPlayer) {
            aliyunVodPlayer.start();
        }
    }

    /**
     * 重新播放
     */
    public void rePlay() {
        if (null != aliyunVodPlayer) {
            playVideo(lastPlayUrl);
        }
    }

    /**
     * 结束
     */
    public void stop() {
        if (null != aliyunVodPlayer) {
            aliyunVodPlayer.reset();
            aliyunVodPlayer.stop();
        }
    }

    /**
     * 获取当前播放进度
     *
     * @return
     */
    public long getCurrentPosition() {
        return currentPlayPosition;
    }

    /**
     * 获取总时长
     *
     * @return
     */
    public long getPlayDuration() {
        return aliyunVodPlayer.getDuration();
    }

    /**
     * 音乐播放相关监听
     *
     * @param playListener
     */
    public void setMediaPLayerPlayListener(VideoPlayStateListener playListener) {
        playerListener = playListener;
    }

}
