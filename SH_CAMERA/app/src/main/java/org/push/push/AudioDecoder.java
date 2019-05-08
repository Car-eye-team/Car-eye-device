package org.push.push;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Process;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * @author lavender
 * @createtime 2018/5/24
 * @desc
 */

public class AudioDecoder extends Thread {

    private MediaCodec audioDecoder;//音频解码器
    private static final String TAG = "AACDecoderUtil";
    //声道数
    private static final int KEY_CHANNEL_COUNT = 1;
    //采样率
    private static final int KEY_SAMPLE_RATE = 8000;
    private final Object obj = new Object();

    private Thread audioThread;
    private long prevPresentationTimes;
    private InputStream inputStream;
    private Context context;

    private MediaCodec.BufferInfo encodeBufferInfo;
    private ByteBuffer[] encodeInputBuffers;
    private ByteBuffer[] encodeOutputBuffers;

    public boolean isStop = false;

    int SAMPLE_RATE = 8000; //采样率8K或16k

    private AudioTrack audioTrack;
    private int buffSize = 0;
    private int bufferSizeInBytes = 0;
    int size = 0;
    
    public AudioDecoder(Context context) {
        this.context = context;
        isStop = true;
        
    }


    /**
     * 获取当前的时间戳
     *
     * @return
     */
    private long getPTSUs() {
        long result = System.nanoTime() / 1000;
        if (result < prevPresentationTimes) {
            result = (prevPresentationTimes - result) + result;
        }
        return result;
    }


    /**
     * 开启解码播放
     * @throws IOException 
     */
    public void startPlay() throws IOException {
    	if(isStop == false)
    		return;
        isStop = false;
        prevPresentationTimes = 0;
       
        prepare();
        bufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,//播放途径  外放
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes * 4,
                AudioTrack.MODE_STREAM);
        //启动AudioTrack
        audioTrack.play();      
        Log.i("CMD", "start");
        startDecoder();          

    }

    /**
     * 停止并且重启解码器以便下次使用
     */
    public void stopdecoder() {
    	if(isStop == true)
    		return;
    	isStop = true;      
        
    	try {
            Thread t = audioThread;
            audioThread = null;
            if (t != null) {
                t.interrupt();
                t.join();
            }
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        }
       if(audioDecoder!=null) {
            audioDecoder.stop();
            audioDecoder.release();
            audioDecoder = null;
        }
	    if (audioTrack != null) {
	        audioTrack.stop();
	        audioTrack.release();
	        audioTrack = null;
	    }        
	    Log.i("CMD", "stop");
        
    }
    public void startDecoder() throws IOException
    {
    	
    	audioThread = new Thread(new Runnable() {
        @Override
        public void run() {
        	
	        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
	        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
	        do {
	            encodeOutputBuffers = audioDecoder.getOutputBuffers();
	            int outputBufferIndex = audioDecoder.dequeueOutputBuffer(bufferInfo, 30000);
	        
	
	            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
	                // no output available yet
	            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
	                // not expected for an encoder
	            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
	           /* EasyMuxer muxer = mMuxer;
	            if (muxer != null) {
	                // should happen before receiving buffers, and should only happen once
	                MediaFormat newFormat = mMediaCodec.getOutputFormat();
	                muxer.addTrack(newFormat, true);
	            }*/
	            } else if (outputBufferIndex < 0) {
	                // let's ignore it
	            } else {
	                ByteBuffer outputBuffer;
	                //获取解码后的ByteBuffer
	                outputBuffer = encodeOutputBuffers[outputBufferIndex];
	                //用来保存解码后的数据
	                byte[] outData = new byte[bufferInfo.size];
	
	                outputBuffer.get(outData);
	                //清空缓存
	                outputBuffer.clear();
	                //播放解码后的数据
	                if(outData.length>0) {
	                    audioTrack.write(outData, 0, outData.length);
	                }
	                //释放已经解码的buffer
	                audioDecoder.releaseOutputBuffer(outputBufferIndex, false);
	            }
	        }while(!isStop);
        	}
        }, "AACRecoder");
    	audioThread.start();
           
  }

    /**
     * 初始化音频解码器
     *
     * @return 初始化失败返回false，成功返回true
     */
    public boolean prepare() throws IOException {
        //需要解码数据的类型
		String mine = "audio/mp4a-latm";
		//初始化解码器
		audioDecoder = MediaCodec.createDecoderByType(mine);
		//MediaFormat用于描述音视频数据的相关参数
		MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.KEY_AAC_PROFILE, 8000, 1);
		//数据类型
		mediaFormat.setString(MediaFormat.KEY_MIME, mine);
		//采样率
		mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, KEY_SAMPLE_RATE);//16k
		//声道个数
		mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, KEY_CHANNEL_COUNT);//单声道
		mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
		mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024);//作用于inputBuffer的大小
		//比特率
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
		//用来标记AAC是否有adts头，1->有
		mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);
		mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
		//ByteBuffer key（参数代表 单声道，16000采样率，AAC LC数据）
// *******************根据自己的音频数据修改data参数*************************************************
		//AAC Profile 5bits | 采样率 4bits | 声道数 4bits | 其他 3bits |
		byte[] data = new byte[]{(byte) 0x15, (byte) 0x88};
		ByteBuffer csd_0 = ByteBuffer.wrap(data);
		mediaFormat.setByteBuffer("csd-0", csd_0);
		//解码器配置
		audioDecoder.configure(mediaFormat, null, null, 0);
        if (audioDecoder == null) {
            return false;
        }
        audioDecoder.start();
        encodeInputBuffers = audioDecoder.getInputBuffers();
        encodeOutputBuffers = audioDecoder.getOutputBuffers();
        encodeBufferInfo = new MediaCodec.BufferInfo();
        return true;
    }
    /**
     * aac音频解码+播放
     */
    public void decode(byte[] buf, int offset, int length) {
    	
        //等待时间，0->不等待，-1->一直等待    	
        long kTimeOutUs = 1000;
        if(isStop)
        	return;
        try {
            //返回一个包含有效数据的input buffer的index,-1->不存在
            int inputBufIndex = audioDecoder.dequeueInputBuffer(0);
          
            if (inputBufIndex >= 0) {
                //获取当前的ByteBuffer
                ByteBuffer dstBuf = encodeInputBuffers[inputBufIndex];
                //清空ByteBuffer
                dstBuf.clear();
                //填充数据
                dstBuf.put(buf, 0, length);
                //将指定index的input buffer提交给解码器
                audioDecoder.queueInputBuffer(inputBufIndex, 0, length, getPTSUs(), 0);
               // fout.write(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
