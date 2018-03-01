/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.socket.db;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.utils.CommConstants;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：SystemDataBiz    
 * 类描述： 系统数据初始化   
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:35:17    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:35:17    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class SystemDataBiz {

	/**
	 * 初始化系统数据
	 */
	public static void initData() {

		try {
			
			Editor commEditor = SPutil.getCommEditor();
			
			//心跳包间隔
			commEditor.putInt("comm_gps_interval", CommConstants.COMM_GPS_INTERVAL);
			
			String terminal = ServerManager.getInstance().getStreamname();
			//终端设备号
			commEditor.putString("comm_terminal", terminal);
			
			//主服务器参数
			commEditor.putString("master_server_apn", CommConstants.MASTER_SERVER_APN);
			commEditor.putString("master_server_ip", CommConstants.MASTER_SERVER_IP);
			commEditor.putString("master_server_port", CommConstants.MASTER_SERVER_PORT);

			//备用服务器参数
			commEditor.putString("backup_server_apn", CommConstants.BACKUP_SERVER_APN);
			commEditor.putString("backup_server_ip", CommConstants.BACKUP_SERVER_IP);
			commEditor.putString("backup_server_port", CommConstants.BACKUP_SERVER_PORT);
			
			//总里程
			commEditor.putString("total_mileage", CommConstants.TOTAL_MILEAGE);
			
			commEditor.commit();

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}
	}

}
