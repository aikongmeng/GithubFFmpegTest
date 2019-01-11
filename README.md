# ffmpeg demo
 在Android上執行ffmpeg command line.

#### akmffmpeg

修改ffmpeg.c,添加jni.

基於預編譯庫編譯新的庫.  

#### magiclenDemo
ProcessBuilder使用編譯好的libffmpeg.so 执行ffmpeg command line 

[DEMO截图](https://github.com/aikongmeng/GithubFFmpegTest/blob/master/magiclenDemo/device-2016-06-30-154941.png)


###### 參考鏈接

* [simplest_ffmpeg_android_transcoder](https://github.com/leixiaohua1020/simplest_ffmpeg_mobile/tree/master/simplest_ffmpeg_android_transcoder)

* [ffmpeg command document](http://ffmpeg.org/ffmpeg.html)

* [MyActivtiy.java](https://github.com/aikongmeng/SampleFFmpegApp/blob/master/app/src/main/java/com/vinsol/androidffmpeg/sampleffmpegapp/MyActivity.java)

* [android-ffmpeg](https://magiclen.org/android-ffmpeg/)

* [question](http://ffmpeg.gusari.org/viewtopic.php?f=8&t=1181)

### ffmpeg 常用命令
###### 视频转图片
>ffmpeg -i /Users/Arjun/Downloads/a.mov -r 5 -f /Users/Arjun/Downloads/image-2%d.jpeg
>ffmpeg -i /Users/Arjun/Downloads/b.mov -r 10 -q:v 2 -f image2 /Users/Arjun/Downloads/image-%d.jpeg

###### 视频截取
>ffmpeg -i /Users/Arjun/Downloads/a.mov -vcodec copy -acodec copy -ss 00:00:00.55 -to  00:00:03.52 /Users/Arjun/Downloads/b.mov -y -ss time_off 


