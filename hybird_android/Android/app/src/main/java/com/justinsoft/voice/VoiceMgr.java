package com.justinsoft.voice;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.justinsoft.util.LogUtil;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

/**
 * 音量管理
 */
public final class VoiceMgr
{
    private static final String TAG = LogUtil.getClassTag(VoiceMgr.class);
    
    // 最大录音时长30秒;
    private static final int MAX_LENGTH = 1000 * 30;
    
    // 单例锁
    private static final Lock LOCK = new ReentrantLock();
    
    // 是否录音中
    private static final AtomicBoolean STARTING = new AtomicBoolean(false);
    
    // 分贝管理实例（单例）
    private static VoiceMgr instance;
    
    // Android内置录音对象
    private MediaRecorder recorder;
    
    // 录音的存储文件
    private String filePath;
    
    // 录音开始时间
    private long startTime;
    
    // 最大振幅
    private double max;
    
    /**
     * 私有化构造方法，避免在外部被实例化
     */
    private VoiceMgr()
    {
        filePath = Environment.getExternalStorageDirectory() + "/bt_voice_temp.amr";
        Log.i(TAG, "bt voice path:" + filePath);
    }
    
    /**
     * 获取单例
     *
     * @return
     */
    public static VoiceMgr getInstance()
    {
        if (null == instance)
        {
            LOCK.tryLock();
            try
            {
                if (null == instance)
                {
                    instance = new VoiceMgr();
                }
                return instance;
            }
            catch (Exception e)
            {
                Log.e(TAG, "failed to get voice instance.", e);
            }
            finally
            {
                LOCK.unlock();
            }
        }
        
        return instance;
    }
    
    /**
     * 开始测试
     */
    public void startVoice()
    {
        if (STARTING.compareAndSet(false, true))
        {
            if (null == recorder)
            {
                recorder = new MediaRecorder();
                File file = new File(filePath);
                if(file.exists()){
                    file.deleteOnExit();
                }
            }
            try
            {
                // 设置麦克风
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样
                recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                /*
                 * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
                 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
                 */
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                
                recorder.setOutputFile(filePath);
                recorder.setMaxDuration(MAX_LENGTH);
                recorder.prepare();
                /* ④开始 */
                recorder.start();
                // AudioRecord audioRecord.
                /* 获取开始时间* */
                startTime = System.currentTimeMillis();
                updateMicStatus();
                Log.i(TAG, "record start time:" + startTime);
            }
            catch (IllegalStateException e)
            {
                Log.i(TAG, "start voice failed!" + e.getMessage());
            }
            catch (IOException e)
            {
                Log.i(TAG, "start voice failed!" + e.getMessage());
            }
        }
        else
        {
            Log.i(TAG, "It's recording now.");
        }
    }
    
    /**
     * 停止测试
     */
    public int stopVoice()
    {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
        long endTime = System.currentTimeMillis();
        Log.i(TAG, "Time cost:" + (endTime - startTime) / 1000 + " sec.");
        
        int maxDB = new Double(20 * Math.log10(max)).intValue();
        Log.d(TAG, "maxAmplitude:" + max + ",max dB:" + maxDB);
        STARTING.compareAndSet(true, false);
        return maxDB;
    }
    
    /**
     * 更新最大振幅
     */
    private void updateMicStatus()
    {
        if (recorder != null)
        {
            double maxAmplitude = recorder.getMaxAmplitude();
            // double ratio = maxAmplitude / BASE;
            // 分贝
            // double db = 0;
            if (max < maxAmplitude)
            {
                max = maxAmplitude;
            }
            // 不需要使用平均振幅
            // if (ratio > 1)
            // {
            // db = 20 * Math.log10(ratio);
            // }
            // Log.d(TAG, "分贝值：" + db + ",最大振幅：" + maxAmplitude + ",平均振幅：" + ratio);
            if (startTime + MAX_LENGTH > System.currentTimeMillis())
            {
                handler.postDelayed(dBRunnable, SPACE);
            }
            else
            {
                Log.i(TAG, "reach the max record time.");
                stopVoice();
            }
        }
    }
    
    /**
     * 更新分贝的线程
     */
    private Runnable dBRunnable = new Runnable()
    {
        public void run()
        {
            updateMicStatus();
        }
    };
    
    // // 取样振幅的基数
    // private static final int BASE = 1;
    
    // 间隔取样时间
    private static final int SPACE = 10;
    
    private final Handler handler = new Handler();
}
