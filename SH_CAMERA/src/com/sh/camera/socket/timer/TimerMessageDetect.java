/**
 * 
 */
package com.sh.camera.socket.timer;

import java.util.TimerTask;

import android.content.Intent;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.AppUtil;
import com.sh.camera.util.DateUtil;

/**
 * @author 张荣
 * 消息检测
 */
public class TimerMessageDetect extends TimerTask {
	private static final String tag = "TimerMessageDetect";

	@Override
	public void run() {
		try {
			String msgtime = SPutil.getComm().getString("receive_msg_time", DateUtil.getSQLDate());
			long diff = DateUtil.secBetween( DateUtil.getSQLDate(), msgtime);
			AppLog.i(tag, "===平台消息检测,上次消息时间："+msgtime+",间隔："+diff+"秒");
			
			//2分钟与平台么有数据连接重启通讯连接
			if(diff > 120){
				AppLog.i(tag, "超过2分钟与平台么有数据连接重启通讯连接");
				boolean isRun = AppUtil.isServiceRunning(MainService.getInstance(),ShCommService.class.getName());
				if(!isRun){
					//启动通讯service
					MainService.getInstance().startService(new Intent(MainService.getInstance(), ShCommService.class));	
				}else{
					CommCenterUsers.restartTimerConnectSvr();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
