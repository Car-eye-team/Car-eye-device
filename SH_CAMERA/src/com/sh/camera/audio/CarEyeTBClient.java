package com.sh.camera.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;

import com.sh.camera.codec.EasyMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static com.sh.camera.audio.Client.TRANSTYPE_TCP;

/**
 * Created by John on 2016/3/17.
 */
public class CarEyeTBClient implements Client.SourceCallBack {

    private static final String TAG = CarEyeTBClient.class.getSimpleName();

    /* 音频编码 */
    public static final int EASY_SDK_AUDIO_CODEC_AAC = 0x15002;        /* AAC */
    public static final int EASY_SDK_AUDIO_CODEC_G711U = 0x10006;        /* G711 ulaw*/
    public static final int EASY_SDK_AUDIO_CODEC_G711A = 0x10007;    /* G711 alaw*/
    public static final int EASY_SDK_AUDIO_CODEC_G726 = 0x1100B;    /* G726 */


    private static final int NAL_VPS = 32;
    private static final int NAL_SPS = 33;
    private static final int NAL_PPS = 34;

    private final String mKey;
    private volatile Thread mThread, mAudioThread;
    private AcousticEchoCanceler mAcousticEchoCanceler;
    private final ResultReceiver mRR;
    private Client mClient;
    private boolean mAudioEnable = true;
    private volatile long mReceivedDataLength;
    private AudioTrack mAudioTrack;
    private String mRecordingPath;
    private EasyMuxer mObject;
    private Client.MediaInfo mMediaInfo;

    /**
     *
     */
    private int mRecordingStatus;
    private long muxerPausedMillis = 0L;
    private long mMuxerCuttingMillis = 0L;
    private volatile long mNewestStample;
    private boolean mWaitingKeyFrame;
    private boolean mTimeout;
    private boolean mNotSupportedAudioCB;


    public static final int RESULT_UNSUPPORTED_AUDIO = 06;
    public static final int RESULT_TIMEOUT = 03;
    public static final int RESULT_EVENT = 04;


    private final Context mContext;

    /**
     * 参数说明
     * 第一个参数为Context
     * 第二个参数为KEY
     * 第三个参数为的textureView,用来显示视频画面
     * 第四个参数为一个ResultReceiver,用来接收SDK层发上来的事件通知;
     * 第五个参数为I420DataCallback,如果不为空,那底层会把YUV数据回调上来.
     */
    public CarEyeTBClient(Context context, String key, ResultReceiver receiver) {
        mContext = context;
        mKey = key;
        mRR = receiver;
    }

    @Override
    public void onSourceCallBack(int _channelId, int _channelPtr, int _frameType, Client.FrameInfo frameInfo) {
        //        long begin = SystemClock.elapsedRealtime();
        try {
            onRTSPSourceCallBack1(_channelId, _channelPtr, _frameType, frameInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
//            Log.d(TAG, String.format("onRTSPSourceCallBack %d", SystemClock.elapsedRealtime() - begin));
        }
    }
    public void onRTSPSourceCallBack1(int _channelId, int _channelPtr, int _frameType, Client.FrameInfo frameInfo) {
        Thread.currentThread().setName("PRODUCER_THREAD");
        if (frameInfo != null) {
            mReceivedDataLength += frameInfo.length;
        }
        if (_frameType == Client.EASY_SDK_AUDIO_FRAME_FLAG) {
            mNewestStample = frameInfo.stamp;
            frameInfo.audio = true;
            if (true) {
                if (frameInfo.codec != EASY_SDK_AUDIO_CODEC_AAC &&
                        frameInfo.codec != EASY_SDK_AUDIO_CODEC_G711A &&
                        frameInfo.codec != EASY_SDK_AUDIO_CODEC_G711U &&
                        frameInfo.codec != EASY_SDK_AUDIO_CODEC_G726) {
                    ResultReceiver rr = mRR;
                    if (!mNotSupportedAudioCB && rr != null) {
                        mNotSupportedAudioCB = true;
                        if (rr != null) {
                            rr.send(RESULT_UNSUPPORTED_AUDIO, null);
                        }
                    }
                    return;
                }

            }
//            Log.d(TAG, String.format("queue size :%d", mQueue.size()));
            try {
                mQueue.put(frameInfo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (_frameType == 0) {
            // time out...
            if (!mTimeout) {
                mTimeout = true;

                ResultReceiver rr = mRR;
                if (rr != null) rr.send(RESULT_TIMEOUT, null);
            }
        } else if (_frameType == Client.EASY_SDK_EVENT_FRAME_FLAG) {
            ResultReceiver rr = mRR;
            Bundle resultData = new Bundle();
            resultData.putString("event-msg", new String(frameInfo.buffer));
            if (rr != null) rr.send(RESULT_EVENT, null);
        }
    }
    @Override
    public void onMediaInfoCallBack(int _channelId, Client.MediaInfo mi) {
        mMediaInfo = mi;
        Log.i(TAG, String.format("MediaInfo fetchd\n%s", mi));
    }

    @Override
    public void onEvent(int _channelId, int err, int info) {
        ResultReceiver rr = mRR;
        Bundle resultData = new Bundle();

        switch (info) {
            case 1:
                resultData.putString("event-msg", "连接中...");
                break;
            case 2:
                resultData.putInt("errorcode", err);
                resultData.putString("event-msg", String.format("错误：%d", err));
                break;
            case 3:
                resultData.putInt("errorcode", err);
                resultData.putString("event-msg", String.format("线程退出。%d", err));
                break;
        }
        if (rr != null) rr.send(RESULT_EVENT, resultData);

    }

//    private RtmpClient mRTMPClient = new RtmpClient();

    private static class FrameInfoQueue extends PriorityQueue<Client.FrameInfo> {
        public static final int CAPACITY = 500;
        public static final int INITIAL_CAPACITY = 300;

        public FrameInfoQueue() {
            super(INITIAL_CAPACITY, new Comparator<Client.FrameInfo>() {

                @Override
                public int compare(Client.FrameInfo frameInfo, Client.FrameInfo t1) {
                    return (int) (frameInfo.stamp - t1.stamp);
                }
            });
        }

        final ReentrantLock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();
        final Condition notVideo = lock.newCondition();
        final Condition notAudio = lock.newCondition();

        @Override
        public int size() {
            lock.lock();
            try {
                return super.size();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void clear() {
            lock.lock();
            try {
                int size = super.size();
                super.clear();
                int k = size;
                for (; k > 0 && lock.hasWaiters(notFull); k--)
                    notFull.signal();
            } finally {
                lock.unlock();
            }
        }

        public void put(Client.FrameInfo x) throws InterruptedException {
            lock.lockInterruptibly();
            try {
                int size;
                while ((size = super.size()) == CAPACITY) {
                    Log.v(TAG, "queue full:" + CAPACITY);
                    notFull.await();
                }
                offer(x);
//                Log.d(TAG, String.format("queue size : " + size));
                // 这里是乱序的。并非只有空的queue才丢到首位。因此不能做限制 if (size == 0)
                {

                    if (x.audio) {
                        notAudio.signal();
                    } else {
                        notVideo.signal();
                    }
                }

            } finally {
                lock.unlock();
            }
        }

        public Client.FrameInfo takeAudioFrame() throws InterruptedException {
            lock.lockInterruptibly();
            try {
                while (true) {
                    Client.FrameInfo x = peek();
                    if (x == null) {
                        notAudio.await();
                    } else {
                        if (x.audio) {
                            remove();
                            notFull.signal();
                            notVideo.signal();
                            return x;
                        } else {
                            notAudio.await();
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private FrameInfoQueue mQueue = new FrameInfoQueue();



    /**
     * 启动播放
     *
     * @param url
     * @return
     */
    public void play(final String url) {
        start(url, TRANSTYPE_TCP, 0, Client.EASY_SDK_AUDIO_FRAME_FLAG,  "", "", null);
    }

    /**
     * 启动播放
     *
     * @param url
     * @param type
     * @param sendOption
     * @param mediaType
     * @param user
     * @param pwd
     * @return
     */
    public int start(final String url, int type, int sendOption, int mediaType, String user, String pwd) {
        return start(url, type, sendOption, mediaType, user, pwd, null);
    }

    /**
     * 启动播放
     *  url, TRANSTYPE_TCP, 0,Client.EASY_SDK_VIDEO_FRAME_FLAG | Client.EASY_SDK_AUDIO_FRAME_FLAG, "", "", null
     * @param url
     * @param type：TCP or UDP
     * @param sendOption
     * @param mediaType EASY_SDK_VIDEO_FRAME_FLAG EASY_SDK_AUDIO_FRAME_FLAG
     * @param user
     * @param pwd
     * @return
     */
    public int start(final String url, int type, int sendOption, int mediaType, String user, String pwd, String recordPath) {
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        if (type == 0)
            type = TRANSTYPE_TCP;
        mQueue.clear();
        startAudio();
//        mTimeout = false;
//        mNotSupportedVideoCB = mNotSupportedAudioCB = false;
        mReceivedDataLength = 0;
        mClient = new Client(mContext, mKey);
        int channel = mClient.registerCallback(this);
        mRecordingPath = recordPath;
        Log.i(TAG, String.format("playing url:\n%s\n", url));
        return mClient.openStream(channel, url, type, sendOption, mediaType, user, pwd);
    }

    public boolean isAudioEnable() {
        return mAudioEnable;
    }

    public void setAudioEnable(boolean enable) {
        mAudioEnable = enable;
        AudioTrack at = mAudioTrack;
        if (at != null) {
            Log.i(TAG, String.format("audio will be %s", enable ? "enabled" : "disabled"));
            synchronized (at) {
                if (!enable) {
                    at.pause();
                    at.flush();
                } else {
                    at.flush();
                    at.play();
                }
            }
        }
    }


    public void pause() {
        mQueue.clear();
        if (mClient != null) {
            mClient.pause();
        }
        mQueue.clear();
    }

    public void resume() {
        if (mClient != null) {
            mClient.resume();
        }
    }

    /**
     * 终止播放
     */
    public void stop() {
        Thread t = mThread;
        mThread = null;
        if (t != null) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        t = mAudioThread;
        mAudioThread = null;
        if (t != null) {
            t.interrupt();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mQueue.clear();
        if (mClient != null) {
            mClient.unrigisterCallback(this);
            mClient.closeStream();
            try {
                mClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mQueue.clear();
        mClient = null;
//        mNewestStample = 0;
    }

    public long receivedDataLength() {
        return mReceivedDataLength;
    }

    private void startAudio() {
        mAudioThread = new Thread("AUDIO_CONSUMER") {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    Client.FrameInfo frameInfo;
                    long handle = 0;
                    final AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    AudioManager.OnAudioFocusChangeListener l = new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int focusChange) {
                            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                                AudioTrack audioTrack = mAudioTrack;
                                if (audioTrack != null) {
                                    audioTrack.setStereoVolume(1.0f, 1.0f);
                                    if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                                        audioTrack.flush();
                                        audioTrack.play();
                                    }
                                }
                            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                                AudioTrack audioTrack = mAudioTrack;
                                if (audioTrack != null) {
                                    if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                                        audioTrack.pause();
                                    }
                                }
                            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                                AudioTrack audioTrack = mAudioTrack;
                                if (audioTrack != null) {
                                    audioTrack.setStereoVolume(0.5f, 0.5f);
                                }
                            }
                        }
                    };
                    try {
                        int requestCode = am.requestAudioFocus(l, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        if (requestCode != AUDIOFOCUS_REQUEST_GRANTED) {
                            return;
                        }
                        do {
                            frameInfo = mQueue.takeAudioFrame();
                            if (mMediaInfo != null) break;
                        } while (true);
                        final Thread t = Thread.currentThread();

                        if (mAudioTrack == null) {
                            int sampleRateInHz = (int) (mMediaInfo.sample * 1.001);
                            int channelConfig = mMediaInfo.channel == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
                            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                            int bfSize = AudioTrack.getMinBufferSize(mMediaInfo.sample, channelConfig, audioFormat) * 8;
                            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat,
                                    bfSize, AudioTrack.MODE_STREAM,am.generateAudioSessionId());

                            // 初始化回音消除
                            try {
                                AcousticEchoCanceler acousticEchoCanceler = AcousticEchoCanceler.create( am.generateAudioSessionId());
                                acousticEchoCanceler.setEnabled(true);
                                mAcousticEchoCanceler = acousticEchoCanceler;
                            } catch (Exception e) {
                                mAcousticEchoCanceler = null;
                            }
                        }
                        mAudioTrack.play();
                        handle = AudioCodec.create(frameInfo.codec, frameInfo.sample_rate, frameInfo.channels, frameInfo.bits_per_sample);

                        // 半秒钟的数据缓存
                        byte[] mBufferReuse = new byte[16000];
                        int[] outLen = new int[1];
                        while (mAudioThread != null) {
                            if (frameInfo == null) {
                                frameInfo = mQueue.takeAudioFrame();
                            }
                            if (frameInfo.codec == EASY_SDK_AUDIO_CODEC_AAC && false) {
                                pumpAACSample(frameInfo);
                            }
                            outLen[0] = mBufferReuse.length;
                            long ms = SystemClock.currentThreadTimeMillis();
                            int nRet = AudioCodec.decode(handle, frameInfo.buffer, 0, frameInfo.length, mBufferReuse, outLen);
                            if (nRet == 0) {
//                                if (frameInfo.codec != EASY_SDK_AUDIO_CODEC_AAC )
                                {
//                                    save2path(mBufferReuse, 0, outLen[0],"/sdcard/111.pcm", true);
                                    pumpPCMSample(mBufferReuse, outLen[0], frameInfo.stamp);
                                }
                                if (mAudioEnable)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mAudioTrack.write(mBufferReuse, 0, outLen[0], AudioTrack.WRITE_NON_BLOCKING);
                                    } else {
                                        mAudioTrack.write(mBufferReuse, 0, outLen[0]);
                                    }

                            }
                            frameInfo = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        am.abandonAudioFocus(l);
                        if (handle != 0) {
                            AudioCodec.close(handle);
                        }
                        AudioTrack track = mAudioTrack;
                        if (track != null) {
                            synchronized (track) {
                                mAudioTrack = null;
                                track.release();
                            }
                        }
                    }
                }
            }
        };

        mAudioThread.start();
    }


    private void pumpAACSample(Client.FrameInfo frameInfo) {
        EasyMuxer muxer = mObject;
        if (muxer == null) return;
        MediaCodec.BufferInfo bi = new MediaCodec.BufferInfo();
        bi.offset = frameInfo.offset;
        bi.size = frameInfo.length;
        ByteBuffer buffer = ByteBuffer.wrap(frameInfo.buffer, bi.offset, bi.size);
        bi.presentationTimeUs = frameInfo.stamp;

        try {
            if (!frameInfo.audio) {
                throw new IllegalArgumentException("frame should be audio!");
            }
            if (frameInfo.codec != EASY_SDK_AUDIO_CODEC_AAC) {
                throw new IllegalArgumentException("audio codec should be aac!");
            }
            bi.offset += 7;
            bi.size -= 7;
            muxer.pumpStream(buffer, bi, false);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }
    }


    private synchronized void pumpPCMSample(byte[] pcm, int length, long stampUS) {
        Log.i(TAG, "writeFrame audio ret:");
    }



}