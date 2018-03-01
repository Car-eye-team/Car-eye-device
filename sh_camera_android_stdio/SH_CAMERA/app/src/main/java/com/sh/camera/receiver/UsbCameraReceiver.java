/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：UsbCameraReceiver    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月26日 上午10:44:11    
 * 修改人：Administrator    
 * 修改时间：2016年10月26日 上午10:44:11    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class UsbCameraReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int flag = intent.getFlags();
		String action = intent.getAction();  
		//一甲丙益设备专用USB摄像头断开/连接广播
		if(action.equals("android.intent.action.USB_CAMERA")){
			if (flag == 32785){
				Toast.makeText(context, "USB摄像头已断开", Toast.LENGTH_LONG).show();
			}else if(flag == 32786){
				Toast.makeText(context, "USB摄像头已连接", Toast.LENGTH_LONG).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}
			
	}

}
