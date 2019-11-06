package com.jeferry.android.androidradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Random;

/**
 * @author chenmz
 * @date 2019-11-06 11:40
 * @des 录音前台服务
 * @mail chenmingzhi@ccclubs.com
 */
public class RadioCollectService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * 间隔时间8分钟上传一次录音
     */
    private final int INTERVAL_TIME = 8 * 60;

    // Binder given to clients
    private final IBinder binder = new Binder();

    // Random number generator
    private final Random mGenerator = new Random();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))// 设置下拉列表中的图标(大图标)
                .setContentTitle("行程保护") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("送乘全程录音中...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
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

    }

    /**
     * 释放录音
     */
    public void releaseRecording() {

    }
}
