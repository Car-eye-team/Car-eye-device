/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import java.net.InetSocketAddress;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

/**    
 *     
 * 项目名称：DSS_808    
 * 类名称：DsMinaClient    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016-6-28 下午6:27:30    
 * 修改人：Administrator    
 * 修改时间：2016-6-28 下午6:27:30    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CommCenterClient {

	private static final String TAG = "DSCenterClient";

	/**
	 * 连接
	 */
	public static void connect() {

		if (CommCenterUsers.session == null || !(CommCenterUsers.session.isConnected())) {

			String ip = "";
			String port = "";
			try {
				CommCenterUsers.isConnector = false;
				CommCenterUsers.minaConnector = new NioSocketConnector();
				CommCenterUsers.minaConnector.getSessionConfig().setReadBufferSize(131072);
				CommCenterUsers.minaConnector.setHandler(new CommCenterClientHandler());
				try {

					ip = SPutil.getComm().getString("master_server_ip", "");
					port = SPutil.getComm().getString("master_server_port", "");

					//判断IP为域名，还是IP
					boolean isip = ParseUtil.isIp(ip);
					if(!isip){
						//非IP进行域名转换成IP
						ip = ParseUtil.domainToip(ip);
					}

				} catch (Exception e) {
					AppLog.e(ExceptionUtil.getInfo(e), e);
					e.printStackTrace();
				}

				AppLog.i(TAG,"Client: 连接服务中心:"+ip+",端口:"+port);
				CommCenterUsers.future = CommCenterUsers.minaConnector.connect(new InetSocketAddress(ip, Integer.parseInt(port)));
				CommCenterUsers.future.awaitUninterruptibly();
				CommCenterUsers.session = CommCenterUsers.future.getSession();

			} catch (Exception e) {
				e.printStackTrace();
				CommCenterUsers.minaConnector = null;
				AppLog.i(TAG,"Client: 连接服务中心:"+ip+",端口:"+port+"失败"+e.getMessage());
			}
		} else {
			CommCenterUsers.isConnector = true;
		}

	}



}
