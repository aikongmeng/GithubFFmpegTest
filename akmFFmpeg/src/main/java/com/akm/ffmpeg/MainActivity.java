package com.akm.ffmpeg;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

	protected static final int WHAT_BEGIN = 0x002323;
	private TextView info;
	private EditText cmd;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	} 

	private void init() {
		initView(); 
	} 
	private void initView() {
		info = (TextView)findViewById(R.id.text_info);
		cmd = (EditText)findViewById(R.id.edit_cmd); 
	}

	public void onClick(View v){

		ffmpegrun();

	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			if(msg.what==WHAT_BEGIN)
				info.setText("BEGIN"); 
			else
				info.setText("--"+String.valueOf(msg.what));
		};
	};

	private void ffmpegrun() {
		new Thread(){ 
			public void run() {  
				String[] argv = cmd.getText().toString().split(" "); 
				int argc = argv.length;  
				handler.sendEmptyMessage(WHAT_BEGIN);
				time = System.currentTimeMillis();
				int result =FFmpegUtils.ffmpegmain(argc, argv);
				time = System.currentTimeMillis()- time;
				System.out.println("time: "+ time);
				handler.sendEmptyMessage(result);
			};
		}.start();

	}

}
