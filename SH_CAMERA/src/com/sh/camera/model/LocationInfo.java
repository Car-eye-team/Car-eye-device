
/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */

package com.sh.camera.model;

/**
 * @author zhangrong
 *
 */
public class LocationInfo {

	/**
	 * 纬度
	 */
	private double latitude;
	
	/**
	 * 经度
	 */
	private double longitude;
	
	public LocationInfo(double longitude, double latitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
