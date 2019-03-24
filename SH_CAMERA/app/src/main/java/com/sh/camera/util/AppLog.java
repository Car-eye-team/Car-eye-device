/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**    
 *     
 * 项目名称：DSS_COMM_SDK    
 * 类名称：AppLog    
 * 类描述：    
 * 创建人：zr    
 * 创建时间：2015-7-17 下午2:30:59    
 * 修改人：zr    
 * 修改时间：2015-7-17 下午2:30:59    
 * 修改备注：    
 * @version 1.0  
 *     
 */
@SuppressLint("SimpleDateFormat")
public class AppLog {

	/**
	 * 日志输出开关 true 开 false 关
	 */
	public static Boolean MYLOG_SWITCH = true; 
	
	/**
	 * 日志写入文件开关  true 开 false 关 默认关闭
	 */
	public static Boolean MYLOG_WRITE_TO_FILE = false;
	

	/**
	 * 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	 */
	private static char MYLOG_TYPE='v';
	
	/**
	 * 日志文件在sdcard中的路径
	 */
	private static String MYLOG_PATH_SDCARD_DIR = Constants.SD_CARD_PATH+"/dscameralog/";
	
	/**
	 * sd卡中日志文件的最多保存天数
	 */
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;  
	
	/**
	 * 本类输出的日志文件名称
	 */
	private static String MYLOGFILEName = "Log.txt";  

	/**
	 * 日志的输出格式  
	 */
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 日志文件格式 
	 */
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 

	/**
	 * 警告信息  
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag, Object msg) { 
		log(tag, msg.toString(), 'w');  
	}  

	/**
	 * 错误信息  
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, Object msg) { 
		log(tag, msg.toString(), 'e');  
	}  

	/**
	 * 调试信息 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, Object msg) { 
		log(tag, msg.toString(), 'd');  
	}  

	public static void i(String tag, Object msg) {
		log(tag, msg.toString(), 'i');  
	}  

	public static void v(String tag, Object msg) {  
		log(tag, msg.toString(), 'v');  
	}  

	public static void w(String tag, String text) {  
		log(tag, text, 'w');  
	}  

	public static void e(String tag, String text) {  
		log(tag, text, 'e');  
	}  

	public static void d(String tag, String text) {  
		log(tag, text, 'd');  
	}  

	public static void i(String tag, String text) {  
		log(tag, text, 'i');  
	}  

	public static void v(String tag, String text) {  
		log(tag, text, 'v');  
	}  

	/**
	 * 根据tag, msg和等级，输出日志   
	 * @param tag 
	 * @param msg 日志消息
	 * @param level 日志级别
	 */
	private static void log(String tag, String msg, char level) {

		if (MYLOG_SWITCH) {  
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息  
				Log.e(tag, msg);  
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
				Log.w(tag, msg);  
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
				Log.d(tag, msg);  
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {  
				Log.i(tag, msg);  
			} else {  
				Log.v(tag, msg);  
			}
			if (MYLOG_WRITE_TO_FILE) {
				writeLogtoFile(String.valueOf(level), tag, msg);  
			}
		}  
	}  


	/**
	 * 将日志写入日志文件
	 * @param mylogtype
	 * @param tag
	 * @param text
	 */
	private static void writeLogtoFile(String mylogtype, String tag, String text) {  

		File f = new File(Constants.SD_CARD_PATH);
		if(!f.exists()){
			return;
		}
		
		try {
			long sdsize = FileOper.getSDAllSize(Constants.SD_CARD_PATH);
			if(sdsize<3){
				
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.e(ExceptionUtil.getInfo(e), e);
		}


		//查看SD卡剩余内存情况
		try {
			long size = FileOper.getSDFreeSize(Constants.SD_CARD_PATH);
			if(size<3){
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			AppLog.e(ExceptionUtil.getInfo(e), e);
		}

		Date nowtime = new Date(); 
		String needWriteFiel = logfile.format(nowtime);  
		String needWriteMessage = "["+myLogSdf.format(nowtime)+"]    " + mylogtype  + "    " + tag + "    " + text; 

		//检查文件路径是否存在
		File filePath = new File(MYLOG_PATH_SDCARD_DIR);
		
		if(!filePath.exists()){
			filePath.mkdir();
		}

		//检查文件路径是否存在
		filePath = new File(MYLOG_PATH_SDCARD_DIR+"log/");
		if(!filePath.exists()){
			filePath.mkdir();
		}


		File file = new File(MYLOG_PATH_SDCARD_DIR+"log/", needWriteFiel  + MYLOGFILEName);

		try {
			FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖  
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
			bufWriter.write(needWriteMessage);  
			bufWriter.newLine();  
			bufWriter.close();  
			filerWriter.close();  
		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();  
		}  
	}  

	/** 
	 * 删除制定的日志文件 
	 * */  
	public static void delFile() {// 删除日志文件  
		String needDelFiel = logfile.format(getDateBefore());  
		File file = new File(MYLOG_PATH_SDCARD_DIR, needDelFiel + MYLOGFILEName);  
		if (file.exists()) {  
			file.delete();  
		}  
	}  

	/** 
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名 
	 * */  
	private static Date getDateBefore() {  
		Date nowtime = new Date();  
		Calendar now = Calendar.getInstance();  
		now.setTime(nowtime);  
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);  
		return now.getTime();  
	}  
}
