/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.vedioPlay;

import java.util.ArrayList;

import com.sh.camera.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



public class MoviePlayer extends Activity  implements OnTouchListener{
	private Button playButton;  
	private Button prevButton;  
	private Button nextButton;  
	private SurfaceView pView;   
	private String url;   
	private MediaPlayer mediaPlayer;    
	private SeekBar seekbar;   
	private boolean flag = true;   
	private LinearLayout seekbarView;   
	private LinearLayout playbarView;
	private boolean display;   
	private Button backButton;   
	private Button menuButton;   
	private View view;   
	private upDateSeekBar update;   
	private ArrayList<String> mFileList;
	private int playIndex;
	private TextView mFileName;
	private TextView mPassTime;
	private TextView mTotalTime;

    
	private String TAG = "MoviePlayer";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);   
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		Intent intent=getIntent(); 
        
		init();  
		url = intent.getStringExtra("uri");
		mFileList = intent.getStringArrayListExtra("filelist");
		String filePath = url.replace("file://", "");
		playIndex = mFileList.indexOf(filePath);
		Log.d(TAG,"play file = "+url);
//		Log.d(TAG,"file list = "+mFileList);
		Log.d(TAG,"filePath = "+filePath);
		Log.d(TAG,"playIndex = "+playIndex);
		
		String filename = url.substring(url.lastIndexOf("/") + 1);
		mFileName.setText(filename);
		
		setListener();   
		
	}
	private void init() {
		mediaPlayer = new MediaPlayer();   
		update = new upDateSeekBar();  
		setContentView(R.layout.movie_player);   
		backButton = (Button) findViewById(R.id.back);  
		menuButton = (Button) findViewById(R.id.menu);  
		seekbar = (SeekBar) findViewById(R.id.seekbar);  
		playButton = (Button) findViewById(R.id.play);
		prevButton = (Button) findViewById(R.id.prev);
		nextButton = (Button) findViewById(R.id.next);
		pView = (SurfaceView) findViewById(R.id.mSurfaceView);
		pView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   
		pView.getHolder().setKeepScreenOn(true);   
		pView.getHolder().addCallback(new surFaceView());   
		seekbarView = (LinearLayout) findViewById(R.id.seekbarview);
		playbarView = (LinearLayout) findViewById(R.id.playbar);
		mFileName = (TextView) findViewById(R.id.file_name);
		mPassTime = (TextView) findViewById(R.id.pass_time);
		mTotalTime = (TextView) findViewById(R.id.total_time);
		
		view = findViewById(R.id.pb); 
		playButton.setOnTouchListener(this);
		backButton.setOnTouchListener(this);
		menuButton.setOnTouchListener(this);
		prevButton.setOnTouchListener(this);
		nextButton.setOnTouchListener(this);
	}
 
	private void playUrl(final String url){

		String filename = url.substring(url.lastIndexOf("/") + 1);
		mFileName.setText(filename);
		
			//mediaPlayer.start(); 
		
		
		 AsyncTask.execute(new Runnable() {
             @Override
             public void run() {	                                    
            
            	 try {
         			mediaPlayer.reset();    
         			Log.i(TAG, "next url  "+url);
         			mediaPlayer.setDataSource(url);   
         			mediaPlayer.setDisplay(pView.getHolder());  
         			//mediaPlayer.setOnPreparedListener(new Ok()); 
         			mediaPlayer.prepare();  
         		} catch (Exception e) {
         			Log.e(TAG, e.toString());
         		}    
            	 mediaPlayer.start(); 
             }
         });                     
	}		
	class PlayMovie extends Thread {   


		public PlayMovie() {


		}

		@Override
		public void run() {
			Message message = Message.obtain();
			try {
				Log.i(TAG, "runrun  "+url);
				mediaPlayer.reset();    
				mediaPlayer.setDataSource(url);   
				mediaPlayer.setDisplay(pView.getHolder());  
				mediaPlayer.setOnPreparedListener(new Ok());  
				mediaPlayer.prepare();  
			} catch (Exception e) {
				message.what = 2;
				Log.e(TAG, e.toString());
			}

			super.run();
		}
	}

	class Ok implements OnPreparedListener {

		public Ok() {

		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			Log.i(TAG, "play");
			view.setVisibility(View.GONE);  
			display = false;
			if (mediaPlayer != null) { 
				mediaPlayer.start();  
			} else {
				return;
			}
			new Thread(update).start();   
		}
	}

	private class surFaceView implements Callback {     

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {   

				new PlayMovie().start();   

		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { 
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				flag = false;
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	private void setListener() {
		mediaPlayer
				.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
					}
				});

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { 
					@Override
					public void onCompletion(MediaPlayer mp) {
						flag = false;
						playButton.setBackgroundResource(R.drawable.play);
					}
				});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {

			}
		});

		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {    
					playButton.setBackgroundResource(R.drawable.pause);
					mediaPlayer.pause();
				} else {
					if (flag == false) {
						flag = true;
						new Thread(update).start();
					}
					mediaPlayer.start();
					playButton.setBackgroundResource(R.drawable.play);

				}
			}
		});
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				int value = seekbar.getProgress() * mediaPlayer.getDuration()  
						/ seekbar.getMax();
				mediaPlayer.seekTo(value);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

		pView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (display) {
					seekbarView.setVisibility(View.GONE);
					playbarView.setVisibility(View.GONE);
					display = false;
				} else {
					seekbarView.setVisibility(View.VISIBLE);
					playbarView.setVisibility(View.VISIBLE);
					pView.setVisibility(View.VISIBLE);

					ViewGroup.LayoutParams lp = pView.getLayoutParams();
					lp.height = LayoutParams.FILL_PARENT;
					lp.width = LayoutParams.FILL_PARENT;
					pView.setLayoutParams(lp);
					display = true;
				}

			}
		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
				}
				mediaPlayer = null;
				MoviePlayer.this.finish();

			}
		});
		prevButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(playIndex == 0){
					playIndex = mFileList.size() - 1;
				}else{
					playIndex --;
				}
				Log.d(TAG,"prev file = "+mFileList.get(playIndex));
				url= (Uri.parse("file://"+mFileList.get(playIndex))).toString();
				playUrl(url);
				
				
				playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));

			}
		});
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playIndex = (playIndex+1)%mFileList.size();
				Log.d(TAG,"next file = "+mFileList.get(playIndex));
				//playUrl(mFileList.get(playIndex));
				url= (Uri.parse("file://"+mFileList.get(playIndex))).toString();
				playUrl(url);	
				
				playButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
			}
		});

	}
    public String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer == null) {
				flag = false;
			} else if (mediaPlayer.isPlaying()) {
				flag = true;
				int position = mediaPlayer.getCurrentPosition();
				int mMax = mediaPlayer.getDuration();
				int sMax = seekbar.getMax();
				seekbar.setProgress(position * sMax / mMax);
				
				int passTime = position/1000;
				int totalTime = mMax/1000;
				
				Log.d(TAG,"passTime = "+passTime);
				Log.d(TAG,"totalTime = "+totalTime);	
				
				int passHour = passTime/3600;
				int passMinute = passTime/60;
				int passSecond = passTime%60;
     
				int totalHour = totalTime/3600;
				int totalMinute = totalTime/60;
				int totalSecond = totalTime%60;
				
                mPassTime.setText(format(passHour) + ":" + format(passMinute) + ":"
                        + format(passSecond));
				mTotalTime.setText(format(totalHour) + ":" + format(totalMinute) + ":"
                        + format(totalSecond));
			} else {
				return;
			}
		};
	};

	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			mHandler.sendMessage(Message.obtain());
			if (flag) {
				mHandler.postDelayed(update, 1000);
			}
		}
	}

	@Override
	protected void onDestroy() {   
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		System.gc();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN){
			if ((v.getId() == R.id.prev)) {
				prevButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.prev_press));
			}
			if ((v.getId() == R.id.next)) {
				nextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.next_press));
			}	
			if ((v.getId() == R.id.back)) {
				backButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_press));
			}	
			if ((v.getId() == R.id.menu)) {
				menuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_press));
			}	
		}else if (event.getAction() == MotionEvent.ACTION_UP) {
			if ((v.getId() == R.id.prev)) {
				prevButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.prev));
			}
			if ((v.getId() == R.id.next)) {
				nextButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.next));
			}
			if ((v.getId() == R.id.back)) {
				backButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.back));
			}
			if ((v.getId() == R.id.menu)) {
				menuButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu));
			}	
		}
		
		return false;
	}
	
	
}

