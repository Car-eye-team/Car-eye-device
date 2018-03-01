/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.bll;

import com.sh.camera.service.MainService;
import com.sh.camera.util.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ParamsBiz {
	
	/***********************************偏好配置，参数设置对象**********************************/
	private static SharedPreferences sp = null;
	private static Editor editor = null;

	private static SharedPreferences getSp(){
		if(sp == null){
			sp = MainService.getInstance().getSharedPreferences("fcoltest", Context.MODE_PRIVATE);
		}
		return sp;
	}
	private static Editor getSpEditor(){
		if(editor == null){
			editor = getSp().edit();
		}
		return editor;
	}
	
	/***************************参数key***************************/
	private static final String KEY_UPDATE_IP = "uodate_ip";
	private static final String KEY_UPDATE_PORT = "uodate_port";
	
	
	/***************************参数操作***************************/
	/**版本升级IP*/
	public static String getUpdateIp(){
		String param = getSp().getString(KEY_UPDATE_IP, Constants.UPDATE_IP);
		return param;
	}
	
	public static void setUpdateIP(String param){
		getSpEditor().putString(KEY_UPDATE_IP, param).commit();
	}
	
	/**版本升级端口*/
	public static String getUpdatePort(){
		String param = getSp().getString(KEY_UPDATE_PORT, Constants.UPDATE_PORT);
		return param;
	}
	
	public static void setUpdatePort(String param){
		getSpEditor().putString(KEY_UPDATE_PORT, param).commit();
	}
}
