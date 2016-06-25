package com.akm.ffmpeg;

public class FFmpegUtils {
	public static native int ffmpegmain(int argc,String[] argv);
    static{
    	System.loadLibrary("avutil-54");
    	System.loadLibrary("swresample-1");
    	System.loadLibrary("avcodec-56");
    	System.loadLibrary("avformat-56");
    	System.loadLibrary("swscale-3");
    	System.loadLibrary("postproc-53");
    	System.loadLibrary("avfilter-5");
    	System.loadLibrary("avdevice-56");
    	System.loadLibrary("akmffmpeg");
    }
    
    public static void callBack(String v){
    	System.out.println(">>>>>>>>>>>>>>call back..."+v);
    	
    }
}
