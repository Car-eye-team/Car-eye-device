/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

import android.content.Context;

/**    
 *     
 * 项目名称：DSS_808    
 * 类名称：DSMinaUsersHandler    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016-6-28 下午6:29:14    
 * 修改人：Administrator    
 * 修改时间：2016-6-28 下午6:29:14    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CommCenterClientHandler extends IoHandlerAdapter {

	private static final String TAG = "DSMinaUsersHandler";
	private static StringBuffer tcp7EBuffer  = new StringBuffer();
	// 当客户端连接进入时  
	@Override  
	public void sessionOpened(IoSession session) throws Exception {  
		System.out.println("incomming 客户端: " + session.getRemoteAddress());  
	}  

	@Override  
	public void exceptionCaught(IoSession session, Throwable cause)  
			throws Exception {  
		CommCenterUsers.session = null;
		CommCenterUsers.restartTimerConnectSvr();
		AppLog.i(TAG,"服务器连接断开..........................");
	}  

	// 当客户端发送消息到达时  
	@Override  
	public void messageReceived(IoSession session, Object message)  
			throws Exception { 

		CommCenterUsers.session = session;
		IoBuffer ioBuffer = (IoBuffer)message;
		byte[] bytes = new byte[ioBuffer.limit()];
		ioBuffer.get(bytes); 
		String bytesStr = ParseUtil.parseByte2HexStr(bytes);
		AppLog.i(TAG,"收到通讯平台GPRS服务器发送过来的数据:"+bytesStr);
		//进行粘包处理
		stick7EPackage(bytes,ShCommService.getInstance());

	}  

	@Override  
	public void sessionClosed(IoSession session) throws Exception {  
		AppLog.i(TAG,"===========远程服务器主动断开=========");
		CloseFuture future = session.close(true);
		future.addListener(new IoFutureListener(){
			public void operationComplete(IoFuture future){
				if(future instanceof CloseFuture){
					((CloseFuture)future).setClosed();
				}
			}
		});
		CommCenterUsers.restartTimerConnectSvr();
	}  

	@Override  
	public void sessionCreated(IoSession session) throws Exception { 

		CommCenterUsers.isConnector = true;
		CommCenterUsers.session = session;
		//设置连接标志为已连接
		CommCenterUsers.isConnTimer = true ;
		//关闭连接定时器
		if(CommCenterUsers.timerConnecter!=null){
			CommCenterUsers.timerConnecter.cancel();
		}

		try {
			if(CommCenterUsers.timerAuth != null){
				CommCenterUsers.timerAuth.cancel();
			}
			//启动鉴权timer
			CommCenterUsers.startTimerAuthSvr();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	} 

	/**
	 * 数据包粘包处理
	 * @param bytes 数据包
	 * @param context 上下文context对象
	 */
	public void stick7EPackage(byte[] bytes,Context context){
		try {

			String data = ParseUtil.parseByte2HexStr(bytes).toUpperCase();

			String [] dataAry = data.split("7E7E");
			int num = dataAry.length;
			if(num>1){				
				for(int j = 0;j<num; j++){
					if(j==0){
						tcp7EBuffer.append(dataAry[j]+"7E");
						int tcplen  = tcp7EBuffer.toString().length();
						if(tcplen%2 == 0){
							BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
							tcp7EBuffer  = new StringBuffer();
						}
					}else if(j == (num-1)){
						if(data.endsWith("7E7E")){
							tcp7EBuffer.append("7E"+dataAry[j]+"7E");
							int tcplen  = tcp7EBuffer.toString().length();
							if(tcplen%2 == 0){
								BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
								tcp7EBuffer  = new StringBuffer();
							}
							tcp7EBuffer.append("7E");
						}else{
							if(dataAry[j].endsWith("7E")){
								tcp7EBuffer.append("7E"+dataAry[j]);
								int tcplen  = tcp7EBuffer.toString().length();
								if(tcplen%2 == 0){
									BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
									tcp7EBuffer  = new StringBuffer();
								}
							}else{
								tcp7EBuffer.append("7E"+dataAry[j]);
							}
						}

					}else{
						tcp7EBuffer.append("7E"+dataAry[j]+"7E");
						int tcplen  = tcp7EBuffer.toString().length();
						if(tcplen%2 == 0){
							BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
							tcp7EBuffer  = new StringBuffer();
						}
					}
				}

			}else{
				if(data.endsWith("7E7E")){
					tcp7EBuffer.append(dataAry[0]+"7E");
					int tcplen  = tcp7EBuffer.toString().length();
					if(tcplen%2 == 0){
						BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
						tcp7EBuffer  = new StringBuffer();
					}
					tcp7EBuffer.append("7E");
				}else{
					if(data.endsWith("7E")){
						tcp7EBuffer.append(dataAry[0]);
						if(data.startsWith("7E")){
							int tcplen  = tcp7EBuffer.toString().length();
							if(tcplen%2 == 0){
								BusinessProcess.decoderData(ParseUtil.parseHexStr2Byte(tcp7EBuffer.toString()),context);
								tcp7EBuffer  = new StringBuffer();
							}
						}
					}else{
						tcp7EBuffer.append(dataAry[0]);
					}
				}

			}	
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
		}
	}


}  

