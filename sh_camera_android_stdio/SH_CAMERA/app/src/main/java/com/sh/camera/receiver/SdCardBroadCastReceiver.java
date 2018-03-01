/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sh.camera.service.MainService;
/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：SdCardBroadCastReceiver    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月25日 下午2:51:33    
 * 修改人：Administrator    
 * 修改时间：2016年10月25日 下午2:51:33    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class SdCardBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		String action = intent.getAction();  
        if(action.equals(Intent.ACTION_MEDIA_EJECT)){         	
        	MainService.getInstance().stopRecoders_SD_ERR();
        	Log.d("DiskManager" , "DiskManager" + "SD card unmount"); 
        	System.out.println("SD card unmount");
        	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(MainService.isrun){
        		synchronized (MainService.disk)
            	{
            		MainService.disk.RemountDisks();     
            	}
        	}       	
        	
        	
        }else if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){ 
        	
        	System.out.println("SD card mounted");
        	Log.d("DiskManager", "SD card mounted"); 
        	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	MainService.getInstance().startRecoders_SD_ERR();
        	if(MainService.isrun){
	        	synchronized (MainService.disk)
	        	{
	        		MainService.disk.RemountDisks();
	        	}
        	}
        }  
		
	}

}
