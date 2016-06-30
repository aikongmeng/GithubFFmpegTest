package com.example.magiclen;


import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by toge on 16/6/30.
 *
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static final String ffmpegFileName = "libffmpeg.so";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    public static byte[] inputStreamToByteArray(InputStream input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        int n;
        try {
            while(EOF != (n = input.read(buffer)))
                output.write(buffer, 0, n);
            close(input);
            return output.toByteArray();
        } catch(IOException e) {
            Log.e(TAG, "FileUtils: IOException in converting inputStreamToByteArray", e);
        }
        return null;
    }

    /**
     * 複製文件到/data/data/xxx/files
     * @param context
     * @param fileNameFromAssets assets文件名
     * @param outputFileName  輸出文件名
     * @return
     */
    public static boolean copyBinaryFromAssetsToData(Context context, String fileNameFromAssets, String outputFileName) {

        // create files directory under /data/data/package name
        File filesDirectory = getFilesDirectory(context);

        if(new File(filesDirectory,outputFileName).exists()){
            return true;
        }

        InputStream is;
        try {
            is = context.getAssets().open(fileNameFromAssets);
            // copy ffmpeg file from assets to files dir
            final FileOutputStream os = new FileOutputStream(new File(filesDirectory, outputFileName));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            int n;
            while(EOF != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
            }

            close(os);
            close(is);

            return true;
        } catch (IOException e) {
            Log.e(TAG, "issue in coping binary from assets to data. ", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 執行目錄
     * @param context
     * @return
     */
    public static File getFilesDirectory(Context context) {
        // creates files directory under data/data/package name
        return context.getFilesDir();
    }

    public static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    public static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                // Do nothing
                e.printStackTrace();
            }
        }
    }

    /**
     * 獲取到ffmepg絕對路徑
     * @param context
     * @return
     */
    public static String getFFmpeg(Context context) {
        return getFilesDirectory(context).getAbsolutePath() + File.separator + FileUtils.ffmpegFileName;
    }
}