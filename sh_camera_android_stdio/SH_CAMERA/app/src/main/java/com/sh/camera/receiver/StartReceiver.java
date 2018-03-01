package com.sh.camera.receiver;

import java.util.List;

import com.sh.camera.MainActivity;
import com.sh.camera.bll.PrefBiz;
import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartReceiver extends BroadcastReceiver {
	private static final String tag = "StartReceiver.";
	private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";  
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION_BOOT)) {  
			Constants.setParam(context);
			AppLog.i(tag, "========开机广播=============");
			boolean isRun = isServiceRunning(context,MainService.class.getName());

			if(!isRun){
				context.startService(new Intent(context, MainService.class));
				//启动通讯service
				context.startService(new Intent(context, ShCommService.class));	
			}
			new PrefBiz(context).putBooleanInfo("acc_uptaapp_init", true);
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
