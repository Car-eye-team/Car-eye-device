/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.service.MainService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.Constants;
import com.sh.camera.version.VersionBiz;

public class SetActivity extends Activity {

	EditText et1,et2,et3,et4,et_editTextkzport;
	Button bt1,bt2;
	CheckBox[] cbs;
	int[] cbids = {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4};
	RadioGroup rg;
	public static int[] rgids = {R.id.radio0, R.id.radio1};

	SharedPreferences sp;
	SharedPreferences.Editor sped;
	private TextView tv_version;

	/**是否有版本检测跳转至该界面*/
	private boolean fromUpdateVersion = false;
	public static SetActivity instance;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_activity);
		instance = this;
		tv_version = (TextView) findViewById(R.id.tv_version);
		et1 = (EditText) findViewById(R.id.set_editText1);
		et2 = (EditText) findViewById(R.id.set_editText2);
		et_editTextkzport = (EditText) findViewById(R.id.et_editTextkzport);
		et3 = (EditText) findViewById(R.id.set_editText3);
		et4 = (EditText) findViewById(R.id.set_editText4);
		bt1 = (Button) findViewById(R.id.set_button1);
		bt2 = (Button) findViewById(R.id.set_button2);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);



		cbs = new CheckBox[4];
		for (int i = 0; i < cbs.length; i++) {
			cbs[i] = (CheckBox) findViewById(cbids[i]);
		}

		sp = getSharedPreferences("fcoltest", MODE_PRIVATE);
		sped = sp.edit();

		rg.check(ServerManager.getInstance().getMode());
		et1.setText(ServerManager.getInstance().getIp());
		et2.setText(ServerManager.getInstance().getPort());
		et3.setText(ServerManager.getInstance().getStreamname());
		et_editTextkzport.setText(ServerManager.getInstance().getAddport());
		//et4.setText(ServerManager.getInstance().getFramerate());
		et4.setText(sp.getString(Constants.fps, String.valueOf(Constants.FRAMERATE)));



		String rulestr = ServerManager.getInstance().getRule();
		for (int i = 0; i < rulestr.length(); i++) {
			int id = Integer.parseInt(rulestr.substring(i, i+1));
			cbs[id].setChecked(true);
		}
		if(rg.getCheckedRadioButtonId()==rgids[0]){
			cbs[2].setChecked(false);
			cbs[3].setChecked(false);
			cbs[2].setVisibility(View.GONE);
			cbs[3].setVisibility(View.GONE);
		}else if(rg.getCheckedRadioButtonId()==rgids[1]){
			cbs[2].setVisibility(View.VISIBLE);
			cbs[3].setVisibility(View.VISIBLE);
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup r, int i) {
				if(r.getCheckedRadioButtonId()==rgids[0]){
					cbs[2].setChecked(false);
					cbs[3].setChecked(false);
					cbs[2].setVisibility(View.GONE);
					cbs[3].setVisibility(View.GONE);
				}else if(r.getCheckedRadioButtonId()==rgids[1]){
					cbs[2].setVisibility(View.VISIBLE);
					cbs[3].setVisibility(View.VISIBLE);
				}
			}
		});


		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String rstr = "";
				for (int i = 0; i < cbs.length; i++) {
					if(cbs[i].isChecked()){
						rstr+=String.valueOf(i);
					}
				}
				sped.putString(Constants.rule, rstr);
				sped.putString(Constants.ip, et1.getText().toString());
				sped.putString(Constants.port, et2.getText().toString());
				sped.putString(Constants.name, et3.getText().toString());
				sped.putString(Constants.addPort, et_editTextkzport.getText().toString());
				sped.putString(Constants.fps, et4.getText().toString());
				sped.putInt(Constants.mode, rg.getCheckedRadioButtonId());
				sped.commit();
				
				Editor commEditor = SPutil.getCommEditor();
				commEditor.putString("comm_terminal", et3.getText().toString());
				commEditor.commit();
				
				//重启通讯
				CommCenterUsers.restartTimerConnectSvr();
				
				Intent intent = new Intent(MainService.ACTION);
				intent.putExtra("type", MainService.RESTART);
				sendBroadcast(intent);
				isSend = true;
				finish();
			}
		});

		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		VersionBiz v = new VersionBiz(this);
		String version = "";
		try {
			version = v.getVersionName(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_version.setText("v"+version);
		
		fromUpdateVersion = (Boolean) getIntent().getBooleanExtra("fromUpdateVersion", false);
		v.doCheckVersion(false,fromUpdateVersion);

		/*boolean acc_uptaapp_init = new PrefBiz(this).getBooleanInfo("acc_uptaapp_init", false);
		try {
			if (acc_uptaapp_init) {
				VersionBiz v = new VersionBiz(this);
				v.doCheckVersion(true);
				new PrefBiz(this).putBooleanInfo("acc_uptaapp_init", false);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}

	/**
	 * 给设置端口 输入框赋值
	 * @param text
	 */
	private void setSetttingport(String text){
		et_editTextkzport.setText(text);
	}
	/**
	 * 获取设置端口 输入框赋值
	 * @param text
	 */
	private String getSetttingPort(){
		return et_editTextkzport.getText().toString().trim();
	}
	private String getSettingPortPre(){

		String settingPort = sp.getString(Constants.addPort,Constants.SERVER_ADDPORT);
		return settingPort;
	}


	boolean isSend = false;
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		instance = null;
		if(!isSend){
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.PASSWINFULL);
			sendBroadcast(intent);
		}
	}

}
