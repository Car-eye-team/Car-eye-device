package org.push.push;

import android.content.Context;
import android.util.Log;

import com.sh.camera.util.StringUtil;

public class AudioDecoder {
    final private String TAG = "AudioDecoder";


    public AudioDecoder(Context c){

    }

    public void startPlay(){
        Log.i(TAG,"开始语音对讲");

    }
    public void stop(){
        Log.i(TAG,"结束语音对讲");

    }

    public void decode(byte[] data, int i, int length) {
        Log.i(TAG,"开始AAC解码:"+"起始地址：" + i + "待解码数据长度："+ length);
        String msg  = StringUtil.bytes2HexString(data);
        Log.i(TAG,msg);
    }
}
