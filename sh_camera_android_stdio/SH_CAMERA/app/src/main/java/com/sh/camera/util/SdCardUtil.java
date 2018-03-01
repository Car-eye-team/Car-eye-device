/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import java.io.File;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：SdCardUtil    
 * 类描述： SD卡相关工具类   
 * 创建人：Administrator    
 * 创建时间：2016年10月25日 上午9:25:52    
 * 修改人：Administrator    
 * 修改时间：2016年10月25日 上午9:25:52    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class SdCardUtil {
	
	/**
	 * 检查SD卡是否存在
	 * @return true 存在  false 不存在
	 */
	public static boolean checkSdCardUtil(){
		try {
			
			File file = new File(Constants.CAMERA_FILE_PATH);
			if(!file.exists()){
				return false;
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
}
