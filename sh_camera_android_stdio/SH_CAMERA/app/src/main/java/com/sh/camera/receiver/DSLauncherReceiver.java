/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sh.camera.codec.MediaCodecManager;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.CameraUtil;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：DSLauncherReceiver    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月12日 上午10:06:14    
 * 修改人：Administrator    
 * 修改时间：2016年10月12日 上午10:06:14    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class DSLauncherReceiver extends BroadcastReceiver {

	protected static final String ACTION_VIDEO_PREVIEW = "com.dss.launcher.ACTION_VIDEO_PREVIEW";
	protected static final String ACTION_VIDEO_PLAYBACK = "com.dss.launcher.ACTION_VIDEO_PLAYBACK";
	protected static final String ACTION_REGIST_SUCCESS  = "com.dss.launcher.ACTION_REGIST_SUCCESS";
	protected static final String ACTION_VIDEO_STOP_PLAYBACK  = "com.dss.launcher.ACTION_VIDEO_STOP_PLAYBACK";
	protected static final String ACTION_VIDEO_FILE_PLAYBACK  = "com.dss.launcher.ACTION_VIDEO_FILE_PLAYBACK";

	//拍照
	protected static final String ACTION_TAKE_PICTURE  = "com.dss.launcher.ACTION_TAKE_PICTURE";
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			if (intent.getAction().equals(ACTION_VIDEO_PLAYBACK)) {
				int id = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
				int type = intent.getIntExtra("EXTRA_TYPE", 0);  //类型  0 图片 1 录像
				String stime = intent.getStringExtra("EXTRA_STIME");  //回放开始时间
				String etime = intent.getStringExtra("EXTRA_ETIME");  //回放结束时间
				//AppLog.d("DSLauncherReceiver", ACTION_VIDEO_PLAYBACK);					
				//CameraFileUtil.screenVideoFile(stime, etime, id);
				

			}else if(intent.getAction().equals(ACTION_VIDEO_PREVIEW)){
				int id = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
				int type = intent.getIntExtra("EXTRA_TYPE", 0);  //操作类型  0 开始预览 1 停止预览
			/*	if(type == 0){
					CameraUtil.startVideoUpload((id-1));
				}else{
					CameraUtil.stopVideoUpload((id-1));
				}*/
			}else if(intent.getAction().equals(ACTION_REGIST_SUCCESS)){
				//设备号
				String deviceid = intent.getStringExtra("EXTRA_DEVICE_ID");
				SharedPreferences sp = context.getSharedPreferences("fcoltest", context.MODE_PRIVATE);
				SharedPreferences.Editor sped = sp.edit();
				sped.putString("name", deviceid);
				sped.commit();
			}else if(intent.getAction().equals(ACTION_VIDEO_STOP_PLAYBACK)){
				CameraUtil.stopVideoFileStream();
				AppLog.d("DSLauncherReceiver", ACTION_VIDEO_STOP_PLAYBACK);

			}else if(intent.getAction().equals(ACTION_VIDEO_FILE_PLAYBACK)){
				try {
					int cameraid = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
					String filename = intent.getStringExtra("EXTRA_FILENAME");
					int splaysec = intent.getIntExtra("EXTRA_SPLAYSEC", 0); 
					int eplaysec = intent.getIntExtra("EXTRA_EPLAYSEC", 0);
					CameraUtil.startVideoFileStream(cameraid, splaysec, eplaysec, filename,null);					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}else if(intent.getAction().equals(ACTION_TAKE_PICTURE)){
				//拍照
				try {
					int cameraid = intent.getIntExtra("EXTRA_ID", 1);  //通道ID
					String resolution = intent.getStringExtra("EXTRA_RESOLUTION");  //分辨率
					MediaCodecManager.TakePicture((cameraid-1), 1);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
