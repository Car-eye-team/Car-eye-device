/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.receiver;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;

import com.sh.camera.service.MainService;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;
import com.sh.camera.util.StringUtil;

/**    
 *     
 * 项目名称：    
 * 类名称：SystemReceiver    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月19日 下午5:06:28    
 * 修改人：Administrator    
 * 修改时间：2016年10月19日 下午5:06:28    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class SystemReceiver extends BroadcastReceiver {
	private static final String tag = "SystemReceiver.";

	protected static final String ACTION_ACC_ON = "com.dss.launcher.ACTION_ACC_ON";
	protected static final String HEARTBEAT = "auto.intent.action.HEARTBEAT";   //一甲丙益设备专用心跳广播
	protected static final String ACTION_SHUTDOWN  = "com.dss.launcher.ACTION_ACC_OFF";   //一甲丙益设备专用长按电源键重启广播
	public static final String ACTION_HOME = "com.dss.app.carcall.ACTION_HOME";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		AppLog.i(tag, "========系统广播============="+intent.getAction());
		
		if(action.equals(ACTION_ACC_ON) || action.equals(HEARTBEAT)){
			String carNumber = getCarNumber(intent);
			Log.d("CMD", String.format(" SystemReceiver:"+carNumber));
			if(!StringUtil.isNull(carNumber)){
				// 主控的开机广播和设置车牌号时，传递过来车牌号
				
			}
			if(!MainService.isrun){
				context.startService(new Intent(context, MainService.class));
			}else{
				boolean isRun = isServiceRunning(context,MainService.class.getName());
				if(!isRun){
					context.startService(new Intent(context, MainService.class));
				}
			}

			Constants.StartFlag = true;					
		}else if(action.equals(ACTION_SHUTDOWN)){
			//停止录音
			Log.d("CMD", String.format(" ACTION_SHUTDOWN:"));

			MainService.getInstance().StopCameraprocess();
			MainService.getInstance().removeView();

		}else if(ACTION_HOME.equals(action)){
			if(MainService.isWindowViewShow){
				Intent intentHome = new Intent(MainService.ACTION);
				intentHome.putExtra("type", MainService.MINIMIZE);
				context.sendBroadcast(intentHome);
			}
		}
	}

	private String getCarNumber(Intent intent) {
		// TODO Auto-generated method stub
		byte[] carNumberByte = intent.getByteArrayExtra("extra_car_number");
		if(carNumberByte != null && carNumberByte.length >0){
			try {
				String carNumber = new String(carNumberByte, "GBK");
				return carNumber;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isServiceRunning(Context mContext, String className) {

		boolean IsRunning = false;
		try {
			ActivityManager activityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningServiceInfo> serviceList = activityManager
					.getRunningServices(30);
			if (!(serviceList.size() > 0)) {
				return false;
			}

			for (int i = 0; i < serviceList.size(); i++) {
				if (serviceList.get(i).service.getClassName().equals(className) == true) {
					IsRunning = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return IsRunning;
	}

}
