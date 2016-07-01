package com.example.magiclen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.magiclen.magiccommand.Command;
import org.magiclen.magiccommand.CommandListener;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 執行FFMPEG COMMAND LINE
 *
 * @author akm
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final int WHAT_BEGIN = 0x002320;
    protected static final int WHAT_ERROR = 0x002321;
    protected static final int WHAT_COMPLETE = 0x002322;
    protected static final int WHAT_RUNNING = 0x002323;
    private LinearLayout info_layout;
    private EditText cmd;
    private long time;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        info_layout = (LinearLayout) findViewById(R.id.text_layout);
        cmd = (EditText) findViewById(R.id.edit_cmd);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }

    public void onClick(View v) {
        info_layout.removeAllViews();
//        ffmpegmain();
        new CopyAndLoadFFmpegBinaryToAssets().execute();
    }


    private void setText(String text) {
        TextView textView = new TextView(MainActivity.this);
        textView.setText(text);
        info_layout.addView(textView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case WHAT_BEGIN:
                    setText(">>>>>>開始執行<<<<<<");
                    break;
                case WHAT_RUNNING:
                    setText(String.valueOf(msg.obj));
                    break;
                case WHAT_COMPLETE:
                    setText(">>>>>執行完畢<<<<<<\n耗時:" + String.valueOf(msg.obj) + "ms");
                    break;
                case WHAT_ERROR:
                    setText("執行失敗");
                    break;
            }
        }

        ;
    };

    private class CopyAndLoadFFmpegBinaryToAssets extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            String ffmpegBinaryName = FileUtils.ffmpegFileName;

            boolean hasFileCopied = FileUtils.copyBinaryFromAssetsToData(MainActivity.this, ffmpegBinaryName, FileUtils.ffmpegFileName);

            // make directory executable
            if (hasFileCopied) {
                String filesDirectoryPath = FileUtils.getFilesDirectory(MainActivity.this).getAbsolutePath();
                File ffmpegFile = new File(filesDirectoryPath + File.separator + FileUtils.ffmpegFileName);

                if (!ffmpegFile.canExecute()) {
                    Log.d(TAG, "FFmpeg File is not executable, trying to make it executable ...");
                    if (ffmpegFile.setExecutable(true)) {
                        return true;
                    }
                } else {
                    Log.d(TAG, "FFmpeg file is executable");
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean loaded) {
            if (loaded) {

                ffmpegmain_assets();
//                try {
////                    String cmds = FileUtils.getFFmpeg(MainActivity.this) + cmd.getText().toString();
//                    String cmds = FileUtils.getFFmpeg(MainActivity.this) + " -i /storage/emulated/0/DCIM/Camera/20160614_174328.mp4  -strict -2 /storage/emulated/0/DCIM/Camera/20160614_174328_out.mp4 ";
//                    Process process = Runtime.getRuntime().exec(cmds);
//                    if (process.waitFor() == 0) {
//                        // success
//                        String is = new String(FileUtils.inputStreamToByteArray(process.getInputStream()));
//                        Log.d(TAG, "process.getInputStream returns : " + is);
//                        setText("SUCCESS : " + is);
//                    } else {
//                        // failure
//                        String error = new String(FileUtils.inputStreamToByteArray(process.getErrorStream()));
//                        Log.d(TAG, "process.getErrorStream returns : " + error);
//                        setText("ERROR : " + error);
//                    }
//                    Log.d(TAG, "process output : " + process.waitFor());
//                } catch (IOException e) {
//                    Log.e(TAG, "IOException while getting ffmpeg version", e);
//                } catch (InterruptedException e) {
//                    Log.e(TAG, "FFmpeg command interrupted", e);
//                }
            } else {
                setText("Device Not Supported");
            }
        }
    }

    private void ffmpegmain_assets() {
        new Thread() {
            @Override
            public void run() {
                try {

                    //commands
                    File fileBin = new File(FileUtils.getFFmpeg(MainActivity.this));
                    File fileBinDir =new File(MainActivity.this.getFilesDir().getAbsolutePath());
                    String[] cmds = (FileUtils.getFFmpeg(MainActivity.this) + " " + cmd.getText().toString()).split(" ");
//                  String cmds = FileUtils.getFFmpeg(MainActivity.this) +" -version";

                    if (!fileBin.exists()) {
                        Message msg = Message.obtain();
                        msg.obj = "file not found..";
                        msg.what = WHAT_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }

                    Command command = new Command(Charset.defaultCharset(),cmds);
                    command.setDefaultDirectory(fileBinDir);
                    command.setCommandListener(new CommandListener() {

                        @Override
                        public void commandStart(String arg0) {
                            time = System.currentTimeMillis();
                            System.out.println("command start " + arg0);
                            Message msg = Message.obtain();
                            msg.obj = arg0;
                            msg.what = WHAT_BEGIN;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandRunning(String arg0, String arg1, boolean arg2) {
                            System.out.println("command running " + arg1);
                            Message msg = Message.obtain();
                            msg.obj = arg1;
                            msg.what = WHAT_RUNNING;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandException(String arg0, Exception arg1) {
                            System.out.println("command err " + arg0 + ",-->" + arg1);
                            Message msg = Message.obtain();
                            msg.obj = arg1;
                            msg.what = WHAT_ERROR;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandEnd(String arg0, int arg1) {
                            time = System.currentTimeMillis() - time;
                            System.out.println("command end " + arg0 + "-->time:" + time);
                            Message msg = Message.obtain();
                            msg.obj = time;
                            msg.what = WHAT_COMPLETE;
                            handler.sendMessage(msg);
                        }
                    });
                    command.run();
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.obj = e.getLocalizedMessage();
                    msg.what = WHAT_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void ffmpegmain() {
        new Thread() {
            @Override
            public void run() {
                try {
                    File fileBinDir = new File(MainActivity.this.getFilesDir().getParentFile(), "lib"); //FFmpeg執行檔的所在目錄
                    File fileBin = new File(fileBinDir, "libffmpeg_0.so"); //FFmpeg執行檔的檔案路徑
                    //commands
                    String[] cmds = (fileBin.getAbsolutePath() + " " + cmd.getText().toString()).split(" ");

                    if (!fileBin.exists()) {
                        Message msg = Message.obtain();
                        msg.obj = "file not found..";
                        msg.what = WHAT_ERROR;
                        handler.sendMessage(msg);
                        return;
                    }

                    org.magiclen.magiccommand.Command command = new org.magiclen.magiccommand.Command(cmds);
                    command.setDefaultDirectory(fileBinDir);
                    command.setCommandListener(new CommandListener() {

                        @Override
                        public void commandStart(String arg0) {
                            time = System.currentTimeMillis();
                            System.out.println("command start " + arg0);
                            Message msg = Message.obtain();
                            msg.obj = arg0;
                            msg.what = WHAT_BEGIN;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandRunning(String arg0, String arg1, boolean arg2) {
                            System.out.println("command running " + arg1);
                            Message msg = Message.obtain();
                            msg.obj = arg1;
                            msg.what = WHAT_RUNNING;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandException(String arg0, Exception arg1) {
                            System.out.println("command err " + arg0 + ",-->" + arg1);
                            Message msg = Message.obtain();
                            msg.obj = arg1;
                            msg.what = WHAT_ERROR;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void commandEnd(String arg0, int arg1) {
                            time = System.currentTimeMillis() - time;
                            System.out.println("command end " + arg0 + "-->time:" + time);
                            Message msg = Message.obtain();
                            msg.obj = time;
                            msg.what = WHAT_COMPLETE;
                            handler.sendMessage(msg);
                        }
                    });
                    command.runAsync();
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.obj = e.getLocalizedMessage();
                    msg.what = WHAT_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
