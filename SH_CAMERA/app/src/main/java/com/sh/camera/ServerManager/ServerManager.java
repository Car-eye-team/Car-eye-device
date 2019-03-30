/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.ServerManager;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.widget.EditText;

import com.sh.camera.util.Constants;

import com.sh.camera.SetActivity;
import com.sh.camera.service.MainService;


/**    
 *     
 * 项目名称：SH-camera   
 * 类名称：SH-camera   
 * 类描述：摄像头工具类    
 * 创建人：Administrator    
 * 创建时间：2016年10月12日 下午2:35:47    
 * 修改人：Administrator    
 * 修改时间：2016年10月12日 下午2:35:47    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class ServerManager {

	private static final String tag = "ServerManger.";
	private static ServerManager instance;
	private static SharedPreferences sp;
	private static SharedPreferences.Editor sped;

	/**
	 * 获取实例
	 * @return
	 */
	public static ServerManager getInstance() {
		if (instance == null) {
			sp = MainService.getInstance().getSharedPreferences("fcoltest", MainService.getInstance().MODE_PRIVATE);

			instance = new ServerManager();
		}
		return instance;
	}
	public String getIp() {
		String ip = sp.getString(Constants.ip,Constants.SERVER_IP);
		return ip;
	}
	public void SetIP(String ip)
	{
		sped = sp.edit();
		sped.putString(Constants.ip, ip);
		sped.commit();
	}
	public void SetPort(String port)
	{
		sped = sp.edit();
		sped.putString(Constants.port, port);
		sped.commit();
	}
	public String getAddport() {
		String addport = sp.getString(Constants.addPort,Constants.SERVER_ADDPORT);
		return addport;
	}

	public String getPort() {
		String port = sp.getString(Constants.port,Constants.SERVER_PORT);
		return port;
	}
	//EditText et_ptserviceip ,et_ptserviceport;
	public String getServiceIp() {
		String port = sp.getString(Constants.PTSERVICE_IP,Constants.PT_SERVER_IP);
		return port;
	}
	public String getServicePort() {
		String port = sp.getString(Constants.PTSERVICE_PORT,Constants.PT_SERVER_PORT);
		return port;
	}
	//EditText et_ptserviceip ,et_ptserviceport;

	public String getStreamname() {
		String streamname = sp.getString(Constants.name,Constants.STREAM_NAME);
		return streamname;
	}
	
	public int getFramerate(){
		int framerate = Integer.parseInt(sp.getString(Constants.fps,String.valueOf(Constants.FRAMERATE)));
		return framerate;
	}

	public int getMode(){
		int mode = sp.getInt(Constants.mode, SetActivity.rgids[0]);
		return mode;
	}

	/**
	 * 取出的值会作为角标去mainService里camera数组取出camera对象，
	 * 由于camera数组保存的对象都是后置摄像头，按角标顺序保存，
	 * 所以这里如果取到1，去camera数组取到的值可以正常取到cameraId为2的camera对象。
	 * @return
	 */
	public String getRule() {
	    //这里注释是因为这边获取的是在设置界面所设置的双路和四路的配置
        // ，但是相机组件是固定那么多个的，所以设置这些是无效的。
//		String rule = sp.getString(Constants.rule, "01");
		StringBuilder stringBuilder=new StringBuilder();
		int cameraNum=getMaxNumCamera();
		for (int i=0;i<cameraNum;i++){
			stringBuilder.append(i+"");
		}
		return stringBuilder.toString();
	}
	/**RTSP  RTMP*/
	public int getprotocol() {
		int rule = sp.getInt(Constants.protocol_type, Constants.protocol);
		return rule;
	}

	public void setprotocol(int protocol) {
		sped = sp.edit();
		sped.putInt(Constants.protocol_type, protocol);
		sped.commit();
	}

	/**
	 * 获取可用的最大摄像头数量，需要去掉前置的摄像头，因为前后摄像头无法同时打开
	 * @return
	 */
	public static int getMaxNumCamera() {
		int cameraNum= Camera.getNumberOfCameras();
		if (cameraNum>1){
			cameraNum=cameraNum-1;
		}
		return cameraNum;
	}

}
