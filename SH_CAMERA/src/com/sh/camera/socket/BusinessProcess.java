/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.coder.CommDecoder;
import com.sh.camera.socket.db.DataMsgDao;
import com.sh.camera.socket.model.DSCommData;
import com.sh.camera.socket.model.PlatformResponse;
import com.sh.camera.socket.model.TerminalRegist;
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
			process(dsCommData.getMsgid(),dsCommData.getData(), context);
		}

	}

	/**
	 * 业务处理
	 * @param msgid 消息ID
	 * @param data 消息数据包
	 * @param context 上下文对象
	 */
	public synchronized static void process(int msgid,byte[] data, final Context context){
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
					ServerManager.getInstance().SetIP(ip);
					ServerManager.getInstance().SetPort(""+port);
					ServerManager.getInstance().setprotocol(protocolType);
					Log.d("vedio", "protocolType" + protocolType + "  ip" + ip + "  port:" + port + "  protocol" + ServerManager.getInstance().getprotocol());
				}
				if(type == 0){
					CameraUtil.startVideoUpload((id-1));
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
				//兼容老的car-eye-server 和 新的两个平台
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
					ServerManager.getInstance().SetIP(ip);
					ServerManager.getInstance().SetPort(""+port);
					ServerManager.getInstance().setprotocol(protocolType);

				}
				/*Intent intent = new Intent("com.dss.launcher.ACTION_VIDEO_PLAYBACK");
				intent.putExtra("EXTRA_ID", id);
				intent.putExtra("EXTRA_TYPE", type);
				intent.putExtra("EXTRA_STIME", stime);
				intent.putExtra("EXTRA_ETIME", etime);
				context.sendBroadcast(intent);*/
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
		default:
			break;
		}
	}

}
