/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.timer;

import java.util.TimerTask;

import com.sh.camera.socket.CommCenterClient;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

/**
 * 项目名称：DSS_808    
 * 类名称：TimerConnecter    
 * 类描述：  连接定时器  
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午3:13:38    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午3:13:38    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class TimerConnecter extends TimerTask {
	private static final String TAG = "TimerConnecter";
	/**
	 * 鉴权连接次数
	 */
	private int times = 0;
	@Override
	public void run() {

		try {

			if(CommCenterUsers.checkConnParam()){
				if (!CommCenterUsers.isConnTimer) {
					times = times+1;
					//默认连接10次，如果10次连接不上将不再进行连接
					if(times < 20){
						AppLog.i(TAG,"开始连接通信平台.....................................");
						CommCenterClient.connect();
					}else{
						CommCenterUsers.timerConnecter.cancel();
					}
				}else{
					if(CommCenterUsers.timerConnecter != null){
						CommCenterUsers.timerConnecter.cancel();
					}
				}
			}

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}

	}
}