
package com.sh.camera.service;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.push.push.Pusher;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore.Video;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dss.car.launcher.provider.biz.ProviderBiz;
import com.sh.camera.BaseApp;
import com.sh.camera.FileActivity;
import com.sh.camera.R;
import com.sh.camera.SessionLinearLayout;
import com.sh.camera.SetActivity;
import com.sh.camera.DiskManager.DiskManager;
import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.TalkBackActivity;
import com.sh.camera.codec.MediaCodecManager;
import com.sh.camera.faceRecognition.activity.ChooseFunctionActivity;
import com.sh.camera.faceRecognition12.DetecterActivity;
import com.sh.camera.faceRecognition12.FaceRecognition12Activity;
import com.sh.camera.faceRecognition12.FaceRgUtils;
import com.sh.camera.faceRecognition12.RegisterActivity;
import com.sh.camera.listener.OnCallbackListener;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.CameraFileUtil;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;
import com.sh.camera.util.Tools;
import com.sh.camera.version.VersionBiz;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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
	public  long[] StreamIndex;
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

	// 获取本地application的对象
	private Button btn_app_minimize,btn_app_exit;
	private FrameLayout inc_alertaui,fl_dialog_normal;
	private FrameLayout   inc_url;
	private TextView text_url;

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
	private boolean isTabletDevice = true;

	public void onCreate() {
		super.onCreate();
		isTabletDevice = isTabletDevice(this);
		instance = this;
		c = MainService.this;
		application = getApplicationContext();
		disk = new DiskManager(this);
		mPusher = new Pusher();
		//最大相机数不包括前置相机，前置和后置不能同时打开，按照现在的逻辑，加进去会有问题
		Constants.MAX_NUM_OF_CAMERAS=ServerManager.getMaxNumCamera();
		StreamIndex = new long[Constants.MAX_NUM_OF_CAMERAS];
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

		disk.CreateDirctionaryOnDisk(Constants.CAMERA_FILE_DIR);
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

		FaceRgUtils.loadFaceData();
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
					openCamera(0, 2,null);
				}
				else if(action.equals(Constants.ACTION_VIDEO_PLAYBACK))
				{
					int id = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
					int type = intent.getIntExtra("EXTRA_TYPE", 0);  //类型  0 图片 1 录像
					String stime = intent.getStringExtra("EXTRA_STIME");  //回放开始时间
					String etime = intent.getStringExtra("EXTRA_ETIME");  //回放结束时间

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
		registerReceiver(SYSBr, localIntentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isrun = false;
		System.exit(0);
		//取消监听
		Log.d("main service", "onDestroy");
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
			if(type.equals("EXIT"))
			{
				StopCameraprocess();
				removeView();
				stopSelf();
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
		wmParams.x = 1;
		wmParams.y = 1;

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
		isWindowViewShow = true;
		if(Constants.checkVersion){
			Constants.checkVersion = false;
			VersionBiz.doCheckVersionFirst(c, handler);
		}
		reconnectCameras();
	}

	/**
	 * 重连相机
	 */
	public void reconnectCameras() {
		try {
			for (int j=0; j<camera.length;j++){
				if (camera[j]!=null){
					camera[j].startPreview();
				}
			}
		} catch (Exception e) {
			AppLog.e(e.toString());
		}
	}

	//窗口化
	void setWindowWin(){
		ismatch = false;
		ly_bts.setVisibility(view.GONE);
		wmParams.x = 1;
		wmParams.y = 1;
		wmParams.width = 1;
		wmParams.height = 1;

		mWindowManager.updateViewLayout(view, wmParams);
		for (int i = 0; i < lys.length; i++) {
			if(i!=0&&i!=3){
				lys[i].setOnClickListener(click2start);
			}
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
		if (Build.VERSION.SDK_INT>=26){
			wmParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
		}else {
			wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		}

		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		addView();

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
        closeAllCamer();

    }

    public void closeAllCamer() {
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
				inc_alertaui.setVisibility(View.VISIBLE);
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
		//通过最大相机数，设置相机预览界面显示多少个
		setPerviewShowByCamera(Constants.MAX_NUM_OF_CAMERAS);
		String rulestr = ServerManager.getInstance().getRule();
//		rulestr="0";
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
		if(isTabletDevice){
			ly_bts = (LinearLayout) view.findViewById(R.id.main_right_btly);
		}else{
			ly_bts = (LinearLayout) view.findViewById(R.id.main_bottom_btly);
		}
		ly_bts.setVisibility(View.VISIBLE);
		if(isTabletDevice){
			btiv1 = (ImageView) view.findViewById(R.id.imageView1);
			btiv2 = (ImageView) view.findViewById(R.id.imageView2);
		}else{
			btiv1 = (ImageView) view.findViewById(R.id.imageView1_bottom);
			btiv2 = (ImageView) view.findViewById(R.id.imageView2_bottom);
		}
		btn_app_minimize = (Button) view.findViewById(R.id.btn_app_minimize);
		btn_app_exit = (Button) view.findViewById(R.id.btn_app_exit);
		inc_alertaui = (FrameLayout) view.findViewById(R.id.inc_alertaui);
		fl_dialog_normal=view.findViewById(R.id.fl_dialog_normal);

//		//预览回调
//		preview[0] = new PreviewCallback() {
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera1) {
//				// TODO Auto-generated method stub
//				MediaCodecManager.getInstance().onPreviewFrameUpload(data,0,camera[0]);
//			}
//		};
//		preview[1] = new PreviewCallback() {
//
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera1) {
//				// TODO Auto-generated method stub
//				MediaCodecManager.getInstance().onPreviewFrameUpload(data,1,camera[1]);
//			}
//		};
//		preview[2] = new PreviewCallback() {
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera1) {
//				// TODO Auto-generated method stub
//				MediaCodecManager.getInstance().onPreviewFrameUpload(data,2,camera[2]);
//			}
//		};
//		preview[3] = new PreviewCallback() {
//
//			@Override
//			public void onPreviewFrame(byte[] data, Camera camera1) {
//				// TODO Auto-generated method stub
//				MediaCodecManager.getInstance().onPreviewFrameUpload(data,3,camera[3]);
//			}
//		};
        //初始化摄像头、开始预览
        for (int i = 0; i < rules.length; i++) {
            final int finalI = i;
            preview[i]=new PreviewCallback() {

                @Override
                public void onPreviewFrame(byte[] data, Camera camera1) {
                    // TODO Auto-generated method stub
                    MediaCodecManager.getInstance().onPreviewFrameUpload(data, finalI, camera[finalI]);
                }
            };
        }
		//初始化摄像头、开始预览
		for (int i = 0; i < Constants.MAX_NUM_OF_CAMERAS; i++) {
			if(isTwoCamera&&i>1) break;
			initPreview(i);
		}
	}

	/**
	 * 通过最大相机数，设置相机预览界面显示多少个
	 * @param maxNumOfCameras
	 */
	private void setPerviewShowByCamera(int maxNumOfCameras) {
		lys[1].setVisibility(maxNumOfCameras>=1?View.VISIBLE:View.GONE);
		lys[2].setVisibility(maxNumOfCameras>=2?View.VISIBLE:View.GONE);
		lys[4].setVisibility(maxNumOfCameras>=3?View.VISIBLE:View.GONE);
		lys[5].setVisibility(maxNumOfCameras>=4?View.VISIBLE:View.GONE);
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
				closeCamera(index);
				return true;
			}
			@SuppressLint("CheckResult")
            @Override
			public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,int arg2) {
				stHolder[index] = arg0;
				openCamera(index, 1, new OnCallbackListener<Boolean>() {
                    @Override
                    public void onCallback(Boolean aBoolean) {
                        if (!aBoolean){//打开相机失败
                            ((View)ttvs[index].getParent()).setVisibility(View.GONE);
                        }
                    }
                });
			}
		};
		ttvs[i].setSurfaceTextureListener(stListener[i]);
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

	public void openCamera(final int index, int type, @Nullable final OnCallbackListener<Boolean> listener){
        new ThreadUtils.SimpleTask<Camera>() {
            @Nullable
            @Override
            public Camera doInBackground() throws Throwable {
                Camera camera=null;
                try {
                    AppLog.w(TAG, "摄像头数量:"+Camera.getNumberOfCameras());
                    camera= Camera.open(cid[index]);
                } catch (Exception e) {
                    e.printStackTrace();
                    camera = null;
                }
                return camera;
            }

            @Override
            public void onSuccess(@Nullable Camera result) {
                try {
					camera[index]=result;
                    boolean falg = true;
                    if(falg){
                        avaliable[index] = false;
                        if (camera[index] != null) {
                            try {
                                camera[index].setPreviewTexture(stHolder[index]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Camera.Parameters parameters = camera[index].getParameters();
                            parameters.setPreviewSize(Constants.RECORD_VIDEO_WIDTH, Constants.RECORD_VIDEO_HEIGHT);
                            camera[index].setErrorCallback(new CameraErrorCallback(index));
                            camera[index].setParameters(parameters);
                            camera[index].startPreview();
                            if (result!=null&&listener != null) {
                                listener.onCallback(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    AppLog.d(TAG, ExceptionUtil.getInfo(e));
                }
            }
        }.run();
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
		 new Thread(new Runnable() {
				@Override
				public void run() {
					boolean flag1 = false;
					for (int i = 0; i < rules.length; i++) {					
						if((camera[i]!= null) ){							
							picid = i;
							MediaCodecManager.PrepareTakePicture();
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
						if(flag1 ==  false )
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
		case R.id.bt_ly_1_bottom://鎷嶇収
			clickLock = true;
			//CameraUtil.cameraTakePicture(0, 1);
			TakePictureAll(1);
			clickLock = false;
			break;
		case R.id.bt_ly_2://录像
		case R.id.bt_ly_2_bottom://褰曞儚

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

			break;
		case R.id.bt_ly_3://上传
		case R.id.bt_ly_3_bottom://涓婁紶
			clickLock = true;
			if(isSC){
				stopSC();
			}else{				
				//处理上传
				btiv2.setImageResource(R.drawable.b03);
				for (int i = 0; i < rules.length; i++) {
					startVideoUpload2(ServerManager.getInstance().getIp(),ServerManager.getInstance().getPort(),ServerManager.getInstance().getStreamname(),i,0, 0);
				}
				isSC = true;
			}
			clickLock = false;
			break;
		case R.id.bt_ly_4://回放
		case R.id.bt_ly_4_bottom://文件

			isClose = false;
			setWindowMin();
			Intent intent_file = new Intent(c, FileActivity.class);
			intent_file.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent_file);
			break;
		case R.id.bt_ly_5://设置
		case R.id.bt_ly_5_bottom://璁剧疆

			isClose = false;
			setWindowMin();
			closeAllCamer();
			Intent intent_set = new Intent(c, SetActivity.class);
			intent_set.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent_set);
			break;
		case R.id.bt_ly_6://退出
		case R.id.bt_ly_6_bottom://退出
			inc_alertaui.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_app_minimize://閫€鍑?
			//娣诲姞涓€夋嫨绐?
			try {
				inc_alertaui.setVisibility(View.GONE);
				setWindowMin();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_app_exit://閫€鍑?
			try {
				inc_alertaui.setVisibility(View.GONE);
				Intent intent = new Intent(ACTION);
				intent.putExtra("type", "EXIT");
				sendBroadcast(intent);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_dialog_cancel:
			inc_alertaui.setVisibility(View.GONE);
			break;
		case R.id.bt_ly_7_bottom:
			if (((BaseApp)getApplication())
						  .mFaceDB.mRegister.isEmpty()){
				showTipDialog("没有注册人脸，请先到设置里面注册！");
				return;
			}
			isClose = false;
			setWindowMin();
			closeAllCamer();
			Intent intent=new Intent(this,DetecterActivity.class);
			intent.putExtra("Camera", 1);//1是前置
			ActivityUtils.startActivity(intent);
			break;
		}
	}

	//结束上传
	private void stopSC() {
		btiv2.setImageResource(R.drawable.a03);
		for (int i = 0; i < rules.length; i++) {
			stopVideoUpload(i,0);
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
		isSC = false;

	}

	public void  DeCoderAAC(byte []data)	{

		MediaCodecManager.getInstance().DecodeAAC(data);
	}

	public   void setCallback(int index, Camera camera)
	{
		camera.setPreviewCallback(preview[index]);	
	}	

	long handle;
	public void startVideoUpload2(String ipstr, String portstr, String serialno,  int index, int type, int  talk){

		int CameraId;
		int  m_index_channel;
		CameraId = index+1;

		if(type == 0) {

			if (camera[rules[index]] != null && sc_controls[rules[index]] != false) {
				return;
			}
			try {

				if (camera[rules[index]] != null) {
					//初始化推流工具
					if (ServerManager.getInstance().getprotocol() == Constants.CAREYE_RTMP_PROTOCOL) {
						handle = mPusher.CarEyeInitNetWorkRTMP(getApplicationContext(), Constants.Key, ipstr, portstr, String.format("live/%s&channel=%d", serialno, CameraId), Constants.CAREYE_VCODE_H264, 20, Constants.CAREYE_ACODE_AAC, 1, 8000);
					} else {
							handle = mPusher.CarEyeInitNetWorkRTP(getApplicationContext(), Constants.rtpKey, ipstr, portstr, serialno, CameraId, Constants.CAREYE_VCODE_H264_1078, 20, Constants.CAREYE_ACODE_AAC_1078, 1, 8000, talk);
					}
					if (handle < 0 && ServerManager.getInstance().getprotocol() == Constants.CAREYE_RTP_PROTOCOL) {
						Log.d("CMD", " init error, error number" + handle);
						//Toast.makeText(MainService.getInstance(), "閾炬帴鏈嶅姟鍣ㄥけ璐ワ細"+m_index_channel, 1000).show();
						return;
					}
					if (handle == 0 && ServerManager.getInstance().getprotocol() == Constants.CAREYE_RTMP_PROTOCOL) {
						Log.d("CMD", " init error, error number" + handle);
						//Toast.makeText(MainService.getInstance(), "閾炬帴鏈嶅姟鍣ㄥけ璐ワ細"+m_index_channel, 1000).show();
						return;
					}
					CameraUtil.VIDEO_UPLOAD[index] = true;
					//控制预览回调
					sc_controls[rules[index]] = true;
					StreamIndex[rules[index]] = handle;
					MediaCodecManager.getInstance().StartUpload(rules[index], camera[rules[index]], handle, type);
					camera[rules[index]].setPreviewCallback(preview[rules[index]]);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}else
		{
			if (ServerManager.getInstance().getprotocol() == Constants.CAREYE_RTMP_PROTOCOL) {
				handle = mPusher.CarEyeInitNetWorkRTMP(getApplicationContext(), Constants.Key, ipstr, portstr, String.format("live/%s&channel=%d", serialno, CameraId), Constants.CAREYE_VCODE_H264, 20, Constants.CAREYE_ACODE_AAC, 1, 8000);
			} else {
					handle = mPusher.CarEyeInitNetWorkRTP(getApplicationContext(), Constants.rtpKey, ipstr, portstr, serialno, CameraId, Constants.CAREYE_VCODE_H264_1078, 20, Constants.CAREYE_ACODE_AAC_1078, 1, 8000, talk);
			}
			MediaCodecManager.getInstance().StartUpload(0, null, handle, type);
		}
	}
	/**
	 * 结束视频上传
	 * @param i
	 */
	public void stopVideoUpload(int i, int talk){
		try {
			Log.d("SERVICE", " stop upload"+i);
			if(talk==0) {
				CameraUtil.VIDEO_UPLOAD[i] = false;
				if (camera[rules[i]] != null) {
					sc_controls[rules[i]] = false;
					camera[rules[i]].setPreviewCallback(null);
					MediaCodecManager.getInstance().StopUpload(rules[i],talk);
					mPusher.stopPush(StreamIndex[rules[i]], ServerManager.getInstance().getprotocol());
					StreamIndex[rules[i]] = 0;
				}
			}else
			{
				MediaCodecManager.getInstance().StopUpload(0,talk);
				mPusher.stopPush(handle, ServerManager.getInstance().getprotocol());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/*
	* 启动语音对讲
	* */
	public void startTalkBack(){
		Intent dialogIntent = new Intent(getBaseContext(), TalkBackActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(dialogIntent);
	}


	/**
	 * 准备录像
	 * @param index
	 */
	public void prepareRecorder(int index,int type){
		try {
			//if(!SdCardUtil.checkSdCardUtil()){
				btiv1.setImageResource(R.drawable.b02);
				if(type == 1){
					recoTime = new Date().getTime();
				}
				isRecording = true;				
				startRecorder(rules[index]);

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

		File mFile;
		try {
			mFile = CameraFileUtil.CreateText(CameraFileUtil.getRootFilePath() + Constants.CAMERA_PATH);
			String title = String.format("%d-%d", index+1, new Date().getTime()) ;
			String filename = title + convertOutputFormatToFileExt(outputFileFormat);
			File file = new File(mFile,filename);
			String path = file.getPath();
			String tmpPath = path + ".tmp";
			String mime = convertOutputFormatToMimeType(outputFileFormat);
			mCurrentVideoValues[index] = new ContentValues(4);
			mCurrentVideoValues[index].put(Video.Media.TITLE, title);
			mCurrentVideoValues[index].put(Video.Media.DISPLAY_NAME, filename);
			mCurrentVideoValues[index].put(Video.Media.MIME_TYPE, mime);
			mCurrentVideoValues[index].put(Video.Media.DATA, path);
			MrTempName[index] = tmpPath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
					openCamera(i,1,null);
				}
				isRecording = false;
			}

		}
	}
	
	private boolean isTabletDevice(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
				Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	//展示提示对话框
	private void showTipDialog(String msg){
		TextView tvMsg= fl_dialog_normal.findViewById(R.id.message);
		fl_dialog_normal.setVisibility(View.VISIBLE);
		tvMsg.setText(msg);
		fl_dialog_normal.findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fl_dialog_normal.setVisibility(View.GONE);
			}
		});
	}

	
}
