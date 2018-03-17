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
import com.jsr.sdk.UsbCameraManager;
import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.service.MainService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.coder.CommEncoder;
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


	public static void startVideoUpload2(final String ipstr, final String portstr, final String serialno,  int CameraId){

		final int i = CameraId -1;
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {	                                     
				//预览之前判断是否回放，如果回放先结束回放
				if(CameraUtil.VIDEO_FILE_UPLOAD){
					stopVideoFileStream();
					try
					{
						Thread.sleep(100);				        
					}catch (Exception localException)
					{				          
					}       
				}

				//预览之前先停止上传
				if(VIDEO_UPLOAD[i]){
					stopVideoUpload(i);
					try
					{
						Thread.sleep(100);				        
					}catch (Exception localException)
					{				          
					}        			
				}
				//初始化推流工具
				VIDEO_UPLOAD[i] = true;
				MainService.getInstance().startVideoUpload2(ipstr, portstr,serialno,i);
			}
		});                      


	}

	/**
	 * 开始视频上传
	 * @param i 通道
	 */
	public static void startVideoUpload(int i){

		//预览之前判断是否回放，如果回放先结束回放
		if(CameraUtil.VIDEO_FILE_UPLOAD){
			stopVideoFileStream();
		}

		//预览之前先停止上传
		if(VIDEO_UPLOAD[i]){
			stopVideoUpload(i);
		}

		//初始化推流工具
		VIDEO_UPLOAD[i] = true;
		MainService.getInstance().startVideoUpload2(ServerManager.getInstance().getIp(),ServerManager.getInstance().getPort(),ServerManager.getInstance().getStreamname(),i);
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
					MainService.mPusher.startfilestream(ip, port,streamName, map.get("path"));
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
	 * @param context
	 * @param cameraid 通道ID
	 * @param splaysec 开始秒
	 * @param eplaysec 结束秒
	 * @param filename 文件名称
	 */
	public static void startVideoFileStream(final int cameraid,final int splaysec,final int eplaysec,final String filename,final Handler handler){

		try {

			SharedPreferences sp = MainService.getInstance().getSharedPreferences("fcoltest", MainService.getInstance().MODE_PRIVATE);
			final String ip = sp.getString("ip",Constants.SERVER_IP);
			final String port = sp.getString("port",Constants.SERVER_PORT);
			final String streamname = sp.getString("name",Constants.STREAM_NAME);
			final String streamName =  String.format("%s-channel=%d.sdp", streamname,cameraid);

			Log.d("CMD", " filePath upload"+filename);

			if(MainService.mPusher == null){
				MainService.mPusher = new Pusher();
			}

			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {	 

					//上传之前先停止回放
					if(CameraUtil.VIDEO_FILE_UPLOAD){
						CameraUtil.stopVideoFileStream();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}

					//上传之前先停止预览
					int i = cameraid-1;
					if(VIDEO_UPLOAD[i]){
						stopVideoUpload(i);
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}			

					CameraUtil.VIDEO_FILE_UPLOAD = true;
					Log.d("CMD", " restart"+filename);
					MainService.mPusher.startfilestream( ip, port, streamName, filename,splaysec,eplaysec,handler);
				}
			});



		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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
						MainService.mPusher. CarEyeStopNativeFileRTSP(i);
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
			if(Constants.PRODUCT_TYPE == 3){
				//有方USB摄像头需要单独处理
				if (index == 1) {
					MainService.picid = MainService.rules[index];
					UsbCameraManager.getInstance().photoGraph(1,type);
					return true;
				}
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
				if(Constants.PRODUCT_TYPE == 2){
					if(index == 1){
						Bitmap bmp = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
						Bitmap bitmap = ImageUtil.reverseBitmap(bmp, 0);
						try {
							FileOutputStream out = new FileOutputStream(picture);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
							out.flush();
							out.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						flag = false;
					}
				}

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

}
