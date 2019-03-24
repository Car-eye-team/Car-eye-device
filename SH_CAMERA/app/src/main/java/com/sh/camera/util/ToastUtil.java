/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.util;

import android.content.Context;
/**
 * 显示工具
 */
public class ToastUtil {
	/**
	 * 
	 * @param context 上下文对
	 * @param msg 显示内容
	 */
	public static void showToast(Context context, String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		MyToast.showToast(context, msg, true, 0);
	}
	public static void longToast(Context context, String msg){
//		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		MyToast.showToast(context, msg, true, 0);
	}
	
	public static void showToast(Context context, int resId){
		String msg = context.getResources().getString(resId);
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		MyToast.showToast(context, msg, true, 0);
	}
	
	public static void longToast(Context context, int resId){
		String msg = context.getResources().getString(resId);
//		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		MyToast.showToast(context, msg, true, 1);
	}
}
