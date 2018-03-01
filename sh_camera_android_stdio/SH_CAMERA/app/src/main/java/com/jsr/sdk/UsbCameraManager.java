/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.jsr.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.sh.camera.service.MainService;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.TextureView;
import android.widget.Toast;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：UsbCameraManager    
 * 类描述： USB摄像头控制类   
 * 创建人：Administrator    
 * 创建时间：2016年10月17日 下午3:29:41    
 * 修改人：Administrator    
 * 修改时间：2016年10月17日 下午3:29:41    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class UsbCameraManager {

	private static UsbCameraManager instance;
	private static final String tag = "UsbCameraManager.";
	private int mFrameWidth = 640;
	private int mFrameHeight = 480;

	private Bitmap mPreviewBitmap;
	private byte[] mYuvArray;
	private volatile boolean mStreamStop = false;
	private boolean mCameraExists = false;
	private UsbCamera mCamera;
	private TextureView mSurfaceView;
	private Rect mPreviewRect;
	private Thread mPreviewThread;
	private boolean mRecordVideoFlag = false;

	public static UsbCameraManager getInstance() {
		if (instance == null) {
			instance = new UsbCameraManager();
		}
		return instance;
	}

	/**
	 * 初始化USB摄像头
	 * @param mSurfaceView
	 */
	public void initCamera(TextureView mSurfaceView){
		this.mSurfaceView = mSurfaceView;
		mCamera = new UsbCamera();
		mPreviewBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
	}

	/**
	 * 启动USB摄像头
	 * @param context
	 * @param cameraid 摄像头ID
	 */
	public void startCamera(Context context,int cameraid){
		try {
			mCamera.setPictureSize(mFrameWidth, mFrameHeight);
			//dev/video3
			int state = mCamera.openCamera(cameraid);

			if(state != -1) {
				mCameraExists = true;
				mPreviewThread = new StartCamera();
				mPreviewThread.start();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 摄像头拍照
	 * @param cameraid 通道ID 0-3
	 * @param type 操作类型  1 内部操作  2 外部操作
	 */
	public void photoGraph(int cameraid,int type){

		Handler handler = MainService.getInstance().handler; 
		try {
			File picture = new File(Constants.CAMERA_FILE_PATH+(cameraid+1)+"-"+new Date().getTime()+".jpg"); 
			FileOutputStream out = new FileOutputStream(picture);
			mPreviewBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

			if(type == 1){
				if(handler != null){
					handler.sendMessage(handler.obtainMessage(1001));
				}
			}else{
				//拍照完成发送广播给Launcher
				Intent intent = new Intent("com.dss.camera.ACTION_TAKE_PICTURE_RESULT");
				intent.putExtra("EXTRA_RESULT", 0);  //结果 0 成功  1 失败 
				intent.putExtra("EXTRA_PATH", picture.getPath());
				MainService.getInstance().sendBroadcast(intent);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			if(type == 1){
				if(handler != null){
					handler.sendMessage(handler.obtainMessage(1003));
				}
			}else{
				//拍照完成发送广播给Launcher
				Intent intent = new Intent("com.dss.camera.ACTION_TAKE_PICTURE_RESULT");
				intent.putExtra("EXTRA_RESULT", 1);  //结果 0 成功  1 失败 
				intent.putExtra("EXTRA_PATH","");
				MainService.getInstance().sendBroadcast(intent);
			}

		}
	}

	/**
	 * 录制视频 
	 * @param cameraid 摄像头ID
	 */
	public void startRecordVideo(Context context,int cameraid){
		try {
			Toast.makeText(MainService.getInstance(), "开始视频录制", 1000).show();
			UsbEncoder.initEncoder();
			mRecordVideoFlag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 停止视频录制
	 */
	public void stopRecordVideo(){
		Toast.makeText(MainService.getInstance(), "停止视频录制", 1000).show();
		mRecordVideoFlag = false;
		UsbEncoder.close();
	}

	/**
	 * 停止摄像头
	 */
	public void stopCamera(){
		try {
			if(mCameraExists) {
				mStreamStop = true;
				mCameraExists = false;
				while (mStreamStop) {
					try{ 
						Thread.sleep(100);
					} catch (Exception e) { }
				}
			}
			mCamera.stopCamera();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private class StartCamera extends Thread {
		@Override
		public void run() {

			while (!mStreamStop) {
				try {
					if(-2 == mCamera.refreshFrame()) {
						mStreamStop = true;
						mCameraExists = false;
					}
					mPreviewRect = new Rect(0, 0, mSurfaceView.getWidth(), mSurfaceView.getHeight());
					mCamera.getBitmap(mPreviewBitmap);
					Canvas canvas = mSurfaceView.lockCanvas();
					if (canvas != null) {
						canvas.drawBitmap(mPreviewBitmap, null, mPreviewRect, null);
						mSurfaceView.unlockCanvasAndPost(canvas);
					}
					if(mRecordVideoFlag){
						mYuvArray = new byte[mFrameWidth*mFrameHeight*3/2];
						int state = mCamera.getRawFrame(mYuvArray);
						AppLog.i(tag, "state==="+state+",mYuvArray=="+mYuvArray.length);
						//MediaCodecManager.getInstance().onPreviewFrameUpload(mYuvArray, 1,null);
						UsbEncoder.saveVideoFile(mYuvArray);
						//AvcEncoder.saveVideoFile(mYuvArray);
					}
				} catch (Exception e) {
					// TODO: handle exception
					AppLog.i(tag, ExceptionUtil.getInfo(e));
					e.printStackTrace();

				}	
			}
		}
	}



}
