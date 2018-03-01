/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.CamerManagePrivate;
import java.io.File;
import java.util.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.sh.camera.R;

import com.sh.camera.util.Constants;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：MediaCodecManager    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月18日 上午11:56:29    
 * 修改人：Administrator    
 * 修改时间：2016年10月18日 上午11:56:29    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class CameraManagerPrivate {

	private static final String TAG = "CameragerPrivate";
	private static CameraManagerPrivate instance;

	public static CameraManagerPrivate getInstance() {
		if (instance == null) {
			//mMediaCodec = new MediaCodec[Constants.MAX_NUM_OF_CAMERAS];			
			
			instance = new CameraManagerPrivate();
		}
		return instance;
	}
		

	 
}
	
	
	


