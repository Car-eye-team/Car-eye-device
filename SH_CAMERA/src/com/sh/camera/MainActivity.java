
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class MainActivity extends Activity {
	
	public static MainActivity mainactivity;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Constants.setParam(this);		
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
