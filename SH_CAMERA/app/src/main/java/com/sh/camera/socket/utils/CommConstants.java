/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.socket.utils;


public class CommConstants {
	/**GPS信息上传间隔(秒)*/
	public static int COMM_GPS_INTERVAL = 30;
	

	/**主服务器APN*/
	public static String MASTER_SERVER_APN = "3gnet";
	
	/**主服务器IP*/
	public static String MASTER_SERVER_IP = "39.108.246.45";
	
	/**主服务器端口*/
	public static String MASTER_SERVER_PORT = "9999";
	
	/**备服务器APN*/
	public static String BACKUP_SERVER_APN = "3gnet";
	
	/**备服务器IP*/
	public static String BACKUP_SERVER_IP = "39.108.246.45";
	
	/**备服务器端口*/
	public static String BACKUP_SERVER_PORT = "9999";
	
	/**总里程 单位米*/
	public static String TOTAL_MILEAGE = "0.0";
	
	public static Boolean LOGIN_FLAG = true;
	
	/**数据库名称*/
	public final static String DATABASE_NAME = "camera.db";
	
	/**数据库表名*/
	public final static String TABLE_NAME = "datamsg";
	
	/**数据表字段_id*/
	public static final String ID = "_id";
	/**数据表字段 序列号*/
	public static final String SEQ = "seq";
	/**数据表字段 消息ID*/
	public static final String MSGID = "msgid";
	/**数据表字段 数据*/
	public static final String DATAHEX = "datahex";
	/**数据表字段 创建时间*/
	public static final String CREATETIME = "createtime";
}
