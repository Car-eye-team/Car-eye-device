
/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
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
	
	/**
	 * Checks if the app has permission to write to device storage
	 * If the app does not has permission then the user will be prompted to
	 * grant permissions
	 * @param activity
	*/
	public static void verifyStoragePermissions(Activity activity) {
	// Check if we have write permission
	int permission = ActivityCompat.checkSelfPermission(activity,
	Manifest.permission.WRITE_EXTERNAL_STORAGE);

	    if (permission != PackageManager.PERMISSION_GRANTED) {
	// We don't have permission so prompt the user
	ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
	REQUEST_EXTERNAL_STORAGE);
	}
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Constants.setParam(this);	
		int version = android.os.Build.VERSION.SDK_INT;
		Log.d("CMD", "version : " + version);	
		if(version>=23)
		{
			verifyStoragePermissions(MainActivity.this);
			
		}
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
		
		//startService(new Intent(MainActivity.this, CommandService.class));	
		finish();
	}
}
