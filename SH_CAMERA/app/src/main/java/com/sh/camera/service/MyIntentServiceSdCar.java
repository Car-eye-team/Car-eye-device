/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.service;

import com.sh.camera.bll.SdCardBiz;
import com.sh.camera.util.Constants;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;


/**
 * 计价器模式下 乘客下车
 * @author zxt
 *
 */
public class MyIntentServiceSdCar extends IntentService {

	public final static String TAG="MyIntentServiceSdCar";
	public static MyIntentServiceSdCar instance;
	public static Handler handler;

	public MyIntentServiceSdCar() {

		super("com.dss.camera.service.MyIntentServiceSdCar");
		instance = this;
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
			}
		};	
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
//		Log.i(TAG,"begin onHandleIntent() in "+this);
		org.json.JSONObject obj=null;

//		try {
//			obj = new org.json.JSONObject(thisD);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		updateContact();
		SystemClock.sleep(20);

//		Log.i(TAG,"end onHandleIntent() in "+this);
	}

	/**
	 * 结束
	 */
	public void updateContact() {
	
		//SdCardBiz.getInstance().getDetectionTWO();
		MainService.disk.DiskCleanprocess();
	}


	public void onDestroy()
	{
		super.onDestroy();
		
	}
}