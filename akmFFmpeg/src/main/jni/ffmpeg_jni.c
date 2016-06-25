#include <string.h>
#include <jni.h>
#include <ffmpeg.h>

#ifdef ANDROID
#include <jni.h>
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "error", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "info", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  LOGE("error:" format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  LOGE("info:" format "\n", ##__VA_ARGS__)
#endif


int ffmpegmain(int argc, char **argv);

//Output FFmpeg's av_log()
void custom_log(void *ptr, int level, const char* fmt, va_list vl){

	//To TXT file

	FILE *fp=fopen("/storage/emulated/0/av_log.txt","a+");
	if(fp){
		vfprintf(fp,fmt,vl);
		fflush(fp);
		fclose(fp);
	}



//
//	//To Logcat
//	LOGE(s, vl);
}

JNIEXPORT jint JNICALL Java_com_akm_ffmpeg_FFmpegUtils_ffmpegmain( JNIEnv * env, jobject thiz, jint cmdnum, jobjectArray cmdline)
{

  //FFmpeg av_log() callback
  av_log_set_callback(custom_log);
//  //在c代码里面调用java代码里面的方法
//  	    // java 反射
//  	    //1 . 找到java代码的 class文件
//  	    //    jclass      (*FindClass)(JNIEnv*, const char*);
//  	    jclass dpclazz = (*env)->FindClass(env,"com/akm/ffmpeg/FFmpegUtils");
//  	    if(dpclazz==0){
//  	        LOGI("find class error");
//  	        return;
//  	    }
//  	    LOGI("find class ");
//
//  	    //2 寻找class里面的方法
//  	    //   jmethodID   (*GetMethodID)(JNIEnv*, jclass, const char*, const char*);
//  	  jmethodID method1 = (*env)->GetStaticMethodID(env,dpclazz,"callBack","(Ljava/lang/String;)V");
//  	    if(method1==0){
//  	        LOGI("find method1 error");
//  	        return;
//  	    }
//  	    LOGI("find method1 ");
//  	    //3 .调用这个方法
//  	    //    void        (*CallVoidMethod)(JNIEnv*, jobject, jmethodID, ...);
//  	    (*env)->CallStaticVoidMethod(env,dpclazz,method1,(*env)->NewStringUTF(env,"ffmpeg"));


  int argc=cmdnum;
  char** argv=(char**)malloc(sizeof(char*)*argc);
  
  int i=0;
  for(i=0;i<argc;i++)
  {
    jstring string=(*env)->GetObjectArrayElement(env,cmdline,i);
    const char* tmp=(*env)->GetStringUTFChars(env,string,0);
    argv[i]=(char*)malloc(sizeof(char)*1024);
    strcpy(argv[i],tmp);
  }
  
  int result = ffmpegmain(argc,argv);

  for(i=0;i<argc;i++){
    free(argv[i]);
  }
  free(argv);
  return result;

}
