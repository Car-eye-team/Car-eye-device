/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 项目名称：DSS_CAMERA    
 * 类名称：ExceptionUtil    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月18日 上午10:01:46    
 * 修改人：Administrator    
 * 修改时间：2016年10月18日 上午10:01:46    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class ExceptionUtil {

	/**
	 * 获取详细的异常信息     
	 * @param e
	 * @return 详细异常描述信息
	 */
	public static String getInfo(Exception e){  
		StringBuffer errormsg = new StringBuffer();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(out);
		e.printStackTrace(pout);
		String ret = new String(out.toByteArray());
		pout.close();
		try {
			out.close();
		} catch (Exception ex) {
		}
		errormsg.append(ret);
		return errormsg.toString();
	}
}
