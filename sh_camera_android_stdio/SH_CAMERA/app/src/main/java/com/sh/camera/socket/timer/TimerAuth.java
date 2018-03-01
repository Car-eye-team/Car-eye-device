/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.socket.timer;

import java.util.TimerTask;

import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.coder.CommEncoder;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：TimerAuth    
 * 类描述：鉴权定时器    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午3:10:26    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午3:10:26    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class TimerAuth extends TimerTask {
	private static final String TAG = "TimerAuth";
	
	/**
	 * 鉴权连接次数
	 */
	private int authtimes = 0;
	@Override
	public void run() {
		authtimes = authtimes+1;
		try {
			if(authtimes < 5){
				AppLog.i(TAG,"Client: 开始鉴权,鉴权次数:"+authtimes);
				//向服务器发送鉴权
				CommCenterUsers.witeMsg(CommEncoder.getAuthentication(),1);
			}else{
				if(CommCenterUsers.timerAuth != null){
					AppLog.i(TAG,"关闭鉴权定时器......");
					CommCenterUsers.timerAuth.cancel();
				}
			}
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}

	}
}
