package com.jeferry.android.androidradio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onDestroy() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        startMilliTime = System.currentTimeMillis();
        String s = countTime(startMilliTime);
        mTvStartTime.setText("开始时间" + s);
        mTvEndTime.setText("");
        mTvDurationTime.setText("");
    }

    private void stop() {
        try {
            mediaRecorderHelper.stop();
            mediaRecorderHelper.release();
        } catch (Exception e) {
        }
        endMilliTime = System.currentTimeMillis();
        String s = countTime(endMilliTime);
        mTvEndTime.setText("结束时间" + s);
        mTvDurationTime.setText("持续时长" + (endMilliTime - startMilliTime) / 1000 / 60 + "m");
    }
}
