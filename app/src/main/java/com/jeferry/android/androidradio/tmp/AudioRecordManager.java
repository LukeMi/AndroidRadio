package com.jeferry.android.androidradio.tmp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.jeferry.android.androidradio.AudioCollectService;

public class AudioRecordManager {
    public static final String TAG = AudioRecordManager.class.getSimpleName();
    private static AudioRecordManager audioRecordManager;

    private Context context;

    private String orderUuid;

    private AudioRecordService mAudioRecordService;

    private boolean mBound;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected " + className);
            mBound = true;
            if (service instanceof AudioRecordService) {
                mAudioRecordService = (AudioRecordService) service;
                mAudioRecordService.setOnAudioRecordListener(mOnRecordListener);
                if (mAudioRecordService != null) {
                    mAudioRecordService.startRecording();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected " + arg0);
            mBound = false;
        }
    };


    private AudioRecordManager(Context context) {
        this.context = context;

    }

    private AudioRecording.OnAudioRecordListener mOnRecordListener = new AudioRecording.OnAudioRecordListener() {
        @Override
        public void onRecordFinished(String filePath) {
            upLoad(filePath);
            Log.d(TAG, "onRecordFinished");
        }

        @Override
        public void onError(int errorCode) {
            Log.d(TAG, "onError");
        }

        @Override
        public void onRecordingStarted() {
            Log.d(TAG, "onRecordingStarted");
        }
    };

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     */
    private void upLoad(String filePath) {
        Toast.makeText(context.getApplicationContext(), "上传文件：" + filePath, Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    private void deleteFile(String filePath) {
        Toast.makeText(context.getApplicationContext(), "上传文件：" + filePath, Toast.LENGTH_SHORT).show();
    }


    public static AudioRecordManager getInstance(Context context) {
        if (audioRecordManager == null) {
            synchronized (AudioRecordManager.class) {
                if (audioRecordManager == null) {
                    audioRecordManager = new AudioRecordManager(context);
                }
            }
        }
        return audioRecordManager;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public void startRecord() {
        Intent service = new Intent(context, AudioRecordService.class);
        context.bindService(service, connection, Context.BIND_AUTO_CREATE);

    }

    public void stopRecord() {
        context.unbindService(connection);
    }
}
