/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.bll.ParamsBiz;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.coder.CommDecoder;
import com.sh.camera.socket.coder.CommEncoder;
import com.sh.camera.socket.db.DataMsgDao;
import com.sh.camera.socket.model.DSCommData;
import com.sh.camera.socket.model.PlatformResponse;
import com.sh.camera.socket.model.TerminalRegist;
import com.sh.camera.socket.model.VideoInfo;
import com.sh.camera.socket.utils.CommConstants;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.CommCameraFileUtil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;
import com.sh.camera.util.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.File;


/**
 *     
 * 项目名称：DSS_808    
 * 类名称：BusinessProcess    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年9月28日 下午3:09:53    
 * 修改人：Administrator    
 * 修改时间：2016年9月28日 下午3:09:53    
 * 修改备注：    
 * @version 1.0  
 *
 */
@SuppressLint("SimpleDateFormat")
public class BusinessProcess {

	private static final String TAG = "BusinessProcess";

	/**
	 * 解码808协议数据
	 * @param bytes
	 * @param context
	 */
	public static void decoderData(byte[] bytes, Context context){

		DSCommData dsCommData = CommDecoder.decoder808Data(bytes, context);
		if(dsCommData == null){
			AppLog.d(TAG, "协议解析失败");
		}else{
			//处理业务
			process(dsCommData.getMsgid(),dsCommData.getData(),dsCommData.getSeq(), context);
		}

	}

	/**
	 * 业务处理
	 * @param msgid 消息ID
	 * @param data 消息数据包
	 * @param context 上下文对象
	 */
	public synchronized static void process(int msgid,byte[] data,int seq, final Context context){
		switch (msgid) {

		case 0x8001:	// 平台通用应答

			try {
				PlatformResponse platformResponse = CommDecoder.decoderPlatformResponse(data);
				//根据消息ID、应答流水号删除数据库中的记录
				if(platformResponse != null){
					DataMsgDao.getInstance(ShCommService.getInstance()).delete(platformResponse.getReseq(), platformResponse.getRemsgid());

					switch (platformResponse.getRemsgid()) {
					//鉴权应答
					case 0x0102:
						if(platformResponse.getResult() == 0x00){
							//鉴权成功
							AppLog.i(TAG,"登录成功!");

							CommConstants.LOGIN_FLAG = true;

							//设置连接标志为已连接
							if(CommCenterUsers.timerAuth != null){
								AppLog.i(TAG,"关闭鉴权定时器......");
								CommCenterUsers.timerAuth.cancel();
							}

							//登录成功获取数据库中未发送的数据进行发送
							BussinessProcessUtil.dataReissue();

							//启动心跳维持连接
							int interval = SPutil.getComm().getInt("comm_gps_interval",30);
							CommCenterUsers.startHeartBeat(interval);
							
							//启动消息检测定时器
							CommCenterUsers.startMessageDetect();

						}else{
							AppLog.i(TAG,"登录失败!");

							CommConstants.LOGIN_FLAG = false;

							if(CommCenterUsers.timerAuth != null){
								AppLog.i(TAG,"关闭鉴权......");
								CommCenterUsers.timerAuth.cancel();
							}

						}
						break;

					default:
						break;
					}

				}

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x8100: //终端注册应答
			try {

				TerminalRegist terminalRegist = CommDecoder.decoderTerminalRegistResponse(data);
				if(terminalRegist != null){

					//注册成功启动鉴权定时器
					if(terminalRegist.getResult() == 0){
						//关闭连接定时器
						if(CommCenterUsers.timerConnecter!=null){
							CommCenterUsers.timerConnecter.cancel();
						}

						if(CommCenterUsers.timerAuth != null){
							CommCenterUsers.timerAuth.cancel();
						}
						//启动鉴权timer
						CommCenterUsers.startTimerAuthSvr();
					}
				}

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}

			break;

		case 0x8103:	// 设置参数
			try {


			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x5110:   //视频预览控制
			try {
				//业务处理
				int num = 0;
				// 通道ID
				int id = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//操作类型 0 实时预览 1 停止预览
				int type = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				/*Intent intent = new Intent("com.dss.launcher.ACTION_VIDEO_PREVIEW");
				intent.putExtra("EXTRA_ID", id);
				intent.putExtra("EXTRA_TYPE", type);*/
				Log.d("vedio", "Data"+data.length);
				//兼容老的car-eye-server 和 新的两个平台
				if(data.length>10)
				{
					int protocolType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)), 16);
					num += 1;
					//需要判断是否有IP 端口信息
					// 鏈嶅姟鍣↖P鍦板潃闀垮害	BYTE	闀垮害n
					int ipLen = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)), 16);
					num += 1;
					// 鏈嶅姟鍣↖P鍦板潃	STRING	瀹炴椂闊宠棰戞湇鍔″櫒IP鍦板潃
					String ip = Tools.byteToString(data, num, ipLen);
					num += ipLen;
					// 绔彛鍙凤紙TCP锛?WORD	瀹炴椂闊宠棰戞湇鍔″櫒TCP绔彛鍙?
					int port = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)), 16);
					num += 2;
					if(ServerManager.getInstance().getprotocol()== Constants.CAREYE_RTP_PROTOCOL)
					{
						ServerManager.getInstance().SetIP(ip);
						ServerManager.getInstance().SetPort("" + port);
					}

					//ServerManager.getInstance().setprotocol(protocolType);
					//Log.d("vedio", "protocolType" + protocolType + "  ip" + ip + "  port:" + port + "  protocol" + ServerManager.getInstance().getprotocol());
				}
				if(type == 0){
					CameraUtil.startVideoUpload((id-1),0);
				}else{
					CameraUtil.stopVideoUpload((id-1));
				}

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x5111:   //视频回放
			try {
				//业务处理
				int num = 0;
				// 通道ID
				int id = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				// 类型  0 图片 1 录像
				int type = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//起始时间
				String stime = ParseUtil.bcd2Str(ParseUtil.byteTobyte(data, num, 6)); 		
				num += 6;
				//结束时间
				String etime = ParseUtil.bcd2Str(ParseUtil.byteTobyte(data, num, 6)); 		
				num += 6;

				/*Intent intent = new Intent("com.dss.launcher.ACTION_VIDEO_PLAYBACK");
				intent.putExtra("EXTRA_ID", id);
				intent.putExtra("EXTRA_TYPE", type);
				intent.putExtra("EXTRA_STIME", stime);
				intent.putExtra("EXTRA_ETIME", etime);
				context.sendBroadcast(intent);*/
				if(data.length>20)
				{
					int protocolType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)), 16);
					num += 1;
					// 鏈嶅姟鍣↖P鍦板潃闀垮害	BYTE	闀垮害n
					int ipLen = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)), 16);
					num += 1;
					// 鏈嶅姟鍣↖P鍦板潃	STRING	瀹炴椂闊宠棰戞湇鍔″櫒IP鍦板潃
					String ip = Tools.byteToString(data, num, ipLen);
					num += ipLen;
					// 绔彛鍙凤紙TCP锛?WORD	瀹炴椂闊宠棰戞湇鍔″櫒TCP绔彛鍙?
					int port = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)), 16);
					num += 2;
					if(ServerManager.getInstance().getprotocol()== Constants.CAREYE_RTP_PROTOCOL) {
						ServerManager.getInstance().SetIP(ip);
						ServerManager.getInstance().SetPort("" + port);
					}
					//ServerManager.getInstance().setprotocol(protocolType);

				}
				CommCameraFileUtil.screenVideoFile(stime, etime, id);


			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x5112:   //停止视频回放
			try {
				//业务处理
				int num = 0;
				// 通道ID
				int id = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				CameraUtil.stopVideoFileStream();
			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x5113:   //回放指定文件
			try {

				System.out.println("data=="+ParseUtil.parseByte2HexStr(data));
				//业务处理
				int num = 0;
				// 通道ID
				int cameraid = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//文件名称
				byte[] filenameByte = ParseUtil.byteToSubstringToByte(data, num, data.length-num);
				String filename = ParseUtil.byteToString(filenameByte, 0, filenameByte.length);
				num = num +filenameByte.length+1;
				//播放开始时间
				//起始时间
				String splaysec = ParseUtil.bcd2Str(ParseUtil.byteTobyte(data, num, 4)); 		
				num += 4;
				//结束时间
				String eplaysec = ParseUtil.bcd2Str(ParseUtil.byteTobyte(data, num, 4)); 		
				num += 4;
				String filepath = Constants.CAMERA_FILE_PATH+filename;
				CameraUtil.startVideoFileStream(cameraid, Integer.parseInt(splaysec),Integer.parseInt(eplaysec), filepath,null);
			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9101:   //实时音视频传输请求
			try {
				//业务处理

				int num = 0;
				// 服务器IP地址长度
				int iplen = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//服务器IP地址
				byte[] ipbyte = ParseUtil.byteTobyte(data, num, iplen);
				num += iplen;
				String ip = ParseUtil.byteToString(ipbyte, 0, ipbyte.length);
				//TCP端口号
				int tcpPort = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//UDP端口号
				int udpPort = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//数据类型
				int type = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//码流类型
				int streamType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

				if(ServerManager.getInstance().getprotocol()==Constants.CAREYE_RTP_PROTOCOL)
				{
					ServerManager.getInstance().SetIP(ip);
					ServerManager.getInstance().SetPort(""+tcpPort);
				}		

				//设置上传服务器IP
				//ParamsBiz.setUpdateIP(ip);
				//设置上传服务器端口
				//ParamsBiz.setUpdatePort(String.valueOf(tcpPort));
				//Log.d("vedio", "protocolType" + logicChannel + "  ip" + ip + "  port:" + tcpPort + "  protocol" + ServerManager.getInstance().getprotocol());
				//开始传输
				String datatype0 = "音视频";
				String datatype1 = "视频";
				String datatype2 = "双向对讲";
				String datatype3 = "监听";
				String datatype4 = "中心广播";
				String datatype5 = "透传";
				Log.d(TAG, "0"+ datatype0 );
				Log.d(TAG, "1"+ datatype1 );
				Log.d(TAG, "2"+ datatype2 );
				Log.d(TAG, "3"+ datatype3 );
				Log.d(TAG, "4"+ datatype4 );
				Log.d(TAG, "5"+ datatype5 );
				if(type == 0 || type == 1) {
					CameraUtil.startVideoUpload((logicChannel - 1),0);
					AppLog.i(TAG,"传输类型："+type );
				}else if(type == 2){

					CameraUtil.startVideoUpload((logicChannel - 1), 1);
					AppLog.i(TAG,"传输类型："+type);

				}else if(type == 3){
					AppLog.i(TAG,"传输类型："+type);

				}


			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9102:   //音视频实时传输控制
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//控制指令
				int command = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//关闭类型
				int closeType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//码流切换类型
				int switchType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

				if(command ==0 || command ==2){
					CameraUtil.stopVideoUpload((logicChannel-1));
					Log.d("vedio", "stop stream");
				}else if(command ==3){
					//开始传输
					Log.d("vedio", "start stream");
							CameraUtil.startVideoUpload((logicChannel-1), 0);
				}

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9105:   //实时音视频状态通知
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//丢包率
				int lostRatio = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9201:   //平台下发远程录像回放请求
			try {
				int num = 0;
				// 服务器IP地址长度
				int iplen = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//服务器IP地址
				byte[] ipbyte = ParseUtil.byteTobyte(data, num, iplen);
				num += iplen;
				String ip = ParseUtil.byteToString(ipbyte, 0, ipbyte.length);
				//TCP端口号
				int tcpPort = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//UDP端口号
				int udpPort = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//数据类型
				int type = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//码流类型
				int streamType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//存储类型
				int memoryType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//回放方式
				int playBackType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//快进或快退倍速
				int playBackRatio = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//开始时间
				byte[] startTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String startTime = ParseUtil.bcd2Str(startTimebyte);
				num += 6;

				//结束时间
				byte[] endTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String endTime = ParseUtil.bcd2Str(endTimebyte);
				num += 6;
				
				if(ServerManager.getInstance().getprotocol()==Constants.CAREYE_RTP_PROTOCOL)
				{
					ServerManager.getInstance().SetIP(ip);
					ServerManager.getInstance().SetPort(""+tcpPort);
				}		
	
				//查找一个文件，然后上传到视频服务器，目前暂时只支持一个文件上传
				File file;
				file = CommCameraFileUtil.SearchFile(startTime,endTime,logicChannel);
				if(file!=null)
				{
					Log.d("vedio", "start file player");
					CameraUtil.startVideoFileStream(logicChannel, 0,0,file.getAbsolutePath(),null);
				}
				
								
				//设置上传服务器IP
				ParamsBiz.setUpdateIP(ip);
				//设置上传服务器端口
				ParamsBiz.setUpdatePort(String.valueOf(tcpPort));				

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9202:   //远程录像回放控制
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//控制指令
				int command = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//快进或快退倍数
				int playBackRatio = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//快速拖动位置
				byte[] fastPoistionbyte = ParseUtil.byteTobyte(data, num, 6);
				String fastPoistion = ParseUtil.bcd2Str(fastPoistionbyte);
				num += 6;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9205:   //查询资源列表
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//开始时间
				byte[] startTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String startTime = ParseUtil.bcd2Str(startTimebyte);
				num += 6;

				//结束时间
				byte[] endTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String endTime = ParseUtil.bcd2Str(endTimebyte);
				num += 6;

				//报警表示(暂时不处理报警)
				byte[] alarmbyte = ParseUtil.byteTobyte(data, num, 8);
				num += 8;

				//音视频资源类型
				int mediaType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

				//码流类型
				int streamType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

				//存储类型
				int memoryType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

				Log.d("vedio", "start file list"+startTime+"endtime"+endTime+"mediaType:"+mediaType+"streamType:"+streamType);

				//获取资源文件并上传
				CommCameraFileUtil.screenVideoFile1078(startTime, endTime, logicChannel,seq);

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9206:   //文件上传指令
			try {
				int num = 0;
				//服务器IP地址长度
				int ipLength = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//服务器IP地址
				byte[] ipbyte = ParseUtil.byteTobyte(data, num, ipLength);
				num += ipLength;
				String ip = ParseUtil.byteToString(ipbyte, 0, ipbyte.length);
				//ftp服务器端口
				int port = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//用户名长度
				int userLength = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//用户名
				byte[] usernamebyte = ParseUtil.byteTobyte(data, num, userLength);
				num += userLength;
				String username = ParseUtil.byteToString(usernamebyte, 0, usernamebyte.length);
				//用户密码长度
				int passLength = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//密码
				byte[] passWordbyte = ParseUtil.byteTobyte(data, num, passLength);
				num += passLength;
				String passWord = ParseUtil.byteToString(passWordbyte, 0, passWordbyte.length);
				//文件上传路径长度
				int pootLength = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//文件路径
				byte[] pootbyte = ParseUtil.byteTobyte(data, num, pootLength);
				num += pootLength;
				String poot = ParseUtil.byteToString(pootbyte, 0, pootbyte.length);
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//开始时间
				byte[] startTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String startTime = ParseUtil.bcd2Str(startTimebyte);
				num += 6;
				//结束时间
				byte[] endTimebyte = ParseUtil.byteTobyte(data, num, 6);
				String endTime = ParseUtil.bcd2Str(endTimebyte);
				num += 6;
				//报警表示(暂时不处理报警)
				byte[] alarmbyte = ParseUtil.byteTobyte(data, num, 8);
				num += 8;
				//音视频资源类型
				int mediaType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//码流类型
				int streamType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//存储类型
				int memoryType = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//执行条件
				int taskOp = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9207:   //文件上传控制
			try {
				int num = 0;
				//流水号
				int seqNumber = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//上传控制	UINT80：暂停1：继续2：取消
				int control = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9301:   //云台旋转
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;
				//速度
				int speed = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;
			case 0x9003: //获取音频属性
				VideoInfo info=new VideoInfo();
				info.setAudioCodec(Constants.CAREYE_ACODE_AAC_1078);
				info.setChannels(1);
				info.setSamplerate(Constants.CAREYE_AUDIO_SAMPLE_RATE_1078);
				info.setSampleBits(Constants.CAREYE_AUDIO_SAMPLE_BITS_1078);
				info.setEnableflag(1);
				info.setVediocodec(Constants.CAREYE_VCODE_H264_1078);
				info.setAudiovhannels(1);
				info.setVediovhannnels(4);
				byte[] cameradata = CommEncoder.getAudiovideoAttributeUpload(info);
				CommCenterUsers.witeMsg(cameradata,1);
				break;
			case 0x9302:   //云台调整焦距
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9303:   //云台调整光圈控制
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9304:   //云台调整雨刷
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9305:   //红外补光控制
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		case 0x9306:   //变倍控制
			try {
				int num = 0;
				//逻辑通道号
				int logicChannel = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 2)),16);
				num += 2;
				//方向
				int direction = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, num, 1)),16);
				num += 1;

			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

}
