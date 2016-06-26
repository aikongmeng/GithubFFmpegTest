package com.example.magiclen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.magiclen.magiccommand.CommandListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
	protected static final int WHAT_BEGIN = 0x002323;
	protected static final int WHAT_RUNNING = 0x002389;
	private TextView info;
	private EditText cmd;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView(); 

	}
	private void initView() {
		info = (TextView)findViewById(R.id.text_info);
		cmd = (EditText)findViewById(R.id.edit_cmd); 
	}

	public void onClick(View v){

		ffmpegmain(); 
	}
	public void onClick2(View v){

		ffmpegmainOld(); 
	}

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			if(msg.what==WHAT_BEGIN)
				info.setText("BEGIN"); 
			else if(msg.what ==WHAT_RUNNING){
				info.append("\n");
				info.append(String.valueOf(msg.obj));
			}else if(msg.what==-1){
				info.setText("執行失敗"); 
			}else if(msg.what==1){
				info.append(">>>>>執行完畢<<<<<");
			}
		};
	};


	private void ffmpegmain(){
		new Thread(){
			@Override
			public void run() {
				try {
					//commands
					String[] cmds = cmd.getText().toString().split(" "); 
					File fileBinDir = new File(MainActivity.this.getFilesDir().getParentFile(), "lib"); //FFmpeg執行檔的所在目錄
					File fileBin = new File(fileBinDir, "libffmpeg.so"); //FFmpeg執行檔的檔案路徑

					if(!fileBin.exists()){
						System.out.println("err,file no found..."); 
						return;
					} 
					//get absolute path rwxr-xr-x 
					Runtime.getRuntime().exec("chmod 755"+fileBin.getAbsolutePath()).waitFor(); 
					String ffmpegBin = fileBin.getCanonicalPath(); 
					//enable Permissions -rxw
					Runtime.getRuntime().exec("chmod 700 " + ffmpegBin); 
					//File fileExec = new File(ffmpegBin).getParentFile();  
					org.magiclen.magiccommand.Command command = new org.magiclen.magiccommand.Command(cmds); 
					command.setDefaultDirectory(fileBinDir);
					command.setCommandListener(new CommandListener() {

						@Override
						public void commandStart(String arg0) {
							System.out.println("command start "+arg0);					
						}

						@Override
						public void commandRunning(String arg0, String arg1, boolean arg2) {
							System.out.println("command running "+arg0); 
							Message msg = Message.obtain();
							msg.obj = arg1;
							msg.what = WHAT_RUNNING;
							handler.sendMessage(msg); 
						}

						@Override
						public void commandException(String arg0, Exception arg1) {
							System.out.println("command err "+arg0+",-->"+arg1);						
						}

						@Override
						public void commandEnd(String arg0, int arg1) {
							System.out.println("command end "+arg0);						
						}
					});
					command.run();
				}

				catch (Exception e) {
					System.out.println("err:"+e.getLocalizedMessage());
					e.printStackTrace();
				} 
			}
		}.start();



	}

	private void ffmpegmainOld(){
		new Thread(){public void run() {

			try {
				String[] cmds = cmd.getText().toString().split(" ");  

				File fileBinDir = new File(MainActivity.this.getFilesDir().getParentFile(), "lib"); //FFmpeg執行檔的所在目錄
				File fileBin = new File(fileBinDir, "libffmpeg.so"); //FFmpeg執行檔的檔案路徑

				ProcessBuilder pb = new ProcessBuilder(Arrays.asList(cmds)); //cmds為Command Line的指令，例如某行指令為：「libffmpeg.so　-h」cmds的List內容就是["libffmpeg.so","-h"]
				pb.directory(fileBinDir); //將工作目錄移到FFmpeg執行檔的所在目錄，不過還是建議在cmds中以絕對路徑來表示libffmpeg.so
				pb.redirectErrorStream(true); //將FFmpeg的標準輸出和錯誤輸出合併成同一個inputStream

				//執行行程
				Process process = pb.start(); 
				StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
				outputGobbler.start();
				int exitVal = process.waitFor();
				if (exitVal != 0) {
					//在這裡通知主執行緒Process執行失敗
					System.out.println("執行失敗...");
					handler.sendEmptyMessage(-1);
				} else { 
					System.out.println("執行成功...");
					handler.sendEmptyMessage(1);
					//在這裡通知主執行緒Process執行成功
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} 

		};}.start();

	}

	class StreamGobbler extends Thread {
		InputStream is;

		StreamGobbler(InputStream is) {
			this.is = is;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null)
					Log.i("FFmpeg", line);
				Log.i("FFmpeg", "Finish");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	} 
}
