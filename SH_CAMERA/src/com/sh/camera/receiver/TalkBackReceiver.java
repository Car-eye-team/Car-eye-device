package com.sh.camera.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sh.camera.audio.LogUtils;
import com.sh.camera.audio.ReadAACFileThread;
import com.sh.camera.bll.PrefBiz;
import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;

import java.util.List;

public class TalkBackReceiver extends BroadcastReceiver {
	private static final String TAG = "TalkBackReceiver.";
	private ReadAACFileThread audioThread;

	@Override
	public void onReceive(Context context, Intent intent) {
		String key = intent.getStringExtra("tbaction");
		if(key.equals("start")){
			LogUtils.d(TAG,"开启语音对讲");
			if (audioThread == null) {
				audioThread = new ReadAACFileThread();
				audioThread.start();
			}

		}else if(key.equals("stop")){
			LogUtils.d(TAG,"关闭语音对讲");
			audioThread.stopplaying();
			audioThread = null;

		}else{


		}
	}

}
