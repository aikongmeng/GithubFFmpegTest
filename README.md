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

1.android 6.0 報:only position independent executables(PIE) are supported. 
2.galaxy note3 lite android 4.3 
> java.lang.SecurityException: Permission Denial: get/set setting for user asks to run as user -2 but is calling from user 0; this requires android.permission.INTERACT_ACROSS_USERS_FULL
                                                   at com.android.server.am.ActivityManagerService.handleIncomingUser(ActivityManagerService.java:13466)
                                                   at android.app.ActivityManager.handleIncomingUser(ActivityManager.java:2058)
                                                   at com.android.providers.settings.SettingsProvider.callFromPackage(SettingsProvider.java:615)
                                                   at android.content.ContentProvider$Transport.call(ContentProvider.java:279)
                                                   at android.content.ContentProviderNative.onTransact(ContentProviderNative.java:273)
                                                   at android.os.Binder.execTransact(Binder.java:388)
                                                   at dalvik.system.NativeStart.run(Native Method)


參考鏈接: 

https://magiclen.org/android-ffmpeg/

http://ffmpeg.gusari.org/viewtopic.php?f=8&t=1181
