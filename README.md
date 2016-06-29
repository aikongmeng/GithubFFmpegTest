# GithubFFmpegTest
ffmpeg command line 測試

##akmffmpeg
修改ffmpeg.c,添加jni.
基於預編譯庫編譯新的庫.
參考鏈接:
[simplest_ffmpeg_android_transcoder](https://github.com/leixiaohua1020/simplest_ffmpeg_mobile/tree/master/simplest_ffmpeg_android_transcoder)

##magiclenDemo
ProcessBuilder使用編譯好的libffmpeg.so  
遺留問題:
android 6.0 報:only position independent executables(PIE) are supported. 

參考鏈接: 
https://magiclen.org/android-ffmpeg/

http://ffmpeg.gusari.org/viewtopic.php?f=8&t=1181
