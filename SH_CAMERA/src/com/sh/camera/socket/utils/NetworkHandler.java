/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */

package com.sh.camera.socket.utils;

import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：NetworkHandler    
 * 类描述：网络服务管理    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午6:49:52    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午6:49:52    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class NetworkHandler {

	private static final String TAG = "NetworkHandler";

	private static NetworkHandler instance = new NetworkHandler();


	/**
	 * 
	 * 实例化当前上下文对象
	 * 
	 */
	public static NetworkHandler getInstance() {
		if (instance == null) {
			instance = new NetworkHandler();
		}
		return instance;
	}

	public static Activity activityContext;

	public void SetActivityContext(Activity act) {
		activityContext = act;
	}


	/**
	 * 
	 * 获取网络类型
	 *  return 0 wifi 1 2 :2G 网络 3 3G网络 8 高速网络
	 */
	public static int getNetWorkType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return netType;
		}

		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			int subtype = networkInfo.getSubtype();
			netType = subtype;
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 0;
		}
		return netType;
	}

	/**
	 * 
	 * 检测网络是否连接
	 * 
	 */
	public static boolean isConnect(Context context) {
		boolean flag = false;
		try {
			if(context != null){
				ConnectivityManager connectivity = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (connectivity != null) {
					NetworkInfo info = connectivity.getActiveNetworkInfo();
					if (null != info && info.isConnected()) {
						if (info.getState() == NetworkInfo.State.CONNECTED) {
							flag = true;
						}
					}
				}
			}
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取版本名称
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String getVersionName(Context context) throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}


}
