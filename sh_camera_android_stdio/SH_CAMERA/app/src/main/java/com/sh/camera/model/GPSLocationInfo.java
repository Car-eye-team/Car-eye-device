/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */



package com.sh.camera.model;

import java.io.Serializable;

/**
 * 
 * @author zhangrong
 *
 */
public class GPSLocationInfo implements Serializable{

	private static final long serialVersionUID = 7966693343090369428L;

	/**方向*/
	private Integer direction;
	
	/**高度**/
	private Integer altitude;
	
	/**速度**/
	private Integer speed;
	
	/**gps时间*/
	private String time;
	
	/**
	 * 纬度
	 */
	private Integer lat;

	/**
	 * 经度
	 */
	private Integer lng;
	
	/***
	 * 总里程
	 */
	private Integer miles;
	
	/***
	 * 定位状态 0 未定位 1 已定位
	 */
	private Integer gpsflag;
	
	
	public Integer getMiles() {
		return miles;
	}

	public void setMiles(Integer miles) {
		this.miles = miles;
	}

	public Integer getGpsflag() {
		return gpsflag;
	}

	public void setGpsflag(Integer gpsflag) {
		this.gpsflag = gpsflag;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Integer getAltitude() {
		return altitude;
	}

	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getLat() {
		return lat;
	}

	public void setLat(Integer lat) {
		this.lat = lat;
	}

	public Integer getLng() {
		return lng;
	}

	public void setLng(Integer lng) {
		this.lng = lng;
	}

}
