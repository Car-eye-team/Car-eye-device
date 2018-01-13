/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.service;


import java.io.File;
import java.util.Date;

import org.push.push.Pusher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore.Video;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dss.car.launcher.provider.biz.ProviderBiz;
import com.jsr.sdk.UsbCameraManager;
import com.sh.camera.FileActivity;
import com.sh.camera.R;
import com.sh.camera.SessionLinearLayout;
import com.sh.camera.SetActivity;
import com.sh.camera.DiskManager.DiskManager;
import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.codec.MediaCodecManager;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;
import com.sh.camera.version.VersionBiz;

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class MainService extends Service {

	private static final String TAG = "CMD";
	public static Context c;
	private static MainService instance;
	public static Context application;
	
	LayoutInflater inflater;
	public static boolean isrun = false;
	/**主界面是否在最前端显示状态*/
	public static boolean isWindowViewShow = true;
	public static String ACTION = "com.dss.car.dvr";
	//控制悬浮窗全屏
	public static String FULLSCREEN = "fullscreen";
	//控制悬浮窗全屏且跳过一次窗口化指令
	public static String PASSWINFULL = "passwinfullscreen";
	//控制悬浮窗窗口化
	public static String WINDOW = "window";
	//控制悬浮窗最小化
	public static String MINIMIZE = "minimize";
	//控制预览界面重启
	public static String RESTART = "restart";
	//通知开始录像
	public static String STARTRECORDER = "startrecorder";
	//通知开始上传
	public static String STARTPUSH = "startpush";
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~初始化主要功能控件~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//主要显示控件
	public TextureView[] ttvs;
	private SurfaceTexture[] stHolder;
	//按钮容器
	private LinearLayout ly_bts;
	//摄像头数组
	public static Camera[] camera;
	private static boolean avaliable[]= {false, false, false, false};
	static PreviewCallback[] preview;
	private MediaRecorder[] mrs;
	private String[] MrTempName;
	private ContentValues[] mCurrentVideoValues;
	public static SurfaceTextureListener[] stListener;
	//摄像头id
	public static int[] cid = null;
	//受控摄像头
	public static int[] rules;
	//记录当前录制视屏的起点，未录制时-1；
	long recoTime = -1;
	//控件id数组
	private int[] ttvids = {R.id.textureview1, R.id.textureview2, R.id.textureview3, R.id.textureview4};
	private ImageView btiv1,btiv2;
	private LinearLayout[] lys;
	private int[] lyids = {R.id.ly_1_0, R.id.ly_1_1, R.id.ly_1_2, R.id.ly_2_0, R.id.ly_2_1, R.id.ly_2_2};
	private boolean isTwoCamera = true;
	//句柄
	public static int[] StreamIndex;
	public static boolean clickLock = false;
	public static boolean[] sc_controls = {false, false, false, false};
	int framerate = Constants.FRAMERATE;
	int bitrate;
	public static DiskManager disk;	
	public static Pusher mPusher;
	
	//判断是退出还是打开其他界面
	boolean isClose = true;	
	//通知结束录像
	public static String STOPRECORDER = "stoprecorder";
	//通知结束上传
	public static String STOPPUSH = "stoppush";
	BroadcastReceiver 	SYSBr;	
	boolean usbcameraConnect = true;
	boolean sd_inject = false;	
	private String longitude = ""; // 经度
	private String latitude = ""; // 维度
	private LocationManager lm;

	// 获取本地application的对象
	public static MainService getInstance() {
		if (instance == null) {
			instance = new MainService();
		}
		return instance;
	}	

	public static DiskManager getDiskManager()
	{
		return disk;
	}
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
	        Manifest.permission.READ_EXTERNAL_STORAGE,
	Manifest.permission.WRITE_EXTERNAL_STORAGE };
	//这个处理目前的V4库存在问题
	public static void verifyStoragePermissions(Activity activity) {  
		   
	    int permission = ActivityCompat.checkSelfPermission(activity,  
	            Manifest.permission.WRITE_EXTERNAL_STORAGE); 
	    
	    Log.d("CMD", "REQUEST_EXTERNAL_STORAGE1");

	    if (permission != PackageManager.PERMISSION_GRANTED) { 
	    	Log.d("CMD", "REQUEST_EXTERNAL_STORAGE");
	    	
	    	ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,  
	                REQUEST_EXTERNAL_STORAGE);  
	   }  
	}  
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		c = MainService.this;
		application = getApplicationContext();
		disk = new DiskManager(this);			
		mPusher = new Pusher();
		StreamIndex = new int[Constants.MAX_NUM_OF_CAMERAS];
		camera = new Camera[Constants.MAX_NUM_OF_CAMERAS];
		mrs = new MediaRecorder[Constants.MAX_NUM_OF_CAMERAS];
		MrTempName = new String[Constants.MAX_NUM_OF_CAMERAS];
		mCurrentVideoValues = new ContentValues[Constants.MAX_NUM_OF_CAMERAS];
		framerate = ServerManager.getInstance().getFramerate();
		CreateView();		
		//一开始就初始化编码器，太占用资源		
		isrun = true;			
		Constants.setParam(c);
		cid = Constants.CAMERA_ID;
		inflater = LayoutInflater.from(c);
		registerReceiver(br, filter);
		registerReceiver(br2, filter2);
		disk.CreateDirctionaryOnDisk(Constants.CAMERA_FILE_DIR);
		if(Constants.GPS_SUPPORT == true)			
		{
			getLocation();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				while (isrun) {
					try {
						if(recoTime>0&&new Date().getTime()-recoTime>1000*60*Constants.VIDEO_TIME){
							handler.sendMessage(handler.obtainMessage(1002));
							Thread.sleep(2000);
						}else if(recoTime>0&&disk.GetDiskFreeTotal()<=Constants.SD_FREEJX){
							//SdCardBiz.getInstance().getDetectionServiceSdCar(Constants.isCleaning,instance);
							disk.getDetectionServiceSdCar(instance);							
						}
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					count++;
					if(count == 8)
					{
						if(Constants.StartFlag == true)
						{
							Constants.StartFlag = false;
							Intent intent = new Intent(MainService.ACTION);
							intent.putExtra("type", MainService.MINIMIZE);
							sendBroadcast(intent);
						}
					}						
				}
			}
		}).start();
		
	}	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		isrun = true;
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		SYSBr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) { 

				String action = intent.getAction();
				UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);        		
				if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action) && device.getDeviceProtocol() ==1) {          
					Toast.makeText(context, "监听到usb摄像头变动1"+device.getDeviceProtocol(), Toast.LENGTH_LONG).show();
					usbcameraConnect = false;    
					closeCamera(0);        		 

				} else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) && device.getDeviceProtocol()==1) {

					Toast.makeText(context, "监听到usb摄像头变动0"+device.getDeviceProtocol(), Toast.LENGTH_LONG).show();
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}
					usbcameraConnect = true;
					openCamera(0, 2);      		

				}  
				else if(action.equals(Constants.ACTION_VIDEO_PLAYBACK))
				{
					int id = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
					int type = intent.getIntExtra("EXTRA_TYPE", 0);  //类型  0 图片 1 录像
					String stime = intent.getStringExtra("EXTRA_STIME");  //回放开始时间
					String etime = intent.getStringExtra("EXTRA_ETIME");  //回放结束时间
					//AppLog.d("DSLauncherReceiver", ACTION_VIDEO_PLAYBACK);*/
					//CameraFileUtil.screenVideoFile(stime, etime, id);
					
					
				}else if(action.equals(Constants.ACTION_VIDEO_FILE_PLAYBACK))
				{		 			
					int cameraid = intent.getIntExtra("Channel", 1);  //通道ID
					String filename = intent.getStringExtra("Name");
					int splaysec = intent.getIntExtra("Start", 0); 
					int eplaysec = intent.getIntExtra("End", 0);
					CameraUtil.startVideoFileStream(cameraid, splaysec, eplaysec, filename,null);					
				}
				else if(action.equals(Constants.ACTION_VIDEO_FILE_PLAYBACK))
				{
					CameraUtil.stopVideoFileStream();
				}else if(action.equals(Constants.ACTION_UPDATE_LOCATION))
				{
					MainService.getInstance().updateLocation();
				}
				if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
				{
					//Toast.makeText(context, "监听到home key", Toast.LENGTH_LONG).show();
					MainService.getInstance().setWindowMin();
				}
			}
		};	      
		IntentFilter localIntentFilter = new IntentFilter();  
		localIntentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);	
		localIntentFilter.addAction(Constants.ACTION_VIDEO_PLAYBACK);        
		localIntentFilter.addAction(Constants.ACTION_VIDEO_FILE_PLAYBACK);   
		localIntentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		localIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); 
		localIntentFilter.addAction(Constants.ACTION_UPDATE_LOCATION); 
		
		registerReceiver(SYSBr, localIntentFilter);	        		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isrun = false;
		unregisterReceiver(br);
		unregisterReceiver(br2);
		unregisterReceiver(SYSBr);
		//取消监听
		Log.d("main service", "onDestroy");
	};

	IntentFilter filter2 = new IntentFilter("android.intent.action.ACC_OVER");
	BroadcastReceiver br2 = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			restart();
		}
	};
	//pass一次window
	boolean passwin = false;
	IntentFilter filter = new IntentFilter(ACTION);
	BroadcastReceiver br = new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String type = intent.getStringExtra("type");
			
			if(type.equals(WINDOW)){
				//				if(passwin){
				//					passwin = false;
				//				}else{
				//					setWindowWin();
				//				}
				ProviderBiz providerBiz = ProviderBiz.getInstance(c);
				int mainStatus = providerBiz.getDeviceInfo().getMainStatus();
				if(mainStatus == 1){
					setWindowWin();
				}
			}
			if(type.equals(MINIMIZE)){
				setWindowMin();
			}
			if(type.equals(FULLSCREEN)){
				setWindowFull();
			}
			if(type.equals(PASSWINFULL)){
				passwin = true;
				setWindowFull();
			}
			if(type.equals(RESTART)){
				passwin = true;
				restart();
			}
			if(type.equals(STARTRECORDER)){
				
				int index = intent.getIntExtra("index", 0);
				/*if(!isRecording){
					click(R.id.bt_ly_2);
				}*/
				prepareRecorder(index, 1);
				
			}
			if(type.equals(STOPRECORDER)){
				if(isRecording){
					click(R.id.bt_ly_2);
				}
			}
			if(type.equals(STARTPUSH)){
				if(!isSC){
					click(R.id.bt_ly_3);
				}
			}
			if(type.equals(STOPPUSH)){
				if(isSC){
					click(R.id.bt_ly_3);
				}
			}
		}

	};

	private void restart() {
		isrun = true;
		Constants.setParam(MainService.getInstance());
		StopCameraprocess();
		removeView();
		addView();
	}
	//最小化
	void setWindowMin(){
		ismatch = true;
		ly_bts.setVisibility(view.VISIBLE);
		if(Constants.PRODUCT_TYPE == 3){
			wmParams.x = 80;
			wmParams.y = 132;
		}else{
			wmParams.x = 1;
			wmParams.y = 1;
		}
		wmParams.width = 1;
		wmParams.height = 1;
		//最小化到后台，需要设置LayoutParams.FLAG_NOT_FOCUSABLE，才能取消对返回键的拦截，并且移除layout
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | 
				LayoutParams.FLAG_NOT_FOCUSABLE | 
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
		if(layoutPoint != null && layoutPoint.isShown()){
			mWindowManager.removeView(layoutPoint);
		}

		mWindowManager.updateViewLayout(view, wmParams);
		for (int i = 0; i < lys.length; i++) {
			if(i!=0&&i!=3){
				lys[i].setOnClickListener(click2start);
			}
		}
		isWindowViewShow = false;
	}	
	
	//最大化
	void setWindowFull(){
		ismatch = true;
		ly_bts.setVisibility(view.VISIBLE);
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width =  WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		
		//最大化，不要设置LayoutParams.FLAG_NOT_FOCUSABLE，才能拦截返回键
	
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | 
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
		mWindowManager.updateViewLayout(view, wmParams);
		
		//最大化，添加layout，才能拦截返回键，长宽为1，才不会挡住界面
		wmParams.width = 1;
		wmParams.height = 1;

		if(layoutPoint != null && layoutPoint.isShown()){
			mWindowManager.updateViewLayout(layoutPoint, wmParams);
		}else{
			mWindowManager.addView(layoutPoint, wmParams);
		}

		for (int i = 0; i < lys.length; i++) {
			if(i!=0&&i!=3){
				lys[i].setOnClickListener(click_ly);
			}
		}		
		//一甲丙益USB摄像头需要单独进行处理，预览反向问题
		if(Constants.PRODUCT_TYPE == 2){
			int index = 1;
			Matrix transform = new Matrix();
			ttvs[index] = (TextureView) view.findViewById(ttvids[index]);
			int width = ttvs[index].getWidth();
			if(width>200){
				width = 411;
			}else {
				width = 206;
			}
			transform.setScale(-1, 1,width, 0);
			ttvs[index].setTransform(transform);
		}
		isWindowViewShow = true;
		if(Constants.checkVersion){
			Constants.checkVersion = false;
			VersionBiz.doCheckVersionFirst(c, handler);
		}
	}

	//窗口化
	void setWindowWin(){
		ismatch = false;
		ly_bts.setVisibility(view.GONE);
		if(Constants.PRODUCT_TYPE == 3){
			wmParams.x = 80;
			wmParams.y = 132;
			wmParams.width = 216;
			wmParams.height = 155;
		}else{
			wmParams.x = 1;
			wmParams.y = 1;
			wmParams.width = 1;
			wmParams.height = 1;
		}
		mWindowManager.updateViewLayout(view, wmParams);
		for (int i = 0; i < lys.length; i++) {
			if(i!=0&&i!=3){
				lys[i].setOnClickListener(click2start);
			}
		}

		//一甲丙益USB摄像头需要单独进行处理，预览反向问题
		if(Constants.PRODUCT_TYPE == 2){
			int index = 1;
			Matrix transform = new Matrix();
			ttvs[index] = (TextureView) view.findViewById(ttvids[index]);
			int width = 0;
			if(ttvs[index].getWidth() > 500){
				width = 216/2;
				//处理摄像头窗口最大化后，进入另外一个应用在回到主界面串口显示一半的问题
			}else if(ttvs[index].getWidth() == 2){
				width = 216/2;
			}else{
				width = 216/4;
			}
			transform.setScale(-1, 1, width, 0);
			ttvs[index].setTransform(transform);
		}

	}

	boolean ismatch = false;
	OnClickListener click2start = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(ismatch){
				setWindowWin();
			}else{
				///setWindowFull();
			}
		}
	};


	LayoutParams wmParams;
	WindowManager mWindowManager;
	View view;
	// 一个点，叠加在Window中，用来监听返回键，最小化后移除，最大化时叠加到window中。
	SessionLinearLayout layoutPoint;
	// 触屏监听  
	float lastX, lastY;  
	int oldOffsetX, oldOffsetY;  
	private void CreateView() {
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		addView();
		/*ProviderBiz providerBiz = ProviderBiz.getInstance(c);
		int mainStatus = providerBiz.getDeviceInfo().getMainStatus();
		if(mainStatus == 1){
			setWindowWin();
		}*/				
		
		
	}
	private void addView() {
		if(inflater==null){
			inflater = LayoutInflater.from(c);
		}
		view = inflater.inflate(R.layout.activity_main, null);
		layoutPoint = (SessionLinearLayout) inflater.inflate(R.layout.layout_point, null);
		layoutPoint.setDispatchKeyEventListener(mDispatchKeyEventListener);
		initView();
		mWindowManager.addView(view, wmParams);
		view.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//		view.setOnClickListener(click2start);
		setWindowFull();
	}

	public void StopCameraprocess()
	{
		if(isRecording){
			btiv1.setImageResource(R.drawable.a02);
			for (int i = 0; i < rules.length; i++) {
				stoprecorder(rules[i],i);
			}
			isRecording = false;
		}

		if(isSC){
			stopSC();
		}
		for (int i = 0; i < rules.length; i++) {
			sc_controls[rules[i]] = false;
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				stopMrs(rules[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				closeCamera(rules[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
	}
	
	public void removeView() {
		try {
			mWindowManager.removeView(view);
			view = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 返回鍵监听
	 */
	private SessionLinearLayout.DispatchKeyEventListener mDispatchKeyEventListener = new SessionLinearLayout.DispatchKeyEventListener() {

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isWindowViewShow) {
				setWindowMin();
				return true;
			}
			return false;
		}
	};

	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1001){
				boolean lock = false;
				Toast.makeText(c, "执行拍照成功", 1000).show();
				/*for (int i = 0; i < rules.length; i++) {
					if(rules[i]==picid&&rules.length>i+1){
						//boolean re = CameraUtil.cameraTakePicture(i+1, 1);
						boolean re = MediaCodecManager.TakePicture(i+1, 1);
						if(re){
							lock = true;
							break;
						}
					}
				}*/
				if(!lock){
					clickLock = false;
				}
			}
			if(msg.what==1003){
				boolean lock = false;
				Toast.makeText(c, "执行拍照失败", 1000).show();
				/*for (int i = 0; i < rules.length; i++) {
					if(rules[i]==picid&&rules.length>i+1){
						//boolean re = CameraUtil.cameraTakePicture(i+1, 1);
						boolean re = MediaCodecManager.TakePicture(i+1, 1);
						if(re){
							lock = true;
							break;
						}
					}
				}*/
				if(!lock){
					clickLock = false;
				}
			}
			//录制达到规定时长，重录
			if(msg.what==1002){
				clickLock = true;
				try {
					for (int i = 0; i < rules.length; i++) {
						stoprecorder(rules[i],i);
					}
					//加入SD卡空间处理逻辑
					//				SdCardBiz.getInstance().getDetection(Constants.isCleaning);
					//SdCardBiz.getInstance().getDetectionServiceSdCar(Constants.isCleaning,instance);
					for (int i = 0; i < rules.length; i++) {
						if(camera[rules[i]]!=null) startRecorder(rules[i]);
					}
					disk.getDetectionServiceSdCar(instance);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				recoTime = new Date().getTime();
				clickLock = false;
			}
			else if(msg.what==1022){
				postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						isClose = false;
						setWindowMin();
						Intent intent_set = new Intent(c, SetActivity.class);
						intent_set.putExtra("fromUpdateVersion", true);
						intent_set.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent_set);
					}
				}, 8000);
			}
		};
	};

	private void initView() {
		lys = new LinearLayout[6];
		for (int i = 0; i < lys.length; i++) {
			lys[i] = (LinearLayout) view.findViewById(lyids[i]);
			if(i!=0&&i!=3){
				//				lys[i].setOnClickListener(click_ly);
				lys[i].setOnClickListener(click2start);
			}
		}		
		//确认四路、二路
		if(ServerManager.getInstance().getMode() == SetActivity.rgids[0]){
			lys[3].setVisibility(View.GONE);
			isTwoCamera = true;
		}else{
			lys[3].setVisibility(View.VISIBLE);
			isTwoCamera = false;
		}
		String rulestr = ServerManager.getInstance().getRule();
		rules = new int[rulestr.length()];
		for (int i = 0; i < rulestr.length(); i++) {
			rules[i] = Integer.parseInt(rulestr.substring(i, i+1));
		}
		for(int i =0; i<Constants.MAX_NUM_OF_CAMERAS; i++){	
			if(isTwoCamera&&i>1) break;
		}		
		ttvs = new TextureView[Constants.MAX_NUM_OF_CAMERAS];
		stHolder = new SurfaceTexture[Constants.MAX_NUM_OF_CAMERAS];		
		preview = new PreviewCallback[Constants.MAX_NUM_OF_CAMERAS];	
		stListener = new SurfaceTextureListener[Constants.MAX_NUM_OF_CAMERAS];
		ly_bts = (LinearLayout) view.findViewById(R.id.main_right_btly);
		btiv1 = (ImageView) view.findViewById(R.id.imageView1);
		btiv2 = (ImageView) view.findViewById(R.id.imageView2);
		//预览回调
		preview[0] = new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera1) {
				// TODO Auto-generated method stub
				MediaCodecManager.getInstance().onPreviewFrameUpload(data,0,camera[0]);
			}
		};
		preview[1] = new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera1) {
				// TODO Auto-generated method stub
				MediaCodecManager.getInstance().onPreviewFrameUpload(data,1,camera[1]);
			}
		};
		preview[2] = new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera1) {
				// TODO Auto-generated method stub
				MediaCodecManager.getInstance().onPreviewFrameUpload(data,2,camera[2]);
			}
		};
		preview[3] = new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera1) {
				// TODO Auto-generated method stub
				MediaCodecManager.getInstance().onPreviewFrameUpload(data,3,camera[3]);
			}
		};
		//初始化摄像头、开始预览
		for (int i = 0; i < Constants.MAX_NUM_OF_CAMERAS; i++) {
			if(isTwoCamera&&i>1) break;
			initPreview(i);
		}
	}
	
	public void SetPreviewValid(int index)
	{
		avaliable[index]  = true;		
	}
	/**
	 * 初始化预览
	 * @param i
	 */
		
	

	public void initPreview(int i){

		final int index = i;
		ttvs[i] = (TextureView) view.findViewById(ttvids[i]);
		stListener[i] = new SurfaceTextureListener() {
			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
			}
			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1, int arg2) {
			}
			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
				colseCamera(index);
				return true;
			}
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,int arg2) {
				stHolder[index] = arg0;	
				openCamera(index, 1);
									
			}
		};
		ttvs[i].setSurfaceTextureListener(stListener[i]);	
	}
	/**
	 * 关闭释放摄像头
	 * @param i
	 */
	public void colseCamera(int index){
		try {
			boolean falg = true;
			if(Constants.PRODUCT_TYPE == 3){
				if(index == 1){
					falg = false;
				}
			}
			if(falg){
				if(camera[index]!=null){
					camera[index].stopPreview();
					camera[index].release();
					camera[index] = null;
				}
			}else{
				//有方USB摄像头需要单独进行处理
				UsbCameraManager.getInstance().stopCamera();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void stopRecoders_SD_ERR()
	{		
		if(isRecording){
			btiv1.setImageResource(R.drawable.a02);
			for (int i = 0; i < rules.length; i++) {
				stoprecorder(rules[i],i);
			}
			isRecording = false;
			sd_inject = true;
		}	
	}
	

	public void startRecoders_SD_ERR()
	{
		if(!isRecording && sd_inject){
			btiv1.setImageResource(R.drawable.a02);
			for (int i = 0; i < rules.length; i++) {
				prepareRecorder(i,1);
			}
			isRecording = true;
			sd_inject = false;
		}	
	}	

	/**
	 * 打开摄像头并预览
	 * @param i
	 * @param type 1 正常启动  2 重启
	 */
	//int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		
	public void openCamera(int index,int type){
		try {
			boolean falg = true;			
			if(falg){
				try {
					AppLog.w(TAG, "摄像头数量:"+Camera.getNumberOfCameras());
					//释放摄像头
					//colseCamera(index);					
					//测试用//
					//camera[index] = Camera.open(mCameraId);
					//Log.d(TAG, "mCameraId" + mCameraId);
					camera[index] = Camera.open(cid[index]);

				} catch (Exception e) {
					e.printStackTrace();
					camera[index] = null;
				}	
				avaliable[index] = false;
				if (camera[index] != null) {
					try {
						camera[index].setPreviewTexture(stHolder[index]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Camera.Parameters parameters = camera[index].getParameters();
					/*List<Camera.Size> previewSIzes = parameters.getSupportedVideoSizes();
					for (Camera.Size s : previewSIzes) {
						System.out.println("CAMERA-"+cid[index]+"   "+s.width+"*"+s.height);
					}	*/
					parameters.setPreviewSize(Constants.RECORD_VIDEO_WIDTH, Constants.RECORD_VIDEO_HEIGHT);
					
					//时间		
					//1 T3 2 一甲丙益后视镜  3 有方后视镜
					if(Constants.PRODUCT_TYPE == 1){
						//parameters.setPreviewSize(Constants.RECORD_VIDEO_WIDTH, Constants.RECORD_VIDEO_HEIGHT);
						//parameters.setPictureSize(Constants.RECORD_VIDEO_WIDTH, Constants.RECORD_VIDEO_HEIGHT);
						//parameters.setPreviewFpsRange(15,15);
						//parameters.setGpsLatitude(21.223);
						//parameters.setGpsLongitude(102.223);						
						camera[index].setErrorCallback(new CameraErrorCallback(index));
					}else if(Constants.PRODUCT_TYPE == 2){						
						parameters.setPreviewFrameRate(30);  
						parameters.set("fps-percent",80);						
						//一甲丙益USB摄像头传输反向的问题
						if(index == 1){
							parameters.set("mirror", "true");
							parameters.set("timewater", "true");
							Camera.Size previewSize = camera[index].getParameters().getPreviewSize();
							Matrix transform = new Matrix();
							transform.setScale(-1, 1, ttvs[index].getWidth()/2, 0); 
							ttvs[index].setTransform(transform);
						}
						camera[index].setErrorCallback(new CameraErrorCallback(index));
					}else{
						parameters.setPreviewFrameRate(30);                       
						parameters.set("fps-percent",80);
						camera[index].setErrorCallback(new CameraErrorCallback(index));
					}
					camera[index].setParameters(parameters);					
					camera[index].startPreview();
					//checkCameraValid(index);					
					//成功后开始录像
					//
					//处理没有数据出来的摄像头，对某些摄像头来说，能看到摄像头节点，但却没有摄像头数据造成录像和拍照失败。										
				}
			}else{
				//有方USB摄像头需要单独进行处理
				UsbCameraManager.getInstance().startCamera(MainService.getInstance(),cid[index]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			AppLog.d(TAG, ExceptionUtil.getInfo(e));
		}
	}
	//防止出现一个摄像头坏的情况下影响别的摄像头不能正常工作
	//2017-06-29
	public boolean  checkCameraValid(final int index)
	{
		if(index > Constants.MAX_NUM_OF_CAMERAS)
			return false;		
		if(camera[index]== null)
		{
			return false;
		}
		camera[index].setPreviewCallback(preview[index]);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("CMD", " checkCameraValid "+avaliable[index]);	
				if(avaliable[index] == false)				
				{
					//closeCamera(index);	
					
				}else
				{
					camera[index].setPreviewCallback(null);
					Intent intent = new Intent(MainService.ACTION);
					intent.putExtra("type", MainService.STARTRECORDER);
					intent.putExtra("index", index);
					sendBroadcast(intent);
					
				}				
			}
		}).start();
		return true;		
	}
	
	
	 public static void TakePictureAll(int type)
	 {
		 	
		 final int pictype = type;
		 Log.d("CMD", String.format(" TakePictureAll:\n%d", type));		 
		 try {		
				if(type == 1){
					//if(!SdCardUtil.checkSdCardUtil()){
					if(MainService.getDiskManager().getDiskCnt()<=0){
						AppLog.d("CMD", "SD卡不存在");
						return ;
					}
				}else
				{
					File f = new File(Constants.SNAP_FILE_PATH);
					if(!f.exists()){
						f.mkdirs();
					}
				}
			
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return ;
			}
		 	MediaCodecManager.CAMERA_OPER_MODE = type;
		 
		 new Thread(new Runnable() {
				@Override
				public void run() {
					boolean flag1 = false;
					for (int i = 0; i < rules.length; i++) {					
						if((camera[i]!= null) ){							
							picid = i;
							MediaCodecManager.Startpick(pictype);	
							camera[i].setPreviewCallback(preview[i]);							
							flag1 = true;							
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} //等待处理完成//						
						}
			
					}					
					if( pictype == 1 )
				 	{
						if(flag1 ==  true )
						{
							Handler handler = MainService.getInstance().handler; 
							if(handler != null){
								handler.sendMessage(handler.obtainMessage(1001));
							}	
						}else
						{
							Handler handler = MainService.getInstance().handler; 
							if(handler != null){
								handler.sendMessage(handler.obtainMessage(1003));
							}	
						}						
				 	}						
				}
			}).start(); 		 	
		 
	 }
	
	//控制某路缩放
	boolean isgone = false;
	OnClickListener click_ly = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean flag = false;
			if(isgone){
				isgone = false;
				setAllView();
				if(v.getId() == R.id.ly_1_1){
					flag = true;
				}
			}else{
				isgone = true;
				switch (v.getId()) {
				case R.id.ly_1_1:
					lys[2].setVisibility(View.GONE);
					if(!isTwoCamera){
						lys[3].setVisibility(View.GONE);
					}
					break;
				case R.id.ly_1_2:
					if(!isTwoCamera){
						lys[3].setVisibility(View.GONE);
					}
					lys[1].setVisibility(View.GONE);
					break;
				case R.id.ly_2_1:
					lys[0].setVisibility(View.GONE);
					lys[5].setVisibility(View.GONE);
					break;
				case R.id.ly_2_2:
					lys[0].setVisibility(View.GONE);
					lys[4].setVisibility(View.GONE);
					break;
				}
			}

			if(Constants.PRODUCT_TYPE == 2){
				int index = 1;
				Matrix transform = new Matrix();
				ttvs[index] = (TextureView) view.findViewById(ttvids[index]);
				int width  = 0;
				if(isgone){
					width = ttvs[index].getWidth();
				}else{
					if(flag){
						width = ttvs[index].getWidth()/2;
					}else{
						width = ttvs[index].getWidth()/4;
					}
				}
				transform.setScale(-1, 1,width, 0);
				ttvs[index].setTransform(transform);
			}
		}
	};
	//控制某路缩放
	void setAllView(){
		if(isTwoCamera){
			lys[1].setVisibility(View.VISIBLE);
			lys[2].setVisibility(View.VISIBLE);
		}else{
			for (int i = 0; i < lys.length; i++) {
				lys[i].setVisibility(View.VISIBLE);
			}
		}
	}
	//释放摄像头资源
	public void closeCamera(int index){
		if(camera[index]!=null){
			camera[index].setPreviewCallback(null);
			camera[index].stopPreview();
			camera[index].release();
			camera[index] = null;
		}
	}
	//释放录像资源
	public void stopMrs(int index){
		if (mrs[index]!=null) { 
			mrs[index].stop(); 
			mrs[index].release(); 
			mrs[index] = null; 
		}
	}

	//右边六个按键的点击事件
	public static int picid = -1;
	boolean isRecording = false;
	boolean isSC = false;
	public void click(View v){
		click(v.getId());
	}
	
	
	public void click(int id){
		if(clickLock) return;
		switch (id) {
		case R.id.bt_ly_1://拍照			
			//检查SD卡是否存在
			//if(!SdCardUtil.checkSdCardUtil()){			
			if(disk.getDiskCnt()<=0){
				Toast.makeText(c, "未检测到SD卡,将无法执行操作", 1000).show();
			}else{
				clickLock = true;
				//CameraUtil.cameraTakePicture(0, 1);
				TakePictureAll(1);				
			}			
			break;
		case R.id.bt_ly_2://录像
			//检查SD卡是否存在
			//if(!SdCardUtil.checkSdCardUtil()){
			
			if(disk.getDiskCnt()<=0){
				Toast.makeText(c, "未检测到SD卡,将无法执行操作", 1000).show();
			}else{
				clickLock = true;
				//先判断是否录制中
				if(isRecording){
					btiv1.setImageResource(R.drawable.a02);
					//遍历受控数组,停止录像
					for (int i = 0; i < rules.length; i++) {
						stoprecorder(rules[i],i);
					}
					isRecording = false;
				}else{
					//判断是否正在上传
					/*if(isSC){
						//停止上传
						stopSC();
					}*/
					btiv1.setImageResource(R.drawable.b02);
					disk.getDetectionServiceSdCar(instance);	
					//遍历受控数组,开始录像
					for (int i = 0; i < rules.length; i++) {
						if(camera[rules[i]]!=null  ) startRecorder(rules[i]);
					}
					recoTime = new Date().getTime();
					isRecording = true;
				}
				clickLock = false;
			}
			break;
		case R.id.bt_ly_3://上传
			clickLock = true;
			if(isSC){
				stopSC();
			}else{				
				//处理上传
				btiv2.setImageResource(R.drawable.b03);
				for (int i = 0; i < rules.length; i++) {
					startVideoUpload2(ServerManager.getInstance().getIp(),ServerManager.getInstance().getPort(),ServerManager.getInstance().getStreamname(),i);
				}
				isSC = true;
			}
			clickLock = false;
			break;
		case R.id.bt_ly_4://回放
			isClose = false;
			setWindowMin();
			Intent intent_file = new Intent(c, FileActivity.class);
			intent_file.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent_file);
			break;
		case R.id.bt_ly_5://设置
			isClose = false;
			setWindowMin();
			Intent intent_set = new Intent(c, SetActivity.class);
			intent_set.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent_set);
			break;
		case R.id.bt_ly_6://退出
			
			setWindowMin();
			break;
		}
	}

	//结束上传
	private void stopSC() {
		btiv2.setImageResource(R.drawable.a03);
		for (int i = 0; i < rules.length; i++) {
			stopVideoUpload(i);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
		isSC = false;

	}
	

	public   void setCallback(int index, Camera camera)
	{
		camera.setPreviewCallback(preview[index]);	
	}	

	public void startVideoUpload2(String ipstr, String portstr, String serialno,  int index){

		int CameraId;
		CameraId = index+1;		
		if(camera[rules[index]]!=null && sc_controls[rules[index]]!=false){
			return;			
		}		
		try {
			CameraUtil.VIDEO_UPLOAD[index] = true;
			if(camera[rules[index]]!=null){
				//初始化推流工具
				StreamIndex[rules[index]]= mPusher.InitNetWork( getApplicationContext(),ipstr, portstr, String.format("%s?channel=%d.sdp", serialno,CameraId),framerate,0);
				//控制预览回调
				sc_controls[rules[index]] = true;
				camera[rules[index]].setPreviewCallback(preview[rules[index]]);	
				MediaCodecManager.getInstance().StartUpload(rules[index],camera[rules[index]]);									
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 结束视频上传
	 * @param i
	 */
	public void stopVideoUpload(int i){
		try {
			Log.d("SERVICE", " stop upload"+i);
			CameraUtil.VIDEO_UPLOAD[i] = false;
			if(camera[rules[i]]!=null){				
				sc_controls[rules[i]] = false;				
				MediaCodecManager.getInstance().StopUpload(rules[i]);
				camera[rules[i]].setPreviewCallback(null);
				mPusher.stopPush(StreamIndex[rules[i]]);	

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 准备录像
	 * @param index
	 */
	public void prepareRecorder(int index,int type){
		try {
			//if(!SdCardUtil.checkSdCardUtil()){
			if(disk.getDiskCnt()<=0){
				Log.d("CMD", " sd card not mount"+index);	
			}else{
				btiv1.setImageResource(R.drawable.b02);
				if(type == 1){
					recoTime = new Date().getTime();
				}
				isRecording = true;				
				startRecorder(rules[index]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * 开始录像
	 * @param index
	 */
	private  String convertOutputFormatToFileExt(int outputFileFormat) {
        if (outputFileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            return ".mp4";
        }
        return ".3gp";
    }
	
	public static String convertOutputFormatToMimeType(int outputFileFormat) {
        if (outputFileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            return "video/mp4";
        }
        return "video/3gpp";
    }
	
	public static void addVideo(final String path,final ContentValues values)
	{
		
		 AsyncTask.execute(new Runnable() {
             @Override
             public void run() {
            	 try
     			{	
     							
     				String finalName  = values.getAsString(Video.Media.DATA);
     				new File(path).renameTo(new File(finalName));
     				
     			}
     			catch(Exception e)
     			{
     				
     			}
             }
         });
		
		
	}
	


	
	private void generateVideoFilename(int index,  int outputFileFormat) {  	 
        
        String title = String.format("%d-%d", index+1, new Date().getTime()) ;
        String filename = title + convertOutputFormatToFileExt(outputFileFormat);
        String path = disk.getDiskDirectory(disk.SelectDisk())+Constants.CAMERA_FILE_DIR + filename;        
        String tmpPath = path + ".tmp";        
        String mime = convertOutputFormatToMimeType(outputFileFormat);  
        mCurrentVideoValues[index] = new ContentValues(4);
        mCurrentVideoValues[index].put(Video.Media.TITLE, title);
        mCurrentVideoValues[index].put(Video.Media.DISPLAY_NAME, filename);
        mCurrentVideoValues[index].put(Video.Media.MIME_TYPE, mime);
        mCurrentVideoValues[index].put(Video.Media.DATA, path);
        MrTempName[index] = tmpPath;
    }

	public void startRecorder(int index){
		try { 
			camera[index].unlock();
			mrs[index] = new MediaRecorder(); 
			mrs[index].reset();
			mrs[index].setCamera(camera[index]);
			mrs[index].setVideoSource(MediaRecorder.VideoSource.CAMERA);
			String starttime;
			String endtime;				
			/*//设置audio的编码格式
			mrs[index].setAudioSource(MediaRecorder.AudioSource.MIC);
			mrs[index].setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);*/
			//1 T3 2 一甲丙益后视镜  3 有方后视镜
			Log.d("CMD", " startRecorder "+index);
			if(Constants.PRODUCT_TYPE ==1){
				mrs[index].setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
				mrs[index].setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
				if(cid[index]>3){
					mrs[index].setVideoSize(720, 576); 
					mrs[index].setVideoEncodingBitRate(4*720*576);
				}else if(cid[index]>-1&&cid[index]<4){
					
					mrs[index].setVideoSize(Constants.RECORD_VIDEO_WIDTH, Constants.RECORD_VIDEO_HEIGHT); 
					mrs[index].setVideoEncodingBitRate(3*Constants.RECORD_VIDEO_WIDTH*Constants.RECORD_VIDEO_HEIGHT/2);
				}
				mrs[index].setVideoFrameRate(framerate); 
				mrs[index].setOnErrorListener(new MediaRecorderErrorListener(index));
				//camera[index].startWaterMark();		

			}else if(Constants.PRODUCT_TYPE ==2){
				mrs[index].setOutputFormat(10); 
				mrs[index].setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
				mrs[index].setVideoEncodingBitRate(6*640*480);
				if(cid[index]>3){
					mrs[index].setVideoSize(720, 576); 
				}else if(cid[index]>-1&&cid[index]<4){
					mrs[index].setVideoSize(640, 480); 
				}
				if(index == 1){
					mrs[index].setVideoFrameRate(20); 
				}else{
					mrs[index].setVideoFrameRate(30); 
				}
				mrs[index].setOnErrorListener(new MediaRecorderErrorListener(index));
			}else if(Constants.PRODUCT_TYPE ==3){
				mrs[index].setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);  
				mrs[index].setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
				mrs[index].setVideoEncodingBitRate(6*640*480);
				mrs[index].setVideoSize(640, 480); 
				mrs[index].setVideoFrameRate(30); 
				mrs[index].setOnErrorListener(new MediaRecorderErrorListener(index));
			}else{
				mrs[index].setOutputFormat(10); 
				mrs[index].setVideoEncoder(MediaRecorder.VideoEncoder.H264); 
				mrs[index].setVideoEncodingBitRate(6*640*480);
				if(cid[index]>3){
					mrs[index].setVideoSize(720, 576); 
				}else if(cid[index]>-1&&cid[index]<4){
					mrs[index].setVideoSize(640, 480); 
				}
				mrs[index].setVideoFrameRate(30); 
				mrs[index].setOnErrorListener(new MediaRecorderErrorListener(index));
			}					
			generateVideoFilename(index, MediaRecorder.OutputFormat.MPEG_4 );
			mrs[index].setOutputFile( MrTempName[index]);			
			Log.d("CMD", "generate filename"+MrTempName[index]);	
			mrs[index].prepare(); 
			mrs[index].start(); 
			Constants.CAMERA_RECORD[index] = true;
		} catch (Exception e) { 
			e.printStackTrace(); 
		}

	}
	//根据摄像头id停止录像
	void stoprecorder(int index,int i){
		try {
			if(camera[rules[i]]!=null){
				recoTime = -1;
				if(Constants.PRODUCT_TYPE ==1){
					//camera[index].stopWaterMark();
				}
				if (mrs[index] != null) { 
					try {
						mrs[index].setOnErrorListener(null);  
						mrs[index].setOnInfoListener(null);    
						mrs[index].setPreviewDisplay(null);  
						mrs[index].stop(); 
					} catch (Exception e) {
						e.printStackTrace();
					}
					Log.d("CMD", String.format(" stop record:"));
					mrs[index].release(); 
					mrs[index] = null; 
					camera[index].lock();
					addVideo(MrTempName[index], mCurrentVideoValues[index]);
					
				} 
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public class CameraErrorCallback implements android.hardware.Camera.ErrorCallback {
		private int mCameraId = -1;
		private Object switchLock = new Object();
		public CameraErrorCallback(int cameraId) {
			mCameraId = cameraId;
		}
		@Override
		public void onError(int error, android.hardware.Camera camera) {
			if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {        //底层camera实例挂掉了
				// We are not sure about the current state of the app (in preview or snapshot or recording). Closing the app is better than creating a new Camera object.                                 
				//如果是mipi挂掉了，usb断电，然后杀掉自己所在的进程，监听心跳广播启动自己
				//usb camera挂掉了，先断电然后再上电
				//Toast.makeText(c, "摄像头：error="+error+",mCameraId="+mCameraId, Toast.LENGTH_LONG).show();

			}
			Log.d("	error!!!", "code!!!!:"+error);	
		}
	}

	private class MediaRecorderErrorListener implements MediaRecorder.OnErrorListener {                 //底层mediaRecorder上报错误信息
		private int mCameraId = -1;
		public MediaRecorderErrorListener(int cameraId) {
			mCameraId = cameraId;
		}    
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {                              
			//先停止掉录制
			if(what == MediaRecorder.MEDIA_ERROR_SERVER_DIED){      //MediaRecorder.MEDIA_ERROR_SERVER_DIED--100，说明mediaService死了，需要释放MediaRecorder

				btiv1.setImageResource(R.drawable.a02);
				//遍历受控数组，停止录像
				for (int i = 0; i < rules.length; i++) {
					stoprecorder(rules[i],i);
					openCamera(i,1);
				}
				isRecording = false;
			}

		}
	}
	
	private void getLocation() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS是否正常启动
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {			
			return;
		}
		// 为获取地理位置信息时设置查询条件
		String bestProvider = lm.getBestProvider(getCriteria(), true);
		// 获取位置信息
		// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
		Location location = lm.getLastKnownLocation(bestProvider);
		updateView(location);
		/**
		 * 监听状态
		 */
		//lm.addGpsStatusListener(listener);
		/**
		 * 绑定监听，有4个参数 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种 参数2，位置信息更新周期，单位毫秒
		 * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息 参数4，监听
		 * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新。
		 * 1秒更新一次，或最小位移变化超过1米更新一次；
		 * 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep
		 * (10000);然后执行handler.sendMessage(),更新位置
		 */
		if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {			
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
					0, locationListener);
		} else if (lm.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
			
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					5000, 0, locationListener);
		}
	}

	// 位置监听
	private LocationListener locationListener = new LocationListener() {
		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
		
			updateView(location);
		}
		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "AVAILABLE");				
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "OUT_OF_SERVICE");				
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "UNAVAILABLE");				
				break;
			}
		}
		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			Location location = lm.getLastKnownLocation(provider);
			updateView(location);
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "FRIST");				
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "START");				
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "OVER");				
				break;
			}
		}

		
	};
	/**
	 * 实时更新文本内容
	 * 
	 * @param location
	 */
	void updateLocation()
	{
		String number="京55555";
		String str = "0,0,DDDD,"+"0,40,"+number+",0,80,"+latitude+"!N  "+longitude +"!E";
		Log.d("CMD", str);	
		for (int i = 0; i < rules.length; i++) {
			if(camera[rules[i]]!=null)
			{
				camera[rules[i]].setWaterMarkMultiple("0,0,DDDD,"+"0,40,"+number+",0,80,"+latitude+"!N  "+longitude +"!E,");
				//camera[rules[i]].setWaterMarkMultiple("0,0,DDDD,"+"0,40,"+"DDDD"+",");
			}
		}		
	}	
	private void updateView(Location location) {
		if (location != null) {
			
			double log;
			double lat;
			long logint;
			long latint;		
			
			log = location.getLongitude();
			lat = location.getLatitude();
			
			logint = (int)(log*10000);
			latint = (int)(lat*10000);
						
			longitude =  String.format("LOG:%d.%d", logint/10000,logint%10000);//String.valueOf(location.getLongitude());
			latitude =  String.format("LAT:%d.%d", latint/10000,latint%10000);//String.valueOf(location.getLatitude());
		
			Log.i(TAG, longitude);
			Log.i(TAG, latitude);		
			Intent intent = new Intent(Constants.ACTION_UPDATE_LOCATION);
			sendBroadcast(intent);		
			
		} else {
			Log.i(TAG, "UNKNOWN");
		}
		
	}

	/**
	 * 返回查询条件
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置是否要求速度
		criteria.setSpeedRequired(false);
		// 设置是否允许运营商收费
		criteria.setCostAllowed(false);
		// 设置是否需要方位信息
		criteria.setBearingRequired(false);
		// 设置是否需要海拔信息
		criteria.setAltitudeRequired(false);
		// 设置对电源的需求
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}
	
	
	
}
