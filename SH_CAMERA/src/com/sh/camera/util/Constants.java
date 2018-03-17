/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import android.content.Context;
import android.content.Intent;


/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：Constants    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年9月30日 下午2:03:55    
 * 修改人：Administrator    
 * 修改时间：2016年9月30日 下午2:03:55    
 * 修改备注：    
 * @version 1.0  
 *     
 */

public class Constants {

	//升级参数
	public static final String UPDATE_APK_TYPE = "322";
	public static final String UPDATE_APK_NAME = "DSS_CAMERA.apk";
	public static final String UPDATE_APK_AK = "zhvvc2vuz2f0zte1mtm4ndy5mzg0mjm=";//正式
	public static final String BASE_URL = "http://39.108.246.45:801";
	public static final String UPDATE_IP = "39.108.246.45";
	public static final String UPDATE_PORT = "801";
	public static final String UPDATE_URL = "/api/loadNewVersion.action";
	/**上传视频宽度*/
	public static final int UPLOAD_VIDEO_WIDTH = 640;
	/**上传视频高度*/
	public static final int UPLOAD_VIDEO_HEIGHT = 480;	
	/**录制视频宽度*/
	public static final int RECORD_VIDEO_WIDTH = 640;
	/**录制视频高度*/
	public static final int RECORD_VIDEO_HEIGHT = 480;
	//四路
	public static final int MAX_NUM_OF_CAMERAS = 4;
	/**服务器IP*/
	public static final String SERVER_IP = "120.76.235.109";
	/**handle apk 升级消息*/
	public static final int  MSG_APK_NEW = 1001;
	/**服务器端口*/
	public static final String SERVER_PORT = "10554";
	public static final String SERVER_ADDPORT = "10000";
	/**设备号*/
	public static final String STREAM_NAME = "18668171282";
	public static String devicename = "NVR";
	public static String AliveInterval = "30";
	public static String DeviceKey  ="a939cd77c7cb4af6b2bd8f2090562b91";
	public static String DeviceTag ="DEMO";
	
	public static String ip = "ip";
	public static String port = "port";
	public static String name = "name";
	public static String fps = "fps";
	public static String rule = "rule";
	public static String mode = "mode";
	public static String addPort = "add_port";	
	public static int CAREYE_VCODE_H264 = 0x1C;
	public static int CAREYE_VCODE_H265 = 0x48323635;

	public static int CAREYE_ACODE_AAC = 0x15002;
	public static int CAREYE_ACODE_G711U = 0x10006;
	public static int CAREYE_ACODE_G711A = 0x10007;
	public static int CAREYE_ACODE_G726 = 0x10007;
	/**录像时长 分钟*/
	public static final int VIDEO_TIME = 10;//10
	/**SD卡路径*/
	public static String CAMERA_FILE_PATH = "";
	public static String CAMERA_FILE_DIR = "";
	public static String SD_CARD_PATH = "";
	public static String INNER_CARD_PATH = "";
	public static String SNAP_FILE_PATH = "";
	/**SD清理界限、清理目标  100M 14950M*/
	public static final  int SD_FREE2C = 800;//清理界限、清理目标 
	/**SD清理界限、清理目标  达到极限 10M*/
	public static final  long SD_FREEJX = 200;//清理界限、清理目标达到极限 5M 
	/**是否正在清理sd内存*/
	public static  boolean isCleaning = false;
	public static  boolean AudioRecord = false;
	public static  final boolean ExtPlayer = false;
	public static  boolean GPS_SUPPORT = false;
	/**帧速率*/
	public static int FRAMERATE = 20;
	/**摄像头ID*/
	public static int[] CAMERA_ID = {0,1,5,6};

	/**录像状态 true录像中 false 未录像*/
	public static boolean CAMERA_RECORD[] = {false,false,false,false};
	
	public static  boolean StartFlag  =false;
	/** 产品类型 1 T3 2 一甲丙益后视镜  3 有方后视镜*/
	public static int  PRODUCT_TYPE = 1;	
	//录像回放有关的定义	
	public static final String ACTION_VIDEO_PREVIEW = "ACTION_VIDEO_PREVIEW";
	public static final String ACTION_VIDEO_PLAYBACK = "ACTION_VIDEO_PLAYBACK";
	public static final String ACTION_REGIST_SUCCESS  = "ACTION_REGIST_SUCCESS";
	public static final String ACTION_VIDEO_STOP_PLAYBACK  = "ACTION_VIDEO_STOP_PLAYBACK";
	public static final String ACTION_VIDEO_FILE_PLAYBACK  = "ACTION_VIDEO_FILE_PLAYBACK";
	public static final String ACTION_VIDEO_PLAYBACK_LIST =  "ACTION_VIDEO_PLAYBACK_LIST";	
	public static final String ACTION_UPDATE_LOCATION =  "UPDATE_LOCATION";		
	/**
	 * 设置参数	 */
	public static void setParam(Context context){
		switch (PRODUCT_TYPE) {
		//T3		
		case 1:
			SD_CARD_PATH = "/mnt/extsd/";
			INNER_CARD_PATH = "/mnt/sdcard/";
			CAMERA_FILE_PATH = SD_CARD_PATH +"CarDVR/";
			SNAP_FILE_PATH = INNER_CARD_PATH+"CarDVR/";
			CAMERA_FILE_DIR = "/CarDVR/";			
			FRAMERATE = 20;
			CAMERA_ID[0] = 0;
			CAMERA_ID[1] = 3;
			CAMERA_ID[2] = 9;
			CAMERA_ID[3] = 8;			
			break;						
			//一甲丙益后视镜			
		case 2:
			SD_CARD_PATH = "/mnt/external_sdio/";
			INNER_CARD_PATH = "/mnt/sdcard/";			
			CAMERA_FILE_PATH = SD_CARD_PATH +"CarDVR/";
			SNAP_FILE_PATH = INNER_CARD_PATH+"CarDVR/";
			CAMERA_FILE_DIR = "/CarDVR/";
			FRAMERATE = 20;
			CAMERA_ID[0] = 2;
			CAMERA_ID[1] = 1;
			CAMERA_ID[2] = 5;
			CAMERA_ID[3] = 6;			
			break;
			//有方后视镜
			
		case 3:
			SD_CARD_PATH = "/mnt/sdcard/";
			INNER_CARD_PATH = "/mnt/sdcard/";
			CAMERA_FILE_PATH = SD_CARD_PATH +"CarDVR/";
			SNAP_FILE_PATH = INNER_CARD_PATH+"CarDVR/";
			CAMERA_FILE_DIR = "/CarDVR/";
			FRAMERATE = 20;
			CAMERA_ID[0] = 0;
			CAMERA_ID[1] = 2;
			CAMERA_ID[2] = 5;
			CAMERA_ID[3] = 6;
			break;
		default:
			break;
		}
		
		CAMERA_RECORD[0] = false;
		CAMERA_RECORD[1] = false;
		CAMERA_RECORD[2] = false;
		CAMERA_RECORD[3] = false;		
		try {
			//发送广播给设置的应用，传递视频路径
			Intent intent = new Intent("com.dss.camera.ACTION_VIDEO_PATH");
			intent.putExtra("EXTRA_VIDEO_PATH",CAMERA_FILE_PATH);
			context.sendBroadcast(intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**应用启动自动检测一次版本信息*/
	public static boolean checkVersion = true;
}
