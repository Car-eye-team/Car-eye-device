/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.sh.camera.service.MainService;

public class UpdateActivity extends Activity {
	
	public static UpdateActivity activity;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update);
		
		activity = this;
		//(EditText) findViewById(R.id.set_editText1);
	}
	
	boolean isSend = false;
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		activity = null;
		if(!isSend){
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.PASSWINFULL);
			sendBroadcast(intent);
		}
	}
	
}
