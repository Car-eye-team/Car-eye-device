/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.coder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sh.camera.socket.utils.SPutil;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;

/**    
 *     
 * 项目名称：SH_808    
 * 类名称：ObdEncoderUtil    
 * 类描述：    
 * 创建人：zr    
 * 创建时间：2015-10-29 下午7:58:37    
 * 修改人：zr    
 * 修改时间：2015-10-29 下午7:58:37    
 * 修改备注：    
 * @version 1.0  
 *     
 */
@SuppressLint("SimpleDateFormat")
public class CommEncoderUtil {
	private static final String TAG = "ObdEncoderUtil";

	/**
	 * 保存最后一次时间
	 */
	public static void saveSystemTime(){
		String triptime = new SimpleDateFormat("ddHHmm").format(Calendar.getInstance().getTime()); // 获取系统当前时间
		String systemtime = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime()); // 获取系统当前时间
		Editor commEditor = SPutil.getCommEditor();
		commEditor.putString("triptime", triptime);
		commEditor.putString("systemtime", systemtime);
		commEditor.commit();
	}

	/**
	 * 获取系统时间
	 * @return
	 */
	public static String getSystemTime(){
		String systemdate = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime()); // 获取系统当前时间
		/*if(systemdate.startsWith("100101080")){
			systemdate = SPutil.getComm().getString("systemtime", "");
		}*/
		return systemdate;
	}

}
