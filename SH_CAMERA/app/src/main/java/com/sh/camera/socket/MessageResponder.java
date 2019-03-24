package com.sh.camera.socket;

import com.sh.camera.util.Tools;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MessageResponder {

	public static final String ACTION_DISPATCHER_MESSAGE_RESPONDER = "com.sh.camera.ACTION_DISPATCHER_MESSAGE_RESPONDER";// 行车记录仪应答平台视频监控消息
	public static final String ACTION_CAMERA_MESSAGE_RESPONDER = "com.sh.camera.ACTION_CAMERA_MESSAGE_RESPONDER";// 行车记录仪应答平台视频监控消息
	
	public static final String EXTRA_MSG_ID = "extra_msg_id"; // 消息ID
	public static final String EXTRA_MSG_DATA = "extra_msg_data"; // 消息数据

	/**
	 * 视频功能指令通用应答
	 * @param context
	 * @param msgId
	 * @param result
	 */
	public static void sendResult(Context context, int msgId, int result){
		byte[] body = Tools.int2Bytes(result, 1);
		Intent intent = new Intent();
		intent.setAction(ACTION_DISPATCHER_MESSAGE_RESPONDER);
		intent.putExtra(EXTRA_MSG_ID, msgId);
		intent.putExtra(EXTRA_MSG_DATA, body);
		context.sendBroadcast(intent);
	}
	
	public static void SendCameraStatus(Context context, int msgId, int CameraID){
		byte[] body = Tools.int2Bytes(CameraID, 1);
		Intent intent = new Intent();
		intent.setAction(ACTION_CAMERA_MESSAGE_RESPONDER);
		intent.putExtra(EXTRA_MSG_ID, msgId);
		intent.putExtra(EXTRA_MSG_DATA, body);
		context.sendBroadcast(intent);		
	}	
	
}
