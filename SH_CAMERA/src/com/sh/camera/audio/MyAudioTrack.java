package com.sh.camera.audio;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Author : szmichaelYe
 * CreateDate : 2019/3/24  14:01
 * Email : haigand@163.com
 * Version : 1.0
 * Desc : 播放pcm数据
 * Modified :
 *
 */
public class MyAudioTrack {
    private int mFrequency;// 采样率
    private int mChannel;// 声道
    private int mSampBit;// 采样精度
    private AudioTrack mAudioTrack;

    public MyAudioTrack(int frequency, int channel, int sampbit) {
        mFrequency = frequency;
        mChannel = channel;
        mSampBit = sampbit;
    }

    /**
     * 初始化
     */
    public void init() {
        if (mAudioTrack != null) {
            release();
        }
        // 获得构建对象的最小缓冲区大小
        int minBufSize = getMinBufferSize();
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                mFrequency, mChannel, mSampBit, minBufSize, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    /**
     * 将解码后的pcm数据写入audioTrack播放
     *
     * @param data   数据
     * @param offset 便宜
     * @param length 需要播放的长度
     */
    public void playAudioTrack(byte[] data, int offset, int length) {
        if (data == null || data.length == 0) {
            return;
        }
        try {
            mAudioTrack.write(data, offset, length);
        } catch (Exception e) {
            Log.e("MyAudioTrack", "AudioTrack Exception : " + e.toString());
            e.printStackTrace();
        }
    }

    public int getMinBufferSize() {
        return AudioTrack.getMinBufferSize(mFrequency,
                mChannel, mSampBit);
    }
}
