/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.coder;

import com.sh.camera.socket.model.DSCommData;
import com.sh.camera.socket.model.PlatformResponse;
import com.sh.camera.socket.model.TerminalRegist;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

import android.content.Context;



/**    
 *     
 * 项目名称：DSS_808    
 * 类名称：Decoder    
 * 类描述：协议解码    
 * 创建人：zr    
 * 创建时间：2015-7-17 下午3:02:39    
 * 修改人：zr    
 * 修改时间：2015-7-17 下午3:02:39    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CommDecoder {

	private static final String TAG = "DSDecoder";
	
	/**
	 * 解码平台应答
	 * @param data
	 * @return
	 */
	public static PlatformResponse decoderPlatformResponse(byte[] data){

		try {
			PlatformResponse platformResponse = new PlatformResponse();
			int dstPos = 0;
			//应答流水号
			int reseq = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, dstPos, 2)),16);
			dstPos +=2;
			platformResponse.setReseq(reseq);

			//消息ID
			int remsgid = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, dstPos, 2)),16);
			dstPos +=2;
			platformResponse.setRemsgid(remsgid);

			//结果
			int result = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, dstPos, 1)),16);
			dstPos +=1;
			platformResponse.setResult(result);

			return platformResponse;

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解码终端注册应答
	 * @param data
	 * @return
	 */
	public static TerminalRegist decoderTerminalRegistResponse(byte[] data){

		try {
			TerminalRegist terminalRegist = new TerminalRegist();
			int dstPos = 0;
			//应答流水号
			int reseq = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, dstPos, 2)),16);
			dstPos +=2;
			terminalRegist.setReseq(reseq);

			//结果
			int result = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(data, dstPos, 1)),16);
			dstPos +=1;
			terminalRegist.setResult(result);

			return terminalRegist;

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 解码808协议
	 * @param bytes
	 */
	public static DSCommData decoder808Data(byte[] bytes, Context context){
		try {

			AppLog.i(TAG, "解码808协议:"+ParseUtil.parseByte2HexStr(bytes));

			//转义
			bytes = ParseUtil.yxReversal(bytes);

			boolean rsa = false; //消息体是否RSA加密 true RSA加密，false RSA不加密
			int nOff = 1;
			//消息ID
			byte[] idByte = ParseUtil.byteTobyte(bytes, nOff, 2);
			int msgid = Integer.parseInt(ParseUtil.parseByte2HexStr(idByte), 16);
			nOff+=2;
			//消息体属性
			byte[] mBodyAttr = ParseUtil.byteTobyte(bytes, nOff, 2);
			// 是否分包
			int pkgInfo = 0;
			int bodylen = 0;
			try {
				//解析消息体属性
				String bodyAttr = ParseUtil.byteTobit(mBodyAttr);
				//分包
				pkgInfo = Integer.valueOf(bodyAttr.substring(2,3),2);
				//消息体长度
				bodylen = Integer.valueOf(bodyAttr.substring(6, 16),2);

			} catch (Exception e) {
				e.printStackTrace();
			}

			nOff+=2;
			// 终端标识
			String terminal = ParseUtil.bcd2Str(ParseUtil.byteTobyte(bytes, nOff, 6)); 
			nOff+=6;
			// 消息体流水号
			int seq = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(bytes, nOff, 2)),16);
			nOff+=2;

			int pkgCount = 0;
			int pkgNum = 0;
			// 消息包封装项
			if(pkgInfo == 1){
				// 消息总包数	WORD
				pkgCount = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(bytes, nOff, 2)), 16);
				nOff+=2;
				// 包序号	WORD
				pkgNum = Integer.parseInt(ParseUtil.parseByte2HexStr(ParseUtil.byteTobyte(bytes, nOff, 2)), 16);
				nOff+=2;
			}
			//应该判断设备ID号，要是不是本设备的消息，不做任何处理//						
			//消息体
			//转义
			byte[] data = ParseUtil.byteTobyte(bytes, nOff, bodylen);

			DSCommData dsData = new DSCommData();
			dsData.setMsgid(msgid);
			dsData.setSeq(seq);
			dsData.setPkgCount(pkgCount);
			dsData.setPkgNum(pkgNum);
			dsData.setTerminal(terminal);
			dsData.setData(data);
			
			return dsData;

		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();
			return null;
		}
	}

}
