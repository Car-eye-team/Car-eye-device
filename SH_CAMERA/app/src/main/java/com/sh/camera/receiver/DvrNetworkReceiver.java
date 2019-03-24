/**    
 * Description: 多森车辆诊断系统   
 * 文件名：NetworkReceiver.java   
 * 版本信息：    
 * 日期：2015-7-17  
 * Copyright 深圳市航盛车云技术有限公司 Copyright (c) 2015     
 * 版权所有    
 *    
 */
package com.sh.camera.receiver;

import java.util.List;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.util.AppLog;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

/**    
 *     
 * 项目名称：DSS_OBD    
 * 类名称：NetworkReceiver    
 * 类描述：网络广播监听    
 * 创建人：zr    
 * 创建时间：2015-7-17 下午5:41:58    
 * 修改人：zr    
 * 修改时间：2015-7-17 下午5:41:58    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class DvrNetworkReceiver extends BroadcastReceiver {
	private static final String TAG = "NetworkReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String networkstate = "";
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			boolean success = false;
			//获得网络连接服务
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			if (connManager!=null) {  
                NetworkInfo [] networkInfos=connManager.getAllNetworkInfo();  
                for (int i = 0; i < networkInfos.length; i++) {  
                    State state = networkInfos[i].getState();  
                    if (NetworkInfo.State.CONNECTED==state) {  
                    	success = true;
                        break;  
                    }  
                }  
            }  
			
			if (!success) {
				AppLog.i(TAG, "您的网络连接已断开:"+networkstate);
				CommCenterUsers.restartTimerConnectSvr();
				boolean isRun = isServiceRunning(context,ShCommService.class.getName());
				if(!isRun){
					//启动通讯service
					context.startService(new Intent(context, ShCommService.class));	
				}
			}else{
				AppLog.i(TAG, "您的网络连接已连接:"+networkstate);
			}
			
		}

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
