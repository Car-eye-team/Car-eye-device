/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package org.push.push;



import java.nio.ByteBuffer;

import com.sh.camera.service.MainService;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.content.Context;
public class Pusher {

	static {
		System.loadLibrary("stream");
		System.loadLibrary("rtmp");
		System.loadLibrary("rtp");
		
	}
	/**
	 * 初始化
	 * @param serverIP   流媒体服务器IP
	 * @param serverPort 流媒体服务器端口
	 * @param streamName 流媒体文件名
	 * @param fps
	 * @param format
	 * @return
	 */

	/* 特别说明：本SDK商业用途，请与Car-eye 开源团队联系  */

	/* rtsp interface*/
	private Handler handle =null;	
	//RTSP 推流方式
	public native int    CarEyeInitNetWorkRTSP(Context context,String serverIP, String serverPort, String streamName, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);
	public native int 	CarEyePusherIsReadyRTSP(int channel);
	public native long   CarEyeSendBufferRTSP(long time, byte[] data, int lenth, int type, int channel);	
	public native int    CarEyeStopNativeFileRTSP(int channel);	
	public native int    CarEyeStartNativeFileRTSPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);
	public native void  CarEyeStopPushNetRTSP(int index);
	
	//JT1078 RTP数据包格式推流接口

	public native int    CarEyeInitNetWorkRTP(Context context,String key, String serverIP, String serverPort, String streamName, int logchannel, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);
	public native int 	CarEyePusherIsReadyRTP(int channel);
	public native long   CarEyeSendBufferRTP(long time, byte[] data, int lenth, int type, int channel);	
	public native int    CarEyeStopNativeFileRTP(int channel);	
	public native int    CarEyeStartNativeFileRTPEX(Context context,String key, String serverIP, String serverPort, String streamName, int logchannel,  String fileName,int start, int end);
	public native void   CarEyeStopPushNetRTP(int index);

	// result： 0 文件传输结束  , 传输出错
	/* rtmp interface*/
	public native long    CarEyeInitNetWorkRTMP(Context context,String key,String serverIP, String serverPort, String streamName, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);
	public native int    CarEyePusherIsReadyRTMP(long handle);
	public native long   CarEyeSendBufferRTMP(long time, byte[] data, int lenth, int type, long handle);
	public native int    CarEyeStopNativeFileRTMP(long handle);
	public native int    CarEyeStartNativeFileRTMPEX(Context context, String key, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);
	public native void   CarEyeStopPushNetRTMP(long index);
	

	public void  CarEyeCallBack(int channel, int Result){		
		
		Log.e("puser", "exit send file!");	
		if(handle != null){
			handle.sendMessage(handle.obtainMessage(1006));
		}else{
		}		
		Intent intent = new Intent("com.dss.camera.ACTION_END_VIDEO_PLAYBACK");
		intent.putExtra("EXTRA_ID", channel);
		MainService.getInstance().sendBroadcast(intent);
	}	
	/**
	 * 发送H264编码格式
	 * @param data
	 * @param timestamp
	 * @param type
	 * @param index
	 * @return
	 */

	/**
	 * 停止发送
	 * @param index
	 */

	public  long SendBuffer_org(final byte[] data,final int length, final long timestamp, final int type, final long handle , int protocol)
	{
		long ret;
		//Log.e("puser", "timestamp:"+timestamp+"length:"+length);
		if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
			ret = CarEyeSendBufferRTSP(timestamp, data, length, type, (int)handle);
		}else if(Constants.CAREYE_RTMP_PROTOCOL==protocol)
		{
			ret =  CarEyeSendBufferRTMP(timestamp, data,length,type, handle);
		}else
		{
			ret =  CarEyeSendBufferRTP(timestamp, data,length,type, (int)handle);
		}
		return ret;
		
	}	
	public  void stopPush(final long  index, final int protocol)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
					CarEyeStopPushNetRTSP((int)index);
				}else  if(Constants.CAREYE_RTMP_PROTOCOL==protocol)
				{
					CarEyeStopPushNetRTMP(index);
				}else
				{
					CarEyeStopPushNetRTP((int)index);
				}
			}
		}).start();
	}
	/**
	 * 停止所有
	 */
	public int  startfilestream( final String serverIP, final String serverPort, final String streamName, final int logchannel, final String fileName,final int splaysec,final int eplaysec,final Handler handler, int protocol){

		//StartNativeFileRTSP(serverIP,serverPort,streamName, fileName);
		handle = handler;
		int channel;
		if(Constants.CAREYE_RTSP_PROTOCOL == protocol ) {
			 channel = CarEyeStartNativeFileRTSPEX(MainService.application, serverIP, serverPort, streamName, fileName, splaysec, eplaysec);
		}else if(Constants.CAREYE_RTMP_PROTOCOL==protocol)
		{
			 channel =  CarEyeStartNativeFileRTMPEX(MainService.application, Constants.Key, serverIP,serverPort,streamName, fileName,splaysec,eplaysec);

		}else
		{
			channel= CarEyeStartNativeFileRTPEX(MainService.application,Constants.rtpKey ,  serverIP,  serverPort,  streamName,  logchannel,   fileName, splaysec,  eplaysec);
			
		}
		return channel;
	}

	public void startfilestream(final String serverIP, final String serverPort, final String streamName, final String filePath,int protocol){

		if(Constants.CAREYE_RTSP_PROTOCOL == protocol )
		{
			CarEyeStartNativeFileRTSPEX(MainService.application,serverIP,serverPort,streamName, filePath,0,0);
		}else
		{
			CarEyeStartNativeFileRTMPEX(MainService.application,Constants.Key, serverIP,serverPort,streamName, filePath,0,0);
		}
	}
}

