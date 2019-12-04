package com.jeferry.android.androidradio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jeferry.android.androidradio.sdk.MediaRecorderHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author chenmz
 * @date 2019-11-06 11:40
 * @des 录音前台服务
 * @mail chenmingzhi@ccclubs.com
 */
public class AudioCollectService extends Service {

    private final String CHANNEL_ONE_ID = this.getClass().getSimpleName();
    private final String CHANNEL_ONE_NAME = this.getClass().getSimpleName();
    private final String TAG = this.getClass().getSimpleName();

    /**
     * 间隔时间8分钟上传一次录音
     */
    private final int INTERVAL_TIME_SECOND_UNIT = 8 * 1;

    // Binder given to clients
    private final AudioCollectBinder binder = new AudioCollectBinder();

    /**
     * 全局管理类
     */
    private MediaRecorderHelper mediaRecorderHelper;
    private Disposable subscribe;
    private MediaRecorderHelper.OnRecordListener mOnRecordListener;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        startService();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void startService() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))// 设置下拉列表中的图标(大图标)
                .setContentTitle("行程保护") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("送乘全程录音中...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(1110, notification);
        interval();
        startRecording("");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        // 停止前台服务--参数：表示是否移除之前的通知
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    /**
     * 开始录音
     */
    public void startRecording(String orderUuid) {
        new Thread(() -> {
            try {
                mediaRecorderHelper = MediaRecorderHelper.getInstance();
                mediaRecorderHelper.setOnRecordListener(mOnRecordListener);
                mediaRecorderHelper.init();
                mediaRecorderHelper.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 重置录音
     */
    public void resetRecording(String orderUuid) {

    }

    /**
     * 停止录音
     */
    public void stopRecording() {
        try {
            if (mediaRecorderHelper != null) {
                mediaRecorderHelper.reset();
                mediaRecorderHelper.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放录音
     */
    public void releaseRecording() {
        if (mediaRecorderHelper != null) {
            mediaRecorderHelper.stop();
            try {
                mediaRecorderHelper.init();
                mediaRecorderHelper.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadAudio("");
    }

    /**
     * 上传文件
     *
     * @param path
     */
    private void uploadAudio(String path) {
        Log.d(TAG, "uploadAudio");
    }

    public AudioCollectService getService() {
        return this;
    }

    public static class AudioCollectBinder extends Binder {

        /**
         * 开始录音
         */
        public void startRecord() {

        }

        /**
         * 停止录音
         */
        public void stopRecord() {

        }

        /**
         * 上传文件
         */
        public void upload() {

        }
    }

    public void interval() {
        subscribe = Observable.interval(INTERVAL_TIME_SECOND_UNIT, TimeUnit.MINUTES)
                .subscribe(aLong -> {
                    releaseRecording();
                });
    }

    public void setOnRecordListener(MediaRecorderHelper.OnRecordListener listener){
        mOnRecordListener = listener;
    }
}
