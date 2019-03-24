/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.socket.timer;

import java.util.TimerTask;

import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.coder.CommEncoder;
import com.sh.camera.socket.coder.CommEncoderUtil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：TimerHeartBeat    
 * 类描述：心跳定时器    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午3:15:42    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午3:15:42    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class TimerHeartBeat extends TimerTask {

	private static final String TAG = "TimerHeartBeat";

	@Override
	public void run() {
		try {
			AppLog.i(TAG,"发送心跳包.......");
			byte[] vehicleFlameoutDataByte = CommEncoder.getLocationInformation();
			CommCenterUsers.witeMsg(vehicleFlameoutDataByte,1);
		
			//保存系统时间
			CommEncoderUtil.saveSystemTime();
			
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}
	}
}