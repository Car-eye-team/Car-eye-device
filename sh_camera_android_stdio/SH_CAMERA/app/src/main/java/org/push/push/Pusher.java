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
	 private ByteBuffer directbuffer;
	    
	    
	public native int  InitNetWork(Context context,String serverIP, String serverPort, String streamName, int fps, int format);
	public native int  StopNativeFileRTSP();
	public native int  StartNativeFileRTSPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);
	
	public native void   InitWatermark(int height);
	public native byte[] addwatermarkScale(byte[] data,int length, int width, int height, int frameheight, int UploadWidth, int UploadHeight);
	public native void  UnInitWatermark();
	public native long  SendBuffer(int time, byte[] data, int lenth, int type, int index);
	
	/**
	 * 发送H264编码格式
	 * @param data
	 * @param timestamp
	 * @param type
	 * @param index
	 * @return
	 */

	// public native int SendBuffer( long timestamp, int type, int index); // 0 ��Ƶ  1 ��Ƶ
			
	/**
	 * 停止发送
	 * @param index
	 */
	public native void stopPushNet(int index);   
	
	public void  initDectBUffer(int size)
	{
		directbuffer = 	ByteBuffer.allocateDirect(size);
	}
	public  long SendBuffer_org(final byte[] data,final int length, final int timestamp, final int type, final int index)
	{
		long ret;
		Log.e("SendBuffer_org", "SendBuffer_org"+timestamp);
		ret = SendBuffer(timestamp, data,length,type,index);		
		return ret;
		
	}
	public byte[] addwatermarkScale(byte[] data, byte[] dest, int size, int width, int height,int UploadWidth, int UploadHeight )
	{
		byte[] pdest;		
		pdest = addwatermarkScale(data,size,width,height, height, UploadWidth,UploadHeight);		
		System.arraycopy( pdest, 0, dest, 0,  UploadWidth*UploadHeight*3/2);
		return data;
	}

	public  void stopPush(final int  index)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				stopPushNet(index);

			}
		}).start();

	}


	/**
	 * 停止所有
	 */
	public void stop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				stopPush(0);
				stopPush(1);
				stopPush(2);
				stopPush(3);
			}
		}).start();
	}

	/* public void initPush(final String serverIP, final String serverPort, final String streamName, final int fps, final int format){
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitNetWork(serverIP,serverPort,streamName, fps, format);
            }
        }).start();
    }*/

	public void startfilestream( final String serverIP, final String serverPort, final String streamName, final String fileName,final int splaysec,final int eplaysec,final Handler handler){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					//StartNativeFileRTSP(serverIP,serverPort,streamName, fileName);
					StartNativeFileRTSPEX(MainService.application, serverIP,serverPort,streamName, fileName,splaysec,eplaysec);
					Log.e("puser", "exit send file!");
					if(handler != null){
						handler.sendMessage(handler.obtainMessage(1006));
					}else{
					}
					Intent intent = new Intent("com.dss.camera.ACTION_END_VIDEO_PLAYBACK");
					intent.putExtra("EXTRA_ID", 0);
					MainService.getInstance().sendBroadcast(intent);
					CameraUtil.stopVideoFileStream();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void startfilestream(final String serverIP, final String serverPort, final String streamName, final String filePath){

		StartNativeFileRTSPEX(MainService.application,serverIP,serverPort,streamName, filePath,0,0);
		Log.e("puser", "exit send file!");
		CameraUtil.stopVideoFileStream();
	}
	
}

