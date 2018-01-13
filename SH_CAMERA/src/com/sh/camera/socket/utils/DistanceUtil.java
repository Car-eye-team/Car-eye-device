/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.socket.utils;

import com.sh.camera.model.LocationInfo;

/**
 * 两地距离工具类
 * @author wlh
 */
public class DistanceUtil {

	private static final double EARTH_RADIUS = 6378137.0;

	/**
	 * 保存里程
	 * @param locationA	GPS位置A
	 * @param locationB	GPS位置B
	 */
	public static void setMileage(LocationInfo locationA, LocationInfo locationB){
		try {
			double distance = getMileage();
			double myDistance = getDistance(locationA, locationB);
			if(myDistance != 0.0){
				myDistance = distance + myDistance;
				SPutil.commEditor.putString("total_mileage", String.valueOf(myDistance)).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取里程（单位：米）
	 * @return 返回里程
	 */
	public static double getMileage(){
		String distance = SPutil.comm.getString("total_mileage", "0.0");
		return Double.parseDouble(distance);
	}

	/**
	 * 获取两地之间的距离 （单位：米）
	 * @param locationA	GPS位置A
	 * @param locationB	GPS位置B
	 * @return 返回距离
	 */
	public static double getDistance(LocationInfo locationA, LocationInfo locationB){
		double latA = locationA.getLatitude();
		double lngA = locationA.getLongitude();
		double latB = locationB.getLatitude();
		double lngB = locationB.getLongitude();
		return getDistance(lngA, latA, lngB, latB);
	}

	/**
	 * 获取两地之间的距离 （单位：米）
	 * @param longitude1	经度1
	 * @param latitude1		纬度1
	 * @param longitude2	经度2
	 * @param latitude2		纬度2
	 * @return 距离
	 */
	public static double getDistance(double longitude1, double latitude1,  
			double longitude2, double latitude2) {  
		double distance = 0.0;
		try {
			double Lat1 = rad(latitude1);  
			double Lat2 = rad(latitude2);  
			double a = Lat1 - Lat2;  
			double b = rad(longitude1) - rad(longitude2);  
			distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
					+ Math.cos(Lat1) * Math.cos(Lat2)  
					* Math.pow(Math.sin(b / 2), 2)));  
			distance = distance * EARTH_RADIUS;  
			distance = Math.round(distance * 10000) / 10000;  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return distance;  
	}  

	private static double rad(double d) {  
		return d * Math.PI / 180.0;  
	}
}
