/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：AppUtil    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月19日 下午1:17:22    
 * 修改人：Administrator    
 * 修改时间：2016年10月19日 下午1:17:22    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class AppUtil {
	
	/**
	 * 判断service 是否运行
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {

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
