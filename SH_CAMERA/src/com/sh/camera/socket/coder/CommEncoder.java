/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.coder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.sh.camera.model.GPSLocationInfo;
import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.model.VideoInfo;
import com.sh.camera.socket.utils.ConstantsAlarm;
import com.sh.camera.socket.utils.ConstantsState;
import com.sh.camera.socket.utils.DistanceUtil;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.DateUtil;
import com.sh.camera.util.ExceptionUtil;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：DSEncoder    
 * 类描述：通讯协议编码    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午3:03:19    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午3:03:19    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class CommEncoder {

	private static final String TAG = "CommEncoder";
	private static int seq = 1;

	/**
	 * 获得发送序列号
	 * @return
	 */
	private static int getSerialId(){
		if(seq > 65530){
			seq = 0;
		}
		seq++;
		return seq;
	}

	/**
	 * 终端注册
	 * @param province 省域ID
	 * @param city 市县域ID
	 * @param mid 制造商ID
	 * @param terminalId 终端ID
	 * @param carnumber 车牌号
	 * @param platecolor 车牌颜色
	 * @return
	 */
	public static byte[] encoderTerminalRegistration(int province,int city,String mid,String terminalId,String carnumber,int platecolor){
		try {
			byte[] carnumberbyte = ParseUtil.stringToByte(carnumber);
			int len = carnumberbyte.length;
			byte[] body = new byte[37+len];
			int dstPos = 0;
			//省域ID
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(province,2)), 0, body, dstPos, 2);
			dstPos+=2;

			//市县域ID
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(city, 2)), 0, body, dstPos, 2);
			dstPos+=2;

			//制造商ID
			System.arraycopy(ParseUtil.getByteToByte(mid, 5), 0, body, dstPos, 5);
			dstPos+=5;

			//终端型号
			String ddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()); // 获取系统当前时间
			System.arraycopy(ParseUtil.getByteToByte(ddHHmmss, 20), 0, body, dstPos, 20);
			dstPos+=20;

			//终端id
			System.arraycopy(ParseUtil.getByteToByte(terminalId, 7), 0, body, dstPos, 7);
			dstPos+=7;

			//车牌颜色
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(platecolor,1)), 0, body, dstPos, 1);
			dstPos+=1;
			//车牌
			System.arraycopy(carnumberbyte, 0, body, dstPos, len);
			byte[] bytes = getProtocol808(body, 0x0100);
			AppLog.e(TAG, "终端注册："+ParseUtil.parseByte2HexStr(bytes));

			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 终端注销
	 * @return
	 */
	public static byte[] encoderTerminalLogout(){

		try {
			byte[] head = ParseUtil.parseHexStr2Byte("");		
			byte[] bytes = getProtocol808(head, 0x0003);
			return bytes;	
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	
	}

	/**
	 * 终端鉴权
	 * @return
	 */
	public static byte[] getAuthentication(){
		try {
			String authcode =  SPutil.getComm().getString("auth_code", "123456");
			int len = ParseUtil.stringToByte(authcode).length;
			byte[] head = new byte[len];	
			//终端手机号
			System.arraycopy(ParseUtil.stringToByte(authcode), 0, head, 0,len);
			byte[] bytes = getProtocol808(head, 0x0102);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 终端通用应答
	 * @param serialCode	应答流水号		UINT16	对应的中心消息的流水号
	 * @param msgid 		应答ID		UINT16	对应的中心消息的ID
	 * @param result 		结果			UINT8	0:成功/确认	1:失败	2:消息有误
	 * @return
	 */
	public static byte[] getTerminalGeneralResponse(int reseq, int remsgid, int result){
		try {
			byte[] head = new byte[5];	
			int dstPos = 0;
			//应答流水号
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(reseq,2)), 0, head, dstPos, 2);
			dstPos+=2;
			//应答id
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(remsgid,2)), 0, head, dstPos, 2);
			dstPos+=2;
			//结果
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(result,1)), 0, head, dstPos, 1);

			byte[] bytes = getProtocol808(head, 0x0001);
			AppLog.i(TAG, "终端通用应答："+ParseUtil.parseByte2HexStr(bytes));
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 心跳
	 * @return
	 */
	public static byte[] getHeartbeat(){
		try {
			byte[] head = ParseUtil.parseHexStr2Byte("");		
			byte[] bytes = getProtocol808(head, 0x0002);
			return bytes;	
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 位置信息汇报
	 * @param locationInfo
	 * @return
	 */
	public static byte[] getLocationInformation(){
		try {
			//获取位置信息
			GPSLocationInfo locationInfo = ShCommService.getInstance().getLocInfo();
			byte[] body = new byte[34];	
			int dstPos = 0;
			//报警标志
			System.arraycopy(ConstantsAlarm.getAlarmStateByte(), 0, body, dstPos, 4);
			dstPos+=4;

			//状态位
			System.arraycopy(ConstantsState.getStateByte(), 0, body, dstPos, 4);
			dstPos+=4;

			//纬度
			int lat = 0;
			if(locationInfo!=null){
				if(locationInfo.getLat() != null){
					lat = locationInfo.getLat();
				}
			}

			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(lat,4)), 0, body, dstPos, 4);
			dstPos+=4;

			//经度
			int lng = 0;
			if(locationInfo!=null){
				if(locationInfo.getLng() != null){
					lng = locationInfo.getLng();
				}
			}
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(lng,4)), 0, body, dstPos, 4);
			dstPos+=4;

			//高度
			int altitude = 0;
			if(locationInfo!=null){
				if(locationInfo.getAltitude() != null){
					altitude = locationInfo.getAltitude();
				}
			}

			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(altitude,2)), 0, body, dstPos, 2);
			dstPos+=2;

			//速度
			int speed = 0;
			if(locationInfo!=null){
				if(locationInfo.getSpeed() != null){
					speed = locationInfo.getSpeed();
				}
			}

			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(speed,2)), 0, body, dstPos, 2);
			dstPos+=2;

			//方向
			int direction = 0;
			if(locationInfo!=null){
				if(locationInfo.getDirection() != null){
					direction = locationInfo.getDirection();
				}
			}

			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(direction,2)), 0, body, dstPos, 2);
			dstPos+=2;

			String systemdate = new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime()); // 获取系统当前时间
			if(locationInfo!=null){
				if(locationInfo.getTime() != null && !"null".equals(locationInfo.getTime())){
					systemdate = DateUtil.gpsTimeToTime(locationInfo.getTime(), "yyMMddHHmmss");	
				}
			}
			//时间
			System.arraycopy(ParseUtil.str2Bcd(systemdate), 0, body, dstPos, 6);
			dstPos+=6;

			//附加信息ID
			System.arraycopy(ParseUtil.parseHexStr2Byte("01"), 0, body, dstPos, 1);
			dstPos+=1;

			//附加信息长度
			System.arraycopy(ParseUtil.parseHexStr2Byte("04"), 0, body, dstPos, 1);
			dstPos+=1;

			try {
				int mileage = (int)(DistanceUtil.getMileage()/100);

				//里程
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(mileage,4)), 0, body, dstPos, 4);
			} catch (Exception e) {
				AppLog.e(ExceptionUtil.getInfo(e), e);
				e.printStackTrace();
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(0,4)), 0, body, dstPos, 4);
			}
			dstPos+=4;

			byte[] bytes = getProtocol808(body, 0x0200);
			return bytes;	
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 视频回放结束
	 * @param id
	 * @return
	 */
	public static byte[] getEndVideoPlayBack(int id){
		try {
			int dstPos = 0;
			byte[] head = new byte[1];	
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(id,1)), 0, head, dstPos, 1);

			byte[] bytes = getProtocol808(head, 0x5115);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] getVideoPlayBackList(byte[] data){
		try {
			byte[] bytes = getProtocol808(data, 0x5114);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 按照国标808协议编码
	 * @param DataByte
	 * @param msgid 消息ID
	 * @return
	 */
	public static byte[] getProtocol808(byte[] dataByte,int msgid){

		String phone = SPutil.getComm().getString("comm_terminal", "");

		int uDataLen = 0;

		//消息头组装
		byte[] msghead = getMsghead808(phone, msgid, dataByte);

		//校验码
		byte[] checkcode = getCheckCode(msghead, dataByte);

		//消息头转义
		msghead = tropeYXByte(ParseUtil.parseByte2HexStr(msghead));

		//校验码转义
		checkcode = tropeYXByte(ParseUtil.parseByte2HexStr(ParseUtil.byteOrbyte(checkcode)));

		if(dataByte!=null){
			//消息体转义
			dataByte = tropeYXByte(ParseUtil.parseByte2HexStr(dataByte));
			uDataLen = dataByte.length;
		}

		byte[] bodybyte =  new byte[msghead.length+uDataLen+checkcode.length+2];
		try {
			int dstPos = 0;
			//--起始位
			System.arraycopy(ParseUtil.parseHexStr2Byte("7e"), 0, bodybyte, dstPos, 1);																																																																																																																																							
			dstPos +=1;

			//--消息头
			System.arraycopy(msghead, 0, bodybyte, dstPos, msghead.length);
			dstPos +=msghead.length;

			//---消息体
			if(dataByte!=null){
				System.arraycopy(dataByte, 0, bodybyte, dstPos, dataByte.length);
				dstPos +=dataByte.length;
			}

			//校验码
			System.arraycopy(checkcode, 0, bodybyte, dstPos, checkcode.length);
			dstPos +=checkcode.length;

			//---结束位
			System.arraycopy(ParseUtil.parseHexStr2Byte("7e"), 0, bodybyte, dstPos, 1);

			System.out.println(ParseUtil.parseByte2HexStr(bodybyte));
			return bodybyte;

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 组装消息头
	 * @param phone
	 * @param msgid
	 * @param uDataByte
	 * @return
	 */
	public static byte[] getMsghead808(String phone,int msgid,byte[] uDataByte){

		int uDataLen = 0;
		if(uDataByte!=null){
			uDataLen = uDataByte.length;
		}

		//消息头
		byte[] msghead = new byte[12];
		int headnum = 0;
		//消息ID
		System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(msgid,2)), 0, msghead, headnum, 2);
		headnum +=2;
		//消息体属性
		System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(uDataLen, 2)), 0, msghead, headnum, 2);
		headnum +=2;

		//终端手机号
		System.arraycopy(ParseUtil.str2Bcd(phone), 0, msghead, headnum,6);
		headnum +=6; 

		//消息流水号
		byte[] serialId = ParseUtil.sortToByte(ParseUtil.int2Bytes(getSerialId(),2));
		System.arraycopy(serialId, 0, msghead, headnum, 2);
		return msghead;
	}

	/**
	 * 组装效验码
	 * @param msghead
	 * @param dataByte
	 * @return
	 */
	public static byte[] getCheckCode(byte[] msghead,byte[] dataByte){
		int dataLen = 0;
		if(dataByte != null){
			dataLen = dataByte.length;
		}
		//---校验码
		byte[] checkcode = new byte[msghead.length+dataLen];
		int num = 0;
		System.arraycopy(msghead, 0, checkcode, num, msghead.length);
		num+=msghead.length;
		if(dataByte!=null){
			System.arraycopy(dataByte, 0, checkcode, num, dataLen);
		}
		return checkcode;
	}
	
	
	/**
	 * 终端上传音视频资源列表
	 * @param id
	 * @return
	 */
	public static byte[] getAudioVideoResourceList(int seq,List<VideoInfo> list){
		try {
			int dstPos = 0;
			int len = 0;
			int total = 0;
			if(list != null){
				total = list.size();
				len = total*28;
			}
			byte[] body = new byte[6+len];	
			//流水号
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(seq,2)), 0, body, dstPos, 2);
			dstPos+=2;
			//资源总数
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(total,4)), 0, body, dstPos, 4);
			dstPos+=4;
			//资源列表
			for (VideoInfo videoItems : list) {
				//逻辑通道号
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoItems.getLogicChannel(),1)), 0, body, dstPos, 1);
				dstPos+=1;
				//开始时间
				byte[] startTimebyte = ParseUtil.str2Bcd(DateUtil.dateToNumber(videoItems.getStartTime()));
				System.arraycopy(startTimebyte, 0, body, dstPos, 6);
				dstPos+=6;
				//结束时间
				byte[] endTimebyte = ParseUtil.str2Bcd(DateUtil.dateToNumber(videoItems.getEndTime()));
				System.arraycopy(endTimebyte, 0, body, dstPos, 6);
				dstPos+=6;
				//报警标志(暂时不处理报警)
				String alarmBit0 = "00000000";
				String alarmBit1 = "00000000";
				String alarmBit2 = "00000000";
				String alarmBit3 = "00000000";
				String alarmBit4 = "00000000";
				String alarmBit5 = "00000000";
				String alarmBit6 = "00000000";
				String alarmBit7 = "00000000";
				byte[] alarmbyte = new byte[8];
				alarmbyte[0] = ParseUtil.BitToByte(alarmBit0);
				alarmbyte[1] = ParseUtil.BitToByte(alarmBit1);
				alarmbyte[2] = ParseUtil.BitToByte(alarmBit2);
				alarmbyte[3] = ParseUtil.BitToByte(alarmBit3);
				alarmbyte[4] = ParseUtil.BitToByte(alarmBit4);
				alarmbyte[5] = ParseUtil.BitToByte(alarmBit5);
				alarmbyte[6] = ParseUtil.BitToByte(alarmBit6);
				alarmbyte[7] = ParseUtil.BitToByte(alarmBit7);
				System.arraycopy(alarmbyte, 0, body, dstPos,8);
				dstPos+=8;
				
				//音视频资源类型
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoItems.getMediaType(),1)), 0, body, dstPos, 1);
				dstPos+=1;
				//码流类型
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoItems.getStreamType(),1)), 0, body, dstPos, 1);
				dstPos+=1;
				//存储类型
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoItems.getMemoryType(),1)), 0, body, dstPos, 1);
				dstPos+=1;
				//文件大小
				System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoItems.getSize(),4)), 0, body, dstPos, 4);
				dstPos+=4;
			}
			
			byte[] bytes = getProtocol808(body, 0x1205);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 终端上传音视频属性0x1003
	 * @param videoInfo
	 * @return
	 */
	public static byte[] getAudiovideoAttributeUpload(VideoInfo videoInfo){
		try {
			int dstPos = 0;
			byte[] body = new byte[10];	
			//音频编码格式
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getAudioCodec(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//输入音频通道数
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getChannels(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//输入音频采样率
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getSamplerate(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//音频采样位数
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getSampleBits(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//音频帧长度
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getSampleLength(),2)), 0, body, dstPos, 2);
			dstPos+=2;
			//是否支持音频输出
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getEnableflag(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//视频编码格式
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getVediocodec(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//终端支持最大音频通道数量
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getAudiovhannels(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			//终端支持的最大视频通道数量
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getVediovhannnels(),1)), 0, body, dstPos, 1);
			dstPos+=1;
			
			byte[] bytes = getProtocol808(body, 0x1003);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 终端上传乘客流量0X1005
	 * @param videoInfo
	 * @return
	 */
	public static byte[] getPassengerFlowUpload(VideoInfo videoInfo){
		try {
			int dstPos = 0;
			byte[] body = new byte[16];	
			//开始时间
			System.arraycopy(ParseUtil.str2Bcd(videoInfo.getStartTime()), 0, body, dstPos, 6);
			dstPos+=6;
			//结束时间
			System.arraycopy(ParseUtil.str2Bcd(videoInfo.getEndTime()), 0, body, dstPos, 6);
			dstPos+=6;
			//上车人数
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getAboard(),2)), 0, body, dstPos, 2);
			dstPos+=2;
			//下车人数
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(videoInfo.getLeave(),2)), 0, body, dstPos, 2);
			dstPos+=2;
			byte[] bytes = getProtocol808(body, 0x1005);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 文件上传结束
	 * @param seqNumber 对应上传的流水号
	 * @param result 结果 0：成功 1：失败
	 * @return
	 */
	public static byte[] getEndFileUpload(int seqNumber,int result){
		try {
			int dstPos = 0;
			byte[] body = new byte[3];	
			//流水号
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(seqNumber,2)), 0, body, dstPos, 2);
			dstPos+=2;
			//结构
			System.arraycopy(ParseUtil.sortToByte(ParseUtil.int2Bytes(result,1)), 0, body, dstPos, 1);
			dstPos+=1;
			byte[] bytes = getProtocol808(body, 0x1206);
			return bytes;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	/**
	 *  7EH 《————》 7DH+02H；
	 *  7DH 《————》 7DH+01H；
	 * @param str 16 进制
	 * @param type
	 * @return
	 */
	public static byte[] tropeYXByte(String str){
		str = str.toLowerCase();
		byte[] result = new byte[str.length()/2];
		str = ParseUtil.replaceStr(str,"7d", "7d01");
		str = ParseUtil.replaceStr(str,"7e", "7d02");
		result = ParseUtil.parseHexStr2Byte(str);
		return result;
	}

}
