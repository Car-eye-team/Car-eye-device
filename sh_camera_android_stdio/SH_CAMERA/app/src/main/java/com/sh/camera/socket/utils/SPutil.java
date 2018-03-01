/**    
 * Description: 深圳市晟鸿科技有限公司车联网平台   
 * 文件名：SPutil.java   
 * 版本信息：    
 * 日期：2015-7-17  
 * Copyright 深圳市晟鸿科技有限公司 Copyright (c) 2015     
 * 版权所有    
 *    
 */
package com.sh.camera.socket.utils;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：SPutil    
 * 类描述：公共参数配置工具类 
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:14:13    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:14:13    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class SPutil {

	@SuppressWarnings("static-access")
	public static SharedPreferences comm = ShCommService.getInstance().getSharedPreferences("commdata", ShCommService.getInstance().MODE_PRIVATE);
	public static Editor commEditor = comm.edit();
	
	/**
	 * ====GPS位置信息相关====
	 * time 时间
	 * lng  经度
	 * lat  纬度
	 * altitude 高度
	 * speed 速度
	 * direction 方向
	 * 
	 * ====通讯相关=====
	 * comm_regit_flag 注册状态
	 * comm_auth_code 鉴权码
	 * comm_heart_interval 心跳
	 * 
	 * comm_sim 通讯SIM卡号
	 * 
	 * master_server_switch 主服务器开关 0 关 1 开
	 * master_server_apn 主服务器APN
	 * master_server_ip 主服务器IP
	 * master_server_port 主服务器端口
	 * 
	 * backup_server_switch 主服务器开关 0 关 1 开
	 * backup_server_apn 主服务器APN
	 * backup_server_ip 主服务器IP
	 * backup_server_port 主服务器端口
	 * 
	 * logswitch 日志开关 0 关 1 开
	 * 
	 */
	
	/**
	 * 获取公共参数设置对象
	 * @return 返回设置对象
	 */
	public  static SharedPreferences getComm(){
		return comm;
	}
	
	/**
	 * 获取通用络参数设置编辑对象
	 * @return 返回编辑对象
	 */
	public static Editor getCommEditor(){
		return commEditor;
	}
	
	

}
