/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.push.push.Pusher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.service.MainService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.coder.CommEncoder;

import static android.hardware.Camera.getCameraInfo;
import static android.hardware.Camera.getNumberOfCameras;

/**
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：CameraUtil    
 * 类描述：摄像头工具类    
 * 创建人：Administrator    
 * 创建时间：2016年10月12日 下午2:35:47    
 * 修改人：Administrator    
 * 修改时间：2016年10月12日 下午2:35:47    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CameraUtil {

	private static final String tag = "CameraUtil.";

	/**视频上传*/
	public static boolean VIDEO_UPLOAD[] = {false,false,false,false};

	/**视频回放,文件上传只能上传一路  true 正在回放  false 未回放*/
	public static boolean VIDEO_FILE_UPLOAD = false;

	/**摄像头操作方式 1 内部操作  2 外部操作*/
	public static int CAMERA_OPER_MODE = 1;




	/**
	 * 开始视频上传
	 * @param i 通道
	 */

	/*  type 0 : verdio   1: talk  */
	public static void startVideoUpload(int i, int type){

		//预览之前判断是否回放，如果回放先结束回放
		if(CameraUtil.VIDEO_FILE_UPLOAD){
			stopVideoFileStream();
		}

		if (VIDEO_UPLOAD.length<=i){
			ToastUtils.showShort("视频上传通道错误！");
		}

		//预览之前先停止上传
		if(VIDEO_UPLOAD[i]){
			stopVideoUpload(i);
		}
		//初始化推流工具
		VIDEO_UPLOAD[i] = true;
		MainService.getInstance().startVideoUpload2(ServerManager.getInstance().getIp(),ServerManager.getInstance().getPort(),ServerManager.getInstance().getStreamname(),i, type);
	}

	/**
	 * 结束视频上传
	 * @param i
	 */
	public static void stopVideoUpload(int i){
		VIDEO_UPLOAD[i] = false;
		MainService.getInstance().stopVideoUpload(i);
	}

	/**
	 * 回放所有文件
	 * @param context
	 * @param data
	 */
	public static void startVideoFileAllStream(final Context context,final ArrayList<HashMap<String, String>> data){
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sp = context.getSharedPreferences("fcoltest", context.MODE_PRIVATE);
				String ip = sp.getString("ip",Constants.SERVER_IP);
				String port = sp.getString("port",Constants.SERVER_PORT);
				String streamname = sp.getString("name",Constants.STREAM_NAME);
				int i = 0;
				for (HashMap<String, String> map : data) {
					i++;
					String filename = map.get("name");
					int cameraid = Integer.parseInt(filename.split("-")[0]);
					String streamName =  String.format("%s-%d.sdp", streamname,(cameraid-1));

					//开始上传
					if(MainService.mPusher == null){
						MainService.mPusher = new Pusher();
					}
					MainService.mPusher.startfilestream(ip, port,streamName, map.get("path"),ServerManager.getInstance().getprotocol());
					if(i == data.size()){
						/*Intent intent = new Intent("com.dss.camera.ACTION_END_VIDEO_PLAYBACK");
						intent.putExtra("EXTRA_ID", map.get("cameraid"));
						context.sendBroadcast(intent);*/
						byte[] data = CommEncoder.getEndVideoPlayBack(Integer.parseInt(map.get("cameraid")));
						CommCenterUsers.witeMsg(data,1);
					}
				}
			}
		}).start();
	}

	/**
	 * 指定文件回放
	 * @param cameraid 通道ID
	 * @param splaysec 开始秒
	 * @param eplaysec 结束秒
	 * @param filename 文件名称
	 */
	public static void startVideoFileStream(final int cameraid,final int splaysec,final int eplaysec,final String filename,final Handler handler){

		SharedPreferences sp = MainService.getInstance().getSharedPreferences("fcoltest", MainService.getInstance().MODE_PRIVATE);
		final String ip = sp.getString("ip",Constants.SERVER_IP);
		final String port = sp.getString("port",Constants.SERVER_PORT);
		final String Name = sp.getString("name",Constants.STREAM_NAME);
		final String streamName;
		if(Constants.CAREYE_RTMP_PROTOCOL==Constants.protocol)
		{
			streamName =  String.format("live/%s&channel=%d", Name,cameraid);
		}else
		{
			streamName =   Name;
		}
		if(MainService.mPusher == null){
			MainService.mPusher = new Pusher();
		}
		//涓婁紶涔嬪墠鍏堝仠姝㈠洖鏀?
		if(CameraUtil.VIDEO_FILE_UPLOAD){
			CameraUtil.stopVideoFileStream();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//涓婁紶涔嬪墠鍏堝仠姝㈤瑙?
		int i = cameraid-1;

		if(VIDEO_UPLOAD[i]){
			VIDEO_UPLOAD[i] = false;
			MainService.getInstance().stopVideoUpload(i);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CameraUtil.VIDEO_FILE_UPLOAD = true;
		//int channel = MainService.mPusher.startfilestream( CommConstants.playBackIp, ""+CommConstants.playBackPort, streamName, filename,splaysec,eplaysec,handler);
		MainService.mPusher.startfilestream( ip, port, streamName, cameraid, filename,splaysec,eplaysec,handler,Constants.protocol);

	}
	/**
	 * 结束视频回放上传
	 */
	public static void stopVideoFileStream(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					CameraUtil.VIDEO_FILE_UPLOAD = false;
					if(MainService.mPusher == null){
						MainService.mPusher = new Pusher();
					}
					for(int i = 0; i <4; i++)
					{
						if(Constants.protocol == Constants.CAREYE_RTMP_PROTOCOL)
						{
							MainService.mPusher. CarEyeStopNativeFileRTMP(i);
						}else
						{
							MainService.mPusher. CarEyeStopNativeFileRTP(i);
						}
					}					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();		
	}

	/**
	 * 拍照
	 * @param cameraid 通道ID 0-3
	 * @param type 操作类型  1 内部操作  2 外部操作
	 */
	public static boolean cameraTakePicture(int cameraid,int type){

		try {
			//检查SD卡是否存在

			Log.d("CMD", " cameraTakePicture"+cameraid);
			CAMERA_OPER_MODE = type;
			if(CAMERA_OPER_MODE == 1){
				if(!SdCardUtil.checkSdCardUtil()){
					AppLog.d(tag, "SD卡不存在");
					return false;
				}
			}else
			{
				File f = new File(Constants.SNAP_FILE_PATH);
				if(!f.exists()){
					f.mkdirs();
				}
			}
			int index = cameraid;
			//Camera camera = MainService.camera[MainService.rules[index]];
			Camera camera = MainService.camera[index];
			if(MainService.rules.length>0&&camera!=null){
				//MainService.picid = MainService.rules[index];
				MainService.picid = cameraid;
				camera.takePicture(null, null, pictureCallback);
				return true;
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

		return false;
	}
	/**
	 * 录像
	 * @param cameraid 通道ID 1-4 
	 * @param time 录像时长
	 */
	public static void videoRecord(int cameraid,long time){

	}
	/**
	 * 摄像头检测
	 */
	public static void checkCamera(){
		if(!SdCardUtil.checkSdCardUtil()){
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.RESTART);
			MainService.getInstance().sendBroadcast(intent);
		}
	}

	/**
	 * 拍照回调
	 */
	public static Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {  
		@Override  
		public void onPictureTaken(byte[] data, Camera camera) {  
			new SavePictureTask().execute(data);  
			camera.startPreview();  
		}  
	};  

	public static class SavePictureTask extends AsyncTask<byte[], String, String> {  
		@Override  
		protected String doInBackground(byte[]... params) { 

			int index = MainService.picid;
			Handler handler = MainService.getInstance().handler; 
			File picture ;

			if(CAMERA_OPER_MODE == 1){
				picture = new File(Constants.CAMERA_FILE_PATH+(index+1)+"-"+new Date().getTime()+".jpg");  
			}else
			{
				picture = new File(Constants.SNAP_FILE_PATH+(index+1)+"-snap.jpg");  
			}
			try {
				boolean flag = true;
				//一甲丙益USB摄像头拍照反向问题
				if(flag){
					FileOutputStream fos = new FileOutputStream(picture.getPath());  
					fos.write(params[0]); 
					fos.close();   
				}
				if(handler != null){
					handler.sendMessage(handler.obtainMessage(1001));
				}

			} catch (Exception e) {  
				e.printStackTrace(); 
				if(handler != null){
					handler.sendMessage(handler.obtainMessage(1003));
				}
			}  
			return null;  
		}  
	}

	public static ArrayList<Camera> getCameras() {
		int numberOfCameras = getNumberOfCameras();
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		ArrayList<Camera> cameras=new ArrayList<>();
		for (int i = 0; i < numberOfCameras; i++) {
			getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK
					||cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT) {
				cameras.add(Camera.open(i));
			}
		}
		return cameras;
	}

}
