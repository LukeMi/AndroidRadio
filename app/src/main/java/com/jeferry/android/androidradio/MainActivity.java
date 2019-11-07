package com.jeferry.android.androidradio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private long startMilliTime;

    private long endMilliTime;

    private MediaRecorderHelper mediaRecorderHelper;

    private TextView mTvStartTime;

    private TextView mTvEndTime;

    private TextView mTvDurationTime;

    private boolean mBound;

    private AudioCollectService mService;

    private AudioCollectServiceManager audioCollectServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        audioCollectServiceManager = AudioCollectServiceManager.getInstance(this.getApplicationContext());
    }

    @Override
    protected void onDestroy() {
//        unbindService(connection);
        super.onDestroy();
    }

    private String countTime(long milliTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date(milliTime));
        return format;
    }

    private void initView() {
        findViewById(R.id.btn_start).setOnClickListener(this::onClick);
        findViewById(R.id.btn_stop).setOnClickListener(this::onClick);
        findViewById(R.id.btn_upload).setOnClickListener(this::onClick);
        findViewById(R.id.btn_bind_service).setOnClickListener(this::onClick);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this::onClick);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mTvDurationTime = findViewById(R.id.tv_duration_time);
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                checkPermission();
                break;
            case R.id.btn_stop:
                stop();
                break;
            case R.id.btn_upload:
                break;
            case R.id.btn_bind_service:
                audioCollectServiceManager.bind();
                break;
            case R.id.btn_unbind_service:
                audioCollectServiceManager.unBind();
                break;
            default:
                break;
        }
    }


    private void checkPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};
        ArrayList<String> denyPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                denyPermissions.add(permission);
            }
        }
        if (denyPermissions.isEmpty()) {
            start();
            return;
        }
        boolean needRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRationale = true;
                break;
            }
        }
        if (needRationale) {

        }
        ActivityCompat.requestPermissions(this, permissions, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("requestCode " + requestCode + "permissions : " + permissions);
        boolean allGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
                break;
            }
        }
        if (allGranted) {
            start();
        }
    }

    private void start() {
        new Thread(() -> {
            try {
                mediaRecorderHelper = MediaRecorderHelper.getInstance();
                mediaRecorderHelper.init();
                mediaRecorderHelper.start();
                new Handler(Looper.getMainLooper()).post(() -> {
                    startMilliTime = System.currentTimeMillis();
                    String s = countTime(startMilliTime);
                    mTvStartTime.setText("开始时间" + s);
                    mTvEndTime.setText("");
                    mTvDurationTime.setText("");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stop() {
        try {
            mediaRecorderHelper.reset();
            mediaRecorderHelper.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        endMilliTime = System.currentTimeMillis();
        String s = countTime(endMilliTime);
        long seconds = (endMilliTime - startMilliTime) / 1000;
        mTvEndTime.setText("结束时间" + s);
        mTvDurationTime.setText("持续时长" + seconds / 60 + "m" + " ； = " + seconds + " s");
    }
}
