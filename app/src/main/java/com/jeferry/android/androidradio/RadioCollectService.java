package com.jeferry.android.androidradio;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * @author chenmz
 * @date 2019-11-06 11:40
 * @des 录音前台服务
 * @mail chenmingzhi@ccclubs.com
 */
public class RadioCollectService extends Service {

    /**
     * 间隔时间8分钟上传一次录音
     */
    private final int INTERVAL_TIME = 8 * 60;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }


}
