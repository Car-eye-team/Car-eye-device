/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.sh.camera.service.MainService;
import com.sh.camera.util.Constants;



public class MainActivity extends Activity {
	public static MainActivity mainactivity;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
	        Manifest.permission.READ_EXTERNAL_STORAGE,
	Manifest.permission.WRITE_EXTERNAL_STORAGE };
	//public FloatWindowManager.MyListener listener;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Constants.setParam(this);
		int version = android.os.Build.VERSION.SDK_INT;
		Log.d("CMD", "version : " + version);
		//setContentView(R.layout.activity_splash);
		/*listener=new FloatWindowManager.MyListener() {
			@Override
			public void getData(boolean res) {
				if (res)
				{
					getPermission();
				}
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				FloatWindowManager.getInstance().setOnListener(listener);
				//	getPermission();

			}
		}).start();*/
		mainactivity = this;
		getPermission();
		//启动通讯service\
		//FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this);

	}

	private void getPermission() {
		Log.e("TAG","OK");
		if(!MainService.isrun){
			startService(new Intent(MainActivity.this, MainService.class));
		}else{
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.FULLSCREEN);
			sendBroadcast(intent);
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*Boolean checkPer=	FloatWindowManager.getInstance().CheckPer(MainActivity.this);
		if (checkPer)
		{
			getPermission();
		}*/
	}
}



