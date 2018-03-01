/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.db.DataMsgDao;
import com.sh.camera.socket.timer.TimerAuth;
import com.sh.camera.socket.timer.TimerConnecter;
import com.sh.camera.socket.timer.TimerHeartBeat;
import com.sh.camera.socket.utils.CommConstants;
import com.sh.camera.socket.utils.NetworkHandler;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.StringUtil;


/**    
 *     
 * 项目名称：DSS_808    
 * 类名称：DSCenterUsers    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016-6-28 下午6:28:15    
 * 修改人：Administrator    
 * 修改时间：2016-6-28 下午6:28:15    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CommCenterUsers {

	private static final String TAG = "DSCenterUsers";

	public static boolean isConnector = false;
	public static IoConnector minaConnector = null;
	public static ConnectFuture future = null;
	public static IoSession session = null;
	public static boolean isConnTimer = false;
	public static boolean isHeartBeat = false;
	public static boolean isAuth = false;
	public static String rememberId = null;

	public static Timer connTimer = null;
	public static TimerConnecter timerConnecter = null;
	public static Timer heartTimer = null;
	public static TimerHeartBeat heartTask = null;

	//鉴权timer相关
	public static Timer authTimer = null;
	public static TimerAuth timerAuth = null;

	//鉴权timer相关
	public static Timer lTimer = null;

	/**
	 * 检测连接参数
	 * @return
	 */
	public static boolean checkConnParam(){

		try {
			//判断网络是否连接
			String ip = SPutil.getComm().getString("master_server_ip", "");
			String port = SPutil.getComm().getString("master_server_port", "");

			if(StringUtil.isNull(ip) || StringUtil.isNull(port)){
				return false;
			}else{
				//判断网络是否连接 true 已连接 false 未连接
				boolean isConnected = NetworkHandler.isConnect(ShCommService.getInstance());
				AppLog.i(TAG,"=======================网络连接=====================+"+isConnected);
				if(isConnected){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 重新连接
	 */
	public static void restartTimerConnectSvr(){
		try {
			CommConstants.LOGIN_FLAG = true;

			//关闭心跳定时器，重新连接
			if(heartTask != null){
				heartTask.cancel();
			}

			if(timerAuth != null){
				timerAuth.cancel();
			}

			if(timerConnecter != null){
				timerConnecter.cancel();
			}

			isConnTimer = false;
			isHeartBeat = false;
			isAuth = false;
			isConnector = false;

			if(CommConstants.LOGIN_FLAG){
				startTimerConnectSvr();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 启动连接
	 */
	public static void startTimerConnectSvr() {

		AppLog.i(TAG,"=====启动通讯连接定时器...");
		session = null;
		try {

			if(connTimer!=null){
				connTimer.cancel();
			}
			connTimer = new Timer();

			if(timerConnecter != null){
				timerConnecter.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		timerConnecter = new TimerConnecter();
		connTimer.schedule(timerConnecter, 5000, 15000);
	}

	/**
	 * 启动鉴权
	 */
	public static void startTimerAuthSvr(){
		if (isConnTimer) {
			try {
				if(timerAuth != null){
					timerAuth.cancel();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			authTimer = new Timer();
			timerAuth = new TimerAuth();
			authTimer.schedule(timerAuth, 0, 30000);
		}
	}



	/**
	 * 启动心跳定时器
	 * @param interval 定时间隔
	 */
	public static void startHeartBeat(int interval) {

		try {
			//启用心跳之前先判断是否有心跳正在运行
			if(heartTask != null){
				heartTask.cancel();
				heartTask = null;
			}
			heartTimer = new Timer();
			heartTask = new TimerHeartBeat();
			heartTimer.schedule(heartTask, 2000, interval*1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送消息
	 * @param bodyByte 数据包
	 * @param type 1 正常发送 2 数据补发
	 * @return 0  成功 1 通讯链路断开 2 发送异常 3 发送数据包为空
	 */
	public static int witeMsg(byte[] bodyByte,int type){
		int re = 0;
		if(bodyByte != null){
			String bodyhex = ParseUtil.parseByte2HexStr(bodyByte);
			try {
				if(session != null){
					if(session.isConnected()){
						session.write(IoBuffer.wrap(bodyByte));
					}else{
						re = 1;
						startTimerConnectSvr();
					}
				}else{
					re = 1;
					startTimerConnectSvr();
				}
			} catch (Exception e) {
				e.printStackTrace();
				re = 2;
			}

			//保证数据只补发一次
			if(type == 1){
				//将发送数据包写入数据库中
				bodyByte = ParseUtil.yxReversal(bodyByte);
				int nOff = 1;
				//消息ID
				byte[] idByte = ParseUtil.byteTobyte(bodyByte, nOff, 2);
				int msgid = Integer.parseInt(ParseUtil.parseByte2HexStr(idByte), 16);
				nOff+=10;
				// 消息体流水号
				int seq = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(bodyByte, nOff, 2)),16);
				nOff+=2;

				if(msgid != 0x0001){

					AppLog.i(TAG, "[seq:"+seq+"] [msgid:"+msgid+"] [bodyhex:"+bodyhex+"]写入数据库中");

					//获取系统时间
					String createtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()); 
					//插入数据库中
					DataMsgDao.getInstance(ShCommService.getInstance()).insert(seq, msgid, bodyhex,createtime);
					System.out.println("数据库记录条数："+DataMsgDao.getInstance(ShCommService.getInstance()).getCount());

				}
			}

		}else{
			re = 3;
		}
		return re;
	}
}
