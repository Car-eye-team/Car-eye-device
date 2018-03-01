/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.jsr.sdk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import org.push.hw.NV21Convertor;

import com.sh.camera.SetActivity;
import com.sh.camera.codec.MediaCodecManager;
import com.sh.camera.service.MainService;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ExceptionUtil;

import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：AvcEncoder    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年11月3日 下午2:19:06    
 * 修改人：Administrator    
 * 修改时间：2016年11月3日 下午2:19:06    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class UsbEncoder {  

	public static MediaCodec mediaCodec;  
	public static BufferedOutputStream outputStream;  
	public static NV21Convertor mConvertor;

	public static void initEncoder() {   
		File f = new File(Constants.CAMERA_FILE_PATH+(2)+"-"+new Date().getTime()+".h264");  
		try {  
			outputStream = new BufferedOutputStream(new FileOutputStream(f));  
			Log.i("AvcEncoder", "outputStream initialized");  
		} catch (Exception e){   
			e.printStackTrace();  
		}  

		try {
			int width = Constants.UPLOAD_VIDEO_WIDTH;
			int height = Constants.UPLOAD_VIDEO_HEIGHT;
			SharedPreferences sp = MainService.getInstance().getSharedPreferences("fcoltest", MainService.getInstance().MODE_PRIVATE);
			int framerate = Integer.parseInt(sp.getString(Constants.fps,String.valueOf(Constants.FRAMERATE)));
			int bitrate = 2 * width * height * framerate / 10;
			/*EncoderDebugger debugger = EncoderDebugger.debug(MainService.getInstance(),width, height);
			mConvertor = debugger.getNV21Convertor();
			mediaCodec = MediaCodec.createByCodecName(debugger.getEncoderName());*/
			MediaFormat mediaFormat;
			mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
			mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
			mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);   
			mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
			mediaCodec = MediaCodec.createEncoderByType("video/avc");  
			mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
			mediaCodec.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			AppLog.d("", ExceptionUtil.getInfo(e));
		} 
	}  

	private static void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height)   {        
		System.arraycopy(yv12bytes, 0, i420bytes, 0, width*height);  
		System.arraycopy(yv12bytes, width*height+width*height/4, i420bytes, width*height, width*height/4);  
		System.arraycopy(yv12bytes, width*height, i420bytes, width*height+width*height/4, width*height/4);    
	}

	private static void NV21ToNV12(byte[] nv21,byte[] nv12,int width,int height){  
		if(nv21 == null || nv12 == null)return;  
		int framesize = width*height;  
		int i = 0,j = 0;  
		System.arraycopy(nv21, 0, nv12, 0, framesize);  
		for(i = 0; i < framesize; i++){  
			nv12[i] = nv21[i];  
		}  
		for (j = 0; j < framesize/2; j+=2)  {  
			nv12[framesize + j-1] = nv21[j+framesize];  
		}  
		for (j = 0; j < framesize/2; j+=2)  {  
			nv12[framesize + j] = nv21[j+framesize-1];  
		}  
	}  

	public static void close() {  
		try {  
			mediaCodec.stop();  
			mediaCodec.release();  
			outputStream.flush();  
			outputStream.close();  
		} catch (Exception e){   
			e.printStackTrace();  
		}  
	}  

	static byte[] mPpsSps = null;
	static byte[] dst = null;
	public static void saveVideoFile(byte[] input){

		byte[] yuv420sp = new byte[640*480*3/2];  
		NV21ToNV12(input,yuv420sp,640,480); 
		input = yuv420sp;

		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();  
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();

		dst = new byte[input.length];		                     
		dst = input;

		try {
			int bufferIndex = mediaCodec.dequeueInputBuffer(5000000);
			if (bufferIndex >= 0) {
				inputBuffers[bufferIndex].clear();
				MediaCodecManager.mConvertor.convert(dst, inputBuffers[bufferIndex]);
				mediaCodec.queueInputBuffer(bufferIndex, 0, inputBuffers[bufferIndex].position(), System.nanoTime() / 1000, 0);
				long currentTime = System.currentTimeMillis();
				MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
				int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				while (outputBufferIndex >= 0) {
					ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
					byte[] outData = new byte[bufferInfo.size];
					outputBuffer.get(outData);
					//记录pps和sps
					int type=outData[4]&0x07;
					if(type==7||type==8){
						mPpsSps = outData;
					}else if(type==5){
						//在关键帧前面加上pps和sps数据
						byte[] iframeData = new byte[mPpsSps.length + outData.length];
						System.arraycopy(mPpsSps, 0, iframeData, 0, mPpsSps.length);
						System.arraycopy(outData, 0, iframeData, mPpsSps.length, outData.length);
						outData = iframeData;
					}
					//MainService.mEasyPusher.SendBuffer(outData, bufferInfo.size, currentTime, 0, MainService.StreamIndex[1]);
					outputStream.write(outData, 0, outData.length);  
					AppLog.i("AvcEncoder", outData.length + " bytes written");  

					mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
					outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
				}
			} else {
				Log.e("puser", "No buffer available !");
			}
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.i("AvcEncoder", ExceptionUtil.getInfo(e)); 
		} finally {
		}
	}  
	
	

}
