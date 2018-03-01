/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.util.Constants;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;



public class MainActivity extends Activity {
	
	public static MainActivity mainactivity;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
	        Manifest.permission.READ_EXTERNAL_STORAGE,
	Manifest.permission.WRITE_EXTERNAL_STORAGE };
	


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Constants.setParam(this);	
		int version = android.os.Build.VERSION.SDK_INT;
		Log.d("CMD", "version : " + version);	

		if(!MainService.isrun){
			startService(new Intent(MainActivity.this, MainService.class));			
		}else{
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.FULLSCREEN);
			sendBroadcast(intent);
		}		
		mainactivity = this;
		//启动通讯service
		startService(new Intent(MainActivity.this, ShCommService.class));		
		finish();
	}
}
