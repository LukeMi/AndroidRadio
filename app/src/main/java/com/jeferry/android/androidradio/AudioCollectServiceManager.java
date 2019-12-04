package com.jeferry.android.androidradio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.jeferry.android.androidradio.sdk.MediaRecorderHelper;

public class AudioCollectServiceManager {

    private static AudioCollectServiceManager audioCollectServiceManager;

    private Context context;

    private boolean mBound;

    private AudioCollectService mAudioCollectService;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBound = true;
            if (service instanceof AudioCollectService) {
                mAudioCollectService = (AudioCollectService) service;
                mAudioCollectService.setOnRecordListener(mOnRecordListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private MediaRecorderHelper.OnRecordListener mOnRecordListener;

    private AudioCollectServiceManager(Context context) {
        this.context = context;
    }

    public static AudioCollectServiceManager getInstance(Context context) {
        if (audioCollectServiceManager == null) {
            synchronized (AudioCollectServiceManager.class) {
                if (audioCollectServiceManager == null) {
                    audioCollectServiceManager = new AudioCollectServiceManager(context);
                }
            }
        }
        return audioCollectServiceManager;
    }

    /**
     * 绑定服务
     */
    public void bind() {
        Intent service = new Intent(context.getApplicationContext(), AudioCollectService.class);
        context.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unBind() {
        context.unbindService(connection);
    }


    /**
     * 开始录音
     */
    public void startRecord() {
        if (mAudioCollectService != null) {
            mAudioCollectService.startRecording(" hhhh");
        }
    }

    public void setOnRecordListener(MediaRecorderHelper.OnRecordListener listener){
        mOnRecordListener = listener;
    }


    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mAudioCollectService != null) {
            mAudioCollectService.stopRecording();
        }
    }

    /**
     * 重置录音
     */
    public void resetRecord() {

    }
}
