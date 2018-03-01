/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

/**
 * 
 */
package com.sh.camera.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sh.camera.model.GPSLocationInfo;
import com.sh.camera.model.LocationInfo;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.db.SystemDataBiz;
import com.sh.camera.socket.utils.ConstantsState;
import com.sh.camera.socket.utils.DistanceUtil;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author zhangrong
 *
 */
public class ShCommService extends Service {

	private static final String TAG = "ShCommService";

	private static ShCommService instance;

	private static LocationManager lManager = null;

	/**
	 * GPS信息实体
	 */
	private static GPSLocationInfo gpslocationInfo = null;
	/**
	 * 位置实体
	 */
	private LocationInfo myLocationInfo = null;
	
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();

	/**
	 * 获取实例
	 * @return
	 */
	public static ShCommService getInstance() {
		if (instance == null) {
			instance = new ShCommService();
		}
		return instance;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//启动GPS监听
		initGPS();
		//启动通讯
		initComm();
		

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	/**
	 * 通讯初始化
	 */
	public void initComm(){
		//第一次启动时初始化通讯SDK系统参数
		boolean first =  SPutil.getComm().getBoolean("shcomm_first",true);
		if(first){
			SPutil.getCommEditor().putBoolean("shcomm_first",false).commit();
			SystemDataBiz.initData();
		}else{
			SharedPreferences sp = ShCommService.getInstance().getSharedPreferences("fcoltest", ShCommService.getInstance().MODE_PRIVATE);
			String terminal = sp.getString("name", Constants.STREAM_NAME);
			Editor commEditor = SPutil.getCommEditor();
			commEditor.putString("comm_terminal", terminal);
			commEditor.commit();
		}
		
		//启动通讯
		CommCenterUsers.restartTimerConnectSvr();
	}

	/**
	 * GPS定位初始化
	 */
	public void initGPS(){
		try {
			lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			//监听状态
			lManager.addGpsStatusListener(listener);
			//绑定监听，有4个参数   
			//参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
			//参数2，位置信息更新周期，单位毫秒   
			//参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息   
			//参数4，监听   
			//备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
			// 1秒更新一次，或最小位移变化超过1米更新一次；
			//注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
			lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GPS获取位置监听
	 */
	private LocationListener locationListener = new LocationListener() {
		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
			//AppLog.i(TAG, "位置信息变化");

			if(location != null){

				long time = location.getTime();
				double lng = location.getLongitude();
				double lat = location.getLatitude();
				double altitude = location.getAltitude();
				float speed = location.getSpeed()*36f;
				float direction = location.getBearing();

				/*AppLog.i(TAG, "时间："+time);
				AppLog.i(TAG, "经度："+lng);
				AppLog.i(TAG, "纬度："+lat);
				AppLog.i(TAG, "海拔："+altitude);
				AppLog.i(TAG, "方向："+direction);*/

				/** 通过前后两个经纬度得出两地距离，并计算里程，保存里程*/
				if(speed>5){
					LocationInfo locNew = new LocationInfo(lng, lat);
					if(myLocationInfo != null){
						DistanceUtil.setMileage(myLocationInfo,locNew);
					}
					myLocationInfo = locNew;
				}

				ConstantsState.setStateValue(1, "1");
				gpslocationInfo = new GPSLocationInfo();
				gpslocationInfo.setAltitude((int)altitude);
				gpslocationInfo.setLat((int)(lat*1E6));
				gpslocationInfo.setLng((int)(lng*1E6));
				gpslocationInfo.setSpeed((int)speed);
				gpslocationInfo.setDirection((int)direction);
				//String date = DateUtils.gpsTimeToTime(String.valueOf(time), "yyyy-MM-dd HH:mm:ss");
				gpslocationInfo.setTime(String.valueOf(time));

				//保存最新GPS位置为最后一次有效位置信息
				Editor commEditor = SPutil.getCommEditor();
				commEditor.putInt("lat", (int)(lat*1E6));
				commEditor.putInt("lng", (int)(lng*1E6));
				commEditor.putInt("altitude", (int)altitude);
				commEditor.putInt("speed", (int)speed);
				commEditor.putInt("direction", (int)direction);
				commEditor.putString("time", String.valueOf(time));
				commEditor.commit();

			}else{
				ConstantsState.setStateValue(1, "0");
				gpslocationInfo = null;
			}
		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			//GPS状态为可见时
			case LocationProvider.AVAILABLE:
				AppLog.i(TAG, "当前GPS状态为可见状态");
				break;
				//GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				AppLog.i(TAG, "当前GPS状态为服务区外状态");
				ConstantsState.setStateValue(1, "0");
				gpslocationInfo = null;
				break;
				//GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				AppLog.i(TAG, "当前GPS状态为暂停服务状态");
				ConstantsState.setStateValue(1, "0");
				gpslocationInfo = null;
				break;
			}
		}

		/**
		 * GPS开启时触发
		 */ 
		public void onProviderEnabled(String provider) {
			lManager.getLastKnownLocation(provider);
			AppLog.i(TAG, "GPS开启");
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			gpslocationInfo = null;
			ConstantsState.setStateValue(1, "0");
			AppLog.i(TAG, "GPS禁用");
		}
	};


	/**
	 * 状态监听
	 */
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			//第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				AppLog.i(TAG, "====GPS已定位=========");
				//修改GPS状态为已定位
				ConstantsState.setStateValue(1, "1");
				break;
				//卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				//获取当前状态
				GpsStatus gpsStatus=lManager.getGpsStatus(null);
				//获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();  
				Iterator<GpsSatellite> it = gpsStatus.getSatellites().iterator();  
				numSatelliteList.clear();  
				int count = 0;  
				while (it.hasNext() && count <= maxSatellites) {  
					GpsSatellite s = it.next();
					if(s.getSnr()!=0){//只有信躁比不为0的时候才算搜到了星
						numSatelliteList.add(s);
						count++;
					}  
				}
				if(numSatelliteList.size() < 4){
					ConstantsState.setStateValue(1, "0");
				}
				//AppLog.i(TAG, "卫星状态改变:"+numSatelliteList.size());
				break;
				//定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				AppLog.i(TAG, "定位启动");
				ConstantsState.setStateValue(1, "0");
				break;
				//定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				AppLog.i(TAG, "定位结束");
				gpslocationInfo = null;
				ConstantsState.setStateValue(1, "0");
				break;
			}
		};
	};

	/**
	 * 获取GPS位置信息
	 * @return
	 */
	public GPSLocationInfo getLocInfo(){
		GPSLocationInfo locationInfo = null;
		//gps定位获取数据
		/*if(gpslocationInfo != null){
			//gpslocationInfo.setTime(null);
			locationInfo = gpslocationInfo;
		}else{*/
		SharedPreferences preferences = SPutil.getComm();
		String time = preferences.getString("time", null);
		int lng = preferences.getInt("lng", 0);
		int lat = preferences.getInt("lat", 0);
		int altitude = preferences.getInt("altitude", 0);
		int speed = preferences.getInt("speed", 0);
		int direction = preferences.getInt("direction", 0);
		locationInfo = new GPSLocationInfo();
		locationInfo.setAltitude((int)altitude);
		locationInfo.setLat((int)(lat));
		locationInfo.setLng((int)(lng));
		locationInfo.setSpeed(speed);
		locationInfo.setDirection((int)direction);
		locationInfo.setTime(String.valueOf(time));
		//}
		return locationInfo;
	}


}	
