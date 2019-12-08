package com.jeferry.android.androidradio.tmp.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.jeferry.android.androidradio.R;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AudioRecordService extends Service {

    public static final String TAG = AudioRecordService.class.getSimpleName() + "....>>";

    public static final String EXTRA_ORDER_UUID = "extra_order_uuid";

    private final String CHANNEL_ONE_ID = this.getClass().getSimpleName();

    private final String CHANNEL_ONE_NAME = this.getClass().getSimpleName();
    /**
     * 间隔时间8分钟上传一次录音
     */
    private final int INTERVAL_TIME_SECOND_UNIT = 3 * 1;

    private Disposable subscribe;

    private AudioRecording mAudioRecording;

    private String orderUuid;

    private AudioRecording.OnAudioRecordListener onAudioRecordListener;

    public AudioRecordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        KLog.d(TAG, "onBind");
        orderUuid = intent.getStringExtra(EXTRA_ORDER_UUID);
        mAudioRecording = new AudioRecording();
        notification();
        return new MBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG, "onUnbind");
        if (subscribe != null) {
            subscribe.dispose();
        }
        stopRecording();
        return super.onUnbind(intent);
    }

    public void interval() {
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        subscribe = Observable.interval(INTERVAL_TIME_SECOND_UNIT, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    reStartRecord();
                });
    }

    private void reStartRecord() {
        stopRecording();
        startRecording();
    }

    private void notification() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
//        Intent nfIntent = new Intent(this, MainActivity.class);
        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, null, 0))
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
    }

    public void setOnAudioRecordListener(AudioRecording.OnAudioRecordListener onAudioRecordListener) {
        this.onAudioRecordListener = onAudioRecordListener;
    }

    public void startRecording() {
        KLog.d(TAG, "startRecording");
        stopRecording();
        String filePath = generatePath();
        KLog.d(TAG, filePath);
        mAudioRecording.setOnAudioRecordListener(onAudioRecordListener);
        try {
            mAudioRecording.setFile(filePath);
            mAudioRecording.startRecording();
            interval();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generatePath() {
        long l = System.currentTimeMillis();
        String name = orderUuid + "-" + l + ".aac";
        String path = getExternalCacheDir().getParentFile().getPath() + File.separator + "audio" + File.separator + orderUuid + File.separator + name;
        return path;
    }

    private void stopRecording() {
        if (mAudioRecording != null) {
            mAudioRecording.stopRecording();
        }
    }

    public class MBinder extends Binder {
        public AudioRecordService getService() {
            return AudioRecordService.this;
        }
    }

}
