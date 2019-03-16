package com.sh.camera.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by John on 2016/3/12.
 */
public class Client implements Closeable {

    private static int sKey;
    private static Context mContext;
    private volatile int paused = 0;
    private static final Handler h = new Handler(Looper.getMainLooper());
    private static Set<Integer> _channelPause = new HashSet<>();
    private final Runnable closeTask = new Runnable() {
        @Override
        public void run() {
            if (paused > 0) {
                Log.i(TAG, "realPause! close stream");
                closeStream();
                paused = 2;
            }
        }
    };
    private int _channel;
    private String _url;
    private int _type;
    private int _mediaType;
    private String _user;
    private String _pwd;
    private int _sendOption;


    public static final class FrameInfo {
        public int codec;			/* 音视频格式 */

        public int type;			/* 视频帧类型 */
        public byte fps;			/* 视频帧率 */
        public short width;			/* 视频宽 */
        public short height;			/* 视频高 */

        public int reserved1;			/* 保留参数1 */
        public int reserved2;			/* 保留参数2 */

        public int sample_rate;	/* 音频采样率 */
        public int channels;		/* 音频声道数 */
        public int bits_per_sample;	/* 音频采样精度 */

        public int length;			/* 音视频帧大小 */
        public long timestamp_usec;	/* 时间戳,微妙 */
        public long timestamp_sec;	/* 时间戳 秒 */

        public long stamp;

        public float bitrate;		/* 比特率 */
        public float losspacket;		/* 丢包率 */

        public byte[] buffer;
        public int offset = 0;
        public boolean audio;
    }

    public static final class MediaInfo {
//        Easy_U32 u32VideoCodec;				/*  ”∆µ±‡¬Î¿‡–Õ */
//        Easy_U32 u32VideoFps;				/*  ”∆µ÷°¬  */
//
//        Easy_U32 u32AudioCodec;				/* “Ù∆µ±‡¬Î¿‡–Õ */
//        Easy_U32 u32AudioSamplerate;		/* “Ù∆µ≤…—˘¬  */
//        Easy_U32 u32AudioChannel;			/* “Ù∆µÕ®µ¿ ˝ */
//        Easy_U32 u32AudioBitsPerSample;		/* “Ù∆µ≤…—˘æ´∂» */
//
//        Easy_U32 u32H264SpsLength;			/*  ”∆µsps÷°≥§∂» */
//        Easy_U32 u32H264PpsLength;			/*  ”∆µpps÷°≥§∂» */
//        Easy_U8	 u8H264Sps[128];			/*  ”∆µsps÷°ƒ⁄»› */
//        Easy_U8	 u8H264Pps[36];				/*  ”∆µsps÷°ƒ⁄»› */

        int videoCodec;
        int fps;
        int audioCodec;
        int sample;
        int channel;
        int bitPerSample;
        int spsLen;
        int ppsLen;
        byte[] sps;
        byte[] pps;


        @Override
        public String toString() {
            return "MediaInfo{" +
                    "videoCodec=" + videoCodec +
                    ", fps=" + fps +
                    ", audioCodec=" + audioCodec +
                    ", sample=" + sample +
                    ", channel=" + channel +
                    ", bitPerSample=" + bitPerSample +
                    ", spsLen=" + spsLen +
                    ", ppsLen=" + ppsLen +
                    '}';
        }
    }

    public interface SourceCallBack {
        void onSourceCallBack(int _channelId, int _channelPtr, int _frameType, FrameInfo frameInfo);

        void onMediaInfoCallBack(int _channelId, MediaInfo mi);

        void onEvent(int _channelId, int err, int info);
    }


    public static final int EASY_SDK_VIDEO_FRAME_FLAG = 0x01;
    public static final int EASY_SDK_AUDIO_FRAME_FLAG = 0x02;
    public static final int EASY_SDK_EVENT_FRAME_FLAG = 0x04;
    public static final int EASY_SDK_RTP_FRAME_FLAG = 0x08;		/* RTP帧标志 */
    public static final int EASY_SDK_SDP_FRAME_FLAG = 0x10;		/* SDP帧标志 */
    public static final int EASY_SDK_MEDIA_INFO_FLAG = 0x20;		/* 媒体类型标志*/

    public static final int EASY_SDK_EVENT_CODEC_ERROR = 0x63657272;	/* ERROR */
    public static final int EASY_SDK_EVENT_CODEC_EXIT = 0x65786974;	/* EXIT */

    public static final int TRANSTYPE_TCP = 1;
    public static final int TRANSTYPE_UDP = 2;
    private static final String TAG = Client.class.getSimpleName();

    static {
        System.loadLibrary("EasyRTSPClient");
    }

    private long mCtx;
    private static final SparseArray<SourceCallBack> sCallbacks = new SparseArray<>();

    Client(Context context, String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (context == null) {
            throw new NullPointerException();
        }
        mCtx = init(context, key);
        mContext = context.getApplicationContext();
        if (mCtx == 0 || mCtx == -1) {
            Log.wtf(TAG, new IllegalArgumentException("初始化失败，KEY不合法！")) ;
        }
    }

    int registerCallback(SourceCallBack cb) {
        synchronized (sCallbacks) {
            sCallbacks.put(++sKey, cb);
            return sKey;
        }
    }

    void unrigisterCallback(SourceCallBack cb) {
        synchronized (sCallbacks) {
            int idx = sCallbacks.indexOfValue(cb);
            if (idx != -1) {
                sCallbacks.removeAt(idx);
            }
        }
    }

    public int getLastErrorCode() {
        return getErrorCode(mCtx);
    }

    public int openStream(int channel, String url, int type, int sendOption, int mediaType, String user, String pwd) {
        _channel = channel;
        _url = url;
        _type = type;
        _mediaType = mediaType;
        _user = user;
        _pwd = pwd;
        _sendOption = sendOption;
        return openStream();
    }

    public void closeStream() {
        h.removeCallbacks(closeTask);
        if (mCtx != 0){
            closeStream(mCtx);
        }
    }

    private static native int getErrorCode(long context);

    private native long init(Context context, String key);

    private native int deInit(long context);

    private int openStream() {
        if (null == _url) {
            throw new NullPointerException();
        }
        if (mCtx == 0){
            throw new IllegalStateException("初始化失败，KEY不合法");
        }
        return openStream(mCtx, _channel, _url, _type, _mediaType, _user, _pwd, 1000, 0, _sendOption);
    }

    private native int openStream(long context, int channel, String url, int type, int mediaType, String user, String pwd, int reconn, int outRtpPacket, int rtspOption);

//    private native int startRecord(int context, String path);
//
//    private native void stopRecord(int context);

    private native void closeStream(long context);


    private static void save2path(byte[] buffer, int offset, int length, String path, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path, append);
            fos.write(buffer, offset, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void onEvent(int channel, int err, int state) {
        //state :  1  Connecting     2 : 连接错误    3 : 连接线程退出
        Log.e(TAG, String.format("__RTSPClientCallBack onEvent: err=%d, state=%d", err, state));

        synchronized (sCallbacks) {
            final SourceCallBack callBack = sCallbacks.get(channel);
            if (callBack != null) {
                callBack.onEvent(channel, err, state);
            }
        }
    }


    public void pause() {
        if (Looper.myLooper() != Looper.getMainLooper()){
            throw new IllegalThreadStateException("please call pause in Main thread!");
        }
        synchronized (_channelPause) {
            _channelPause.add(_channel);
        }
        paused = 1;
        Log.i(TAG,"pause:=" + 1);
        h.postDelayed(closeTask, 10000);
    }

    public void resume() {
        if (Looper.myLooper() != Looper.getMainLooper()){
            throw new IllegalThreadStateException("call resume in Main thread!");
        }
        synchronized (_channelPause) {
            _channelPause.remove(_channel);
        }
        h.removeCallbacks(closeTask);
        if (paused == 2){
            Log.i(TAG,"resume:=" + 0);
            openStream();
        }
        Log.i(TAG,"resume:=" + 0);
        paused = 0;
    }

    @Override
    public void close() throws IOException {
        h.removeCallbacks(closeTask);
        _channelPause.remove(_channel);
        if (mCtx == 0) throw new IOException("not opened or already closed");
        deInit(mCtx);
        mCtx = 0;
    }
}
