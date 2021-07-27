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
	public static int MAX_NUM_OF_CAMERAS = 4;
	/**服务器IP*/
	public static final String SERVER_IP = "120.76.235.109";
	/**handle apk 升级消息*/
	public static final int  MSG_APK_NEW = 1001;
	/**服务器端口*/
	public static final String SERVER_PORT = "10077";
	public static final String SERVER_ADDPORT = "10000";
	
	/**PT服务器端口*/
	public static final String PT_SERVER_PORT = "9999";
	public static final String PT_SERVER_IP = "39.108.246.45";
	
	/**设备号*/
	public static final String STREAM_NAME = "13510671870";

	
	/**PT服务端ip*/
	public static final String PTSERVICE_IP = "ptservice_ip";
	/**PT服务端port*/
	public static final String PTSERVICE_PORT = "ptservice_port";
	/**模式 model */

	public static String ip = "ip";
	public static String port = "port";
	public static String name = "name";
	public static String fps = "fps";
	public static String rule = "rule";
	public static String mode = "mode";
	public static String addPort = "add_port";
	public static String  protocol_type = "protocol_type";
	public static int CAREYE_VCODE_H264 = 0x1C;
	public static int CAREYE_VCODE_H265 = 0x48323635;

	public static int CAREYE_ACODE_AAC = 0x15002;
	public static int CAREYE_ACODE_G711U = 0x10006;
	public static int CAREYE_ACODE_G711A = 0x10007;
	public static int CAREYE_ACODE_G726 = 0x10007;

	public static int CAREYE_VCODE_H264_1078 = 98;
	public static int CAREYE_VCODE_H265_1078 = 99;
	public static int CAREYE_ACODE_AAC_1078 = 0x13;
	public static int CAREYE_ACODE_G711U_1078 = 7;
	public static int CAREYE_ACODE_G711A_1078 = 6;
	public static int CAREYE_ACODE_G726_1078 = 9;

	/* audio paranter  */
	public static int CAREYE_AUDIO_SAMPLE_RATE_1078= 0; //8KHZ
	public static int CAREYE_AUDIO_SAMPLE_BITS_1078= 1; //(16BITS)



	/* rtsp/rtmp   */
	public static int CAREYE_RTSP_PROTOCOL = 0;
	public static int CAREYE_RTMP_PROTOCOL = 1;
	public static int CAREYE_RTP_PROTOCOL = 2;

	public static final int protocol = CAREYE_RTP_PROTOCOL;

	/**录像时长 分钟*/
	public static final int VIDEO_TIME = 10;//10
	/**SD卡路径*/
	public static String CAMERA_FILE_PATH = "";
	public static String CAMERA_FILE_DIR = "";
	public static String SD_CARD_PATH = "/mnt/sdcard";
	public static String INNER_CARD_PATH = "";
	public static String SNAP_FILE_PATH = "";
	/**SD清理界限、清理目标  100M 14950M*/
	public static final  int SD_FREE2C = 800;//清理界限、清理目标 
	/**SD清理界限、清理目标  达到极限 10M*/
	public static final  long SD_FREEJX = 200;//清理界限、清理目标达到极限 5M 
	/**是否正在清理sd内存*/
	public static  boolean isCleaning = false;
	public static  boolean AudioRecord = true;
	public static  final boolean ExtPlayer = false;
	/**帧速率*/
	public static int FRAMERATE = 20;
	/**摄像头ID*/
	public static int[] CAMERA_ID = {0,1,2,3};

	/**录像状态 true录像中 false 未录像*/
	public static boolean CAMERA_RECORD[] = {false,false,false,false};
	
	public static  boolean StartFlag  =false;
	/** 产品类型 1 T3 2 一甲丙益后视镜  3 有方后视镜*/
	//录像回放有关的定义	
	public static final String ACTION_VIDEO_PREVIEW = "ACTION_VIDEO_PREVIEW";
	public static final String ACTION_VIDEO_PLAYBACK = "ACTION_VIDEO_PLAYBACK";
	public static final String ACTION_REGIST_SUCCESS  = "ACTION_REGIST_SUCCESS";
	public static final String ACTION_VIDEO_STOP_PLAYBACK  = "ACTION_VIDEO_STOP_PLAYBACK";
	public static final String ACTION_VIDEO_FILE_PLAYBACK  = "ACTION_VIDEO_FILE_PLAYBACK";
	public static final String ACTION_VIDEO_PLAYBACK_LIST =  "ACTION_VIDEO_PLAYBACK_LIST";	
	public static final String ACTION_UPDATE_LOCATION =  "UPDATE_LOCATION";

	//RTMP pusher key
	public static final String Key = "q3AuiU18%ug8kUgTIT1T2)3TITi7$UiTH6hTiTLUiUjU37K8iU(7KUiU1U3Ug8%U145";
	//RTP pusher key
	public static final String rtpKey = "<2F-}OMnm-NnoON?l?M?PPp?l?l==M4?nlN?5?MO}OQOp5?n}OO5?O}OMOpONnmOM69";

	/**
	 * 设置参数	 */
	public static void setParam(Context context){
			SD_CARD_PATH = "/mnt/extsd/";
			INNER_CARD_PATH = "/mnt/sdcard/";
			CAMERA_FILE_PATH = SD_CARD_PATH +"CarDVR/";
			SNAP_FILE_PATH = INNER_CARD_PATH+"CarDVR/";
			CAMERA_FILE_DIR = "/CarDVR/";			
			FRAMERATE = 20;
			//摄像头id,1取值为2是因为不需要前置摄像头，前置和后置不能一起打开
			CAMERA_ID[0] = 0;
			CAMERA_ID[1] = 2;
			CAMERA_ID[2] = 3;
			CAMERA_ID[3] = 4;
	}
	/**应用启动自动检测一次版本信息*/
	public static final String CAMERA_PATH ="Careye_pusher/";
	public static boolean checkVersion = true;

	//人脸识别2.0版本的appId和appKey
	public static final String APP_ID_FACE_2 = "3rWH4Jgae4iunSweK3cWdm4JfuQ8UHMsoDGRznxanHW2";
	public static final String SDK_KEY_FACE_2 = "CyuKUsxFHLodXdoVxk6Ly9XcWbypjU5RWEHhetJJ8sZB";

	//人脸识别1.2版本的appId和appKey
	public static final String APP_ID_FACE_12 = "3rWH4Jgae4iunSweK3cWdm4JfuQ8UHMsoDGRznxanHW2";
	public static final String SDK_KEY_FACE_12 = "CyuKUsxFHLodXdoVxk6Ly9Byjq2aBCaY9BwrVZoJrFCL,CyuKUsxFHLodXdoVxk6Ly9C6uEHgKVXeqq5oixFZULsZ,CyuKUsxFHLodXdoVxk6Ly9CbYqLNWJjan8C7cHVfRrEV,CyuKUsxFHLodXdoVxk6Ly9CqsdrjkSXWnJvC91pUMKfB,CyuKUsxFHLodXdoVxk6Ly9Cy337twAWtb1K9cQsUJKpx,";
}
