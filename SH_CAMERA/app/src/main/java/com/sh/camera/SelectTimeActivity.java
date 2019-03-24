/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.sh.camera.util.DateUtil;
import com.sh.camera.util.ToastUtil;
import com.sh.camera.widget.timepicker.ScreenInfo;
import com.sh.camera.widget.timepicker.WheelMain;

public class SelectTimeActivity extends Activity implements OnClickListener{

	private Button btn_confirm,btn_back;
	private TextView tv_start_time,tv_end_time;
	private WheelMain wheelMain;
	private Calendar calendar;
	private Calendar calendarST;
	private Calendar calendarET;
	private int year;
	private int month;
	private int day;
	private int h;
	private int m;
	public static Calendar calendarSTCache;
	public static Calendar calendarETCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_backplay);

		setupViews();
		addListener();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		h = calendar.get(Calendar.HOUR_OF_DAY);
		m = calendar.get(Calendar.MINUTE);

		if(calendarSTCache == null){
			calendarST = Calendar.getInstance();
			calendarST.set(year, month, day-1, h, m-1);
		}else{
			calendarST = calendarSTCache;
		}
		Date date = calendarST.getTime();
		tv_start_time.setText(DateUtil.df13.format(date));

		if(calendarETCache == null){
			calendarET = Calendar.getInstance();
			calendarET.set(year, month, day, h, m-1);
		}else{
			calendarET = calendarETCache;
		}
		date = calendarET.getTime();
		tv_end_time.setText(DateUtil.df13.format(date));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void setupViews() {
		// TODO Auto-generated method stub
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		btn_confirm = (Button) findViewById(R.id.btn_confirm);
		btn_back = (Button) findViewById(R.id.btn_back);
	}


	private void addListener() {
		// TODO Auto-generated method stub
		tv_start_time.setOnClickListener(this);
		tv_end_time.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_confirm:
			submit();
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.tv_start_time:
			choiceTime(tv_start_time);
			break;
		case R.id.tv_end_time:
			choiceTime(tv_end_time);
			break;
		}
	}

	/** 选择时间*/
	private void choiceTime(final TextView tv) {
		// TODO Auto-generated method stub
		String title = "";
		if(tv.getId() == R.id.tv_start_time){
			title = "设置开始时间";
			calendar = calendarST;
		}else{
			calendar = calendarET;
			title = "设置结束时间";
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		final View timepickerview = inflater.inflate(R.layout.datepicker,
				null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		wheelMain = new WheelMain(timepickerview, true);
		wheelMain.screenheight = screenInfo.getHeight();

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		h = calendar.get(Calendar.HOUR_OF_DAY);
		m = calendar.get(Calendar.MINUTE);

		wheelMain.initDateTimePicker(year, month, day, h, m);
		wheelMain.setTextSize(26, 30);
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setView(timepickerview)
		.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//				tv.setText(wheelMain.getTime());
				dialog.dismiss();
			}
		})
		.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv.setText(DateUtil.dateFormatForTimePicker(wheelMain.getTime()));
				try {
					if(tv.getId() == R.id.tv_start_time){
						String startTime = tv_start_time.getText().toString();
						long sTime = DateUtil.df13.parse(startTime).getTime();
						calendarST.setTimeInMillis(sTime);
					}else{
						String endTime = tv_end_time.getText().toString();
						long eTime = DateUtil.df13.parse(endTime).getTime();
						calendarET.setTimeInMillis(eTime);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			}
		}).show();
	}

	/**提交*/
	private void submit() {
		// TODO Auto-generated method stub
		try {
			String startTime = tv_start_time.getText().toString();
			String endTime = tv_end_time.getText().toString();
			long sTime = DateUtil.df13.parse(startTime).getTime();
			long eTime = DateUtil.df13.parse(endTime).getTime();
			long currentTime = System.currentTimeMillis();
			if(sTime > currentTime){
				ToastUtil.showToast(this, "开始时间超前了");
				return;
			}
			if(eTime > currentTime){
				ToastUtil.showToast(this, "结束时间超前了");
				return;
			}
			if(sTime > eTime){
				ToastUtil.showToast(this, "开始时间不能大于结束时间！");
				return;
			}
			startTime = DateUtil.df1.format(new Date(sTime));
			endTime = DateUtil.df1.format(new Date(eTime));
			
			calendarSTCache = calendarST;
			calendarETCache = calendarET;

			// BCD[7]	格式为 YYYY-MM-DD-hh-mm-ss
			Intent intent = new Intent();
			intent.putExtra("etime", startTime);
			intent.putExtra("stime", endTime);
			setResult(Activity.RESULT_OK, intent);
			finish();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
