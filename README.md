# PlayProjectMode
音视频依赖库,阿里高级视频播放器，mediaPlayer播放音频

[ ![Download](https://api.bintray.com/packages/zhaoyingtao/maven/play_library/images/download.svg) ](https://bintray.com/zhaoyingtao/maven/play_library/_latestVersion)

## 第一步：配置
### 1.1 在项目的build.gradle中添加：  
```
buildscript {
    repositories {
       ...
        //阿里高级播放器
        maven {
            url 'https://maven.aliyun.com/repository/releases'
        }
        maven { url 'https://jitpack.io' }
    }
}
allprojects {
    repositories {
        ...
        //阿里高级播放器
        maven {
            url 'https://maven.aliyun.com/repository/releases'
        }
        maven { url 'https://jitpack.io' }
    }
}
```  
### 1.2 在引用model比如app模块的build.gradle中添加： 
```
 implementation 'com.github.zhaoyingtao:PlayProjectMode:1.0.1'
```
### 1.3 在application中初始化（初始化阿里视频播放器需要context）：   
```
 PlayAPPConstant.init().setPlayContext(this);
```

## 第二步调用：
### 2.1 音频播放
```
关于音频播放的调用功能，看下PlayAudioManager类就明白了  

PlayAudioManager.init().playMusic(TEST_AUDIO);
//监听=====必须调用，否则不会播放
PlayAudioManager.init().setMediaPLayerPlayListener(new MusicPlayStateListenerHelper() {
            @Override
            public void startPlayMusic() {//开始播放
                totalDuration = (int) PlayAudioManager.init().getPlayDuration();
                tv_total_time.setText(formatTime(totalDuration));
            }

            @Override
            public void playProgress(long currentPlayTime) {//播放进度
                showVideoProgressInfo(currentPlayTime);
            }
        });
```  
### 2.2 视频播放  
关于视频播放的调用功能，看下PlayVideoManager类就明白了  
```
xml文件代码：
  <com.snow.playl.view.BaseVideoPlayView
        android:id="@+id/video_play_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
代码调用：
 PlayVideoManager.init().playVideo(TEST_VIDEO);
 //监听=====必须调用，否则不会播放
 PlayVideoManager.init().setPlayVideoStateListener(new VideoPlayStateListenerHelper() {
      @Override
      public void startPlayMusic() {
          totalDuration = (int) PlayVideoManager.init().getPlayDuration();
          tv_total_time.setText(formatTime(totalDuration));
          videoPlayView.hindVideoCoverLoading();
      }

      @Override
      public void playProgress(long currentPlayTime) {
          showVideoProgressInfo(currentPlayTime);
      }
 });


说明：BaseVideoPlayView类默认包括以下功能：   
1、右半边上下滑动调节音量；   
2、左半边上下滑动调节屏幕亮度；   
3、视频区域左右滑动实现快进快退；   
4、单击回调；   
5、双击暂停或者播放及回调；    


BaseVideoPlayView的回调监听：    
videoPlayView.setBaseVideoPlayListener(new BaseVideoPlayView.BaseVideoPlayViewListener() {
            @Override
            public void doubleTapGesture() {
                if (PlayVideoManager.init().isPlaying()) {
                    PlayVideoManager.init().pause();
                } else {
                    PlayVideoManager.init().playOrPause();
                }
            }

            @Override
            public void singleTapGesture() {

            }

            @Override
            public void onEndMoveAction(int newProgress) {

            }
        });   
        
 
```


感觉有用给个star支持下，谢谢！
