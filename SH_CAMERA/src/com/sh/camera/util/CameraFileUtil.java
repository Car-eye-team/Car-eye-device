/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sh.camera.service.MainService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：CameraFileUtil    
 * 类描述：摄像头文件工具类    
 * 创建人：Administrator    
 * 创建时间：2016年10月12日 下午8:18:13    
 * 修改人：Administrator    
 * 修改时间：2016年10月12日 下午8:18:13    
 * 修改备注：    
 * @version 1.0  
 *     
 */


@SuppressLint("SimpleDateFormat")
public class CameraFileUtil {

	/**
	 * 筛选视频文件
	 * @param stime 开始时间
	 * @param etime 结束时间
	 * @param cameraid 摄像头ID
	 */

	public static File CreateText(String gg) throws IOException {
		File file = new File(gg);
		if (!file.exists()) {
			try {
				// 鎸夌収鎸囧畾鐨勮矾寰勫垱寤烘枃浠跺す
				file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		File dir = new File(gg);
		if (!dir.exists()) {
			try {
				// 鍦ㄦ寚瀹氱殑鏂囦欢澶逛腑鍒涘缓鏂囦欢
				dir.createNewFile();
			} catch (Exception e) {
			}
		}

		return dir;

	}
	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/";
		}
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public synchronized static void saveJpeg_snap(final int index, final byte[] data, final int width, final int height)
	{

		final Handler handler = MainService.getInstance().handler;
		new Thread(new Runnable() {
			@Override
			public void run() {	
				File picture;            	
	        try {

				File mFile = CreateText(getRootFilePath() + Constants.CAMERA_PATH);
				picture = new File(mFile, (index+1)+"-"+new Date().getTime()+".jpg");
				if(picture.exists())
				{
					picture.delete();
				}
		        picture.createNewFile();
				FileOutputStream filecon = new FileOutputStream(picture);
		            YuvImage image = new YuvImage(data,
		                    ImageFormat.NV21, width, height,
		                    null);				            
		            image.compressToJpeg(
		                    new Rect(0, 0, image.getWidth(), image.getHeight()),
		                    90, filecon);   // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流
		            filecon.close();
					if(handler != null){
						handler.sendMessage(handler.obtainMessage(1001));
					}
		        }catch (IOException e)
		        {
		        	if(handler != null){
						handler.sendMessage(handler.obtainMessage(1003));
					}
		            e.printStackTrace();		            
		        }				
			}
		}).start();
	}

}