package com.jeferry.android.androidradio.tmp.audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.File;

public class AudioRecordManager {
    public static final String TAG = AudioRecordManager.class.getSimpleName();
    private static AudioRecordManager audioRecordManager;

    private Context context;

    private String orderUuid;

    private AudioRecordService mAudioRecordService;

    private AudioRecording.OnAudioRecordListener mOnRecordListener = new AudioRecording.OnAudioRecordListener() {
        @Override
        public void onRecordFinished(String filePath) {
            upLoad(filePath);
            KLog.d(TAG, "onRecordFinished");
        }

        @Override
        public void onError(int errorCode) {
            KLog.d(TAG, "onError");
        }

        @Override
        public void onRecordingStarted() {
            KLog.d(TAG, "onRecordingStarted");
        }
    };


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            KLog.d(AudioRecordService.TAG, "onServiceConnected " + className);

            if (service instanceof AudioRecordService.MBinder) {
                mAudioRecordService = ((AudioRecordService.MBinder) service).getService();
                mAudioRecordService.setOnAudioRecordListener(mOnRecordListener);
                if (mAudioRecordService != null) {
                    KLog.d(TAG, "mAudioRecordService != null");
                    mAudioRecordService.startRecording();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            KLog.d(AudioRecordService.TAG, "onServiceDisconnected " + arg0);
        }
    };


    private AudioRecordManager(Context context) {
        this.context = context;

    }


    /**
     * 上传文件
     *
     * @param filePath 文件路径
     */
    private void upLoad(final String filePath) {
        Toast.makeText(context.getApplicationContext(), "上传文件中：", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            File file = new File(filePath);
            if (file.exists()) {
                try {
//                    boolean delete = file.delete();
//                    Toast.makeText(context.getApplicationContext(), delete ? "上传文件成功：" : "上传文件失败", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                } finally {

                }

            }
        }, 3000);
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

    public void bind() {
        Intent service = new Intent(context, AudioRecordService.class);
        service.putExtra(AudioRecordService.EXTRA_ORDER_UUID, orderUuid);
        context.bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    public void startRecord() {
        if (mAudioRecordService != null) {
            mAudioRecordService.startRecording();
        }
    }

    public void stopRecord() {

    }

    public void unBind() {
        context.unbindService(connection);
    }

    public void checkUnUploadFiles() {
        new Thread(() -> {
            try {
                String path = context.getExternalCacheDir().getParentFile().getPath() + File.separator + "audio";
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    File[] list = file.listFiles();
                    deleteOrderAudio(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void deleteOrderAudio(File[] list) throws Exception {
        if (list != null && list.length > 0) {
            for (int i = 0; i < list.length; i++) {
                File file = list[i];
                KLog.d(TAG, "childDirPath: " + file.getPath());
                if (file.exists() && file.isDirectory()) {
                    File[] childList = file.listFiles();
                    if (childList != null && childList.length > 0) {
                        for (int i1 = 0; i1 < childList.length; i1++) {
                            File audioFile = childList[i1];
                            boolean delete = audioFile.delete();
                            KLog.d(TAG, audioFile.getPath() + " ;delete : " + delete);
                        }
                    }
                }
            }
        }
    }
}
