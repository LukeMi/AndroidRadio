package com.jeferry.android.androidradio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeferry.android.androidradio.sdk.MediaRecorderHelper;

import java.io.IOException;

public class AudioActivity extends AppCompatActivity {

    private Button mBtnStart;
    private Button mBtnStop;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TextView mTvDurationTime;
    private MediaRecorderHelper instance;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
        initData();
    }

    private void initData() {
        instance = MediaRecorderHelper.getInstance();
    }

    private void initView() {
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStop = findViewById(R.id.btn_stop);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mTvDurationTime = findViewById(R.id.tv_duration_time);

        mBtnStart.setOnClickListener(this::onClick);
        mBtnStop.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_stop:
                stop();
                break;
        }
    }

    private void start() {
        try {
            instance.init();
            instance.start();
            startTime = System.currentTimeMillis();
            mTvStartTime.setText("开始时间： " + String.valueOf(startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        instance.stop();
        long l = System.currentTimeMillis();
        mTvEndTime.setText("结束时间： " + String.valueOf(l));
        long duration = l - Long.valueOf(startTime);
        mTvDurationTime.setText("持续时间： " + format(duration));
    }

    private String format(long millsTime) {
        long seconds = millsTime / 1000;
        long hours = seconds / 3600;
        long minutes = seconds % 3600 / 60;
        long leftSeconds = seconds % 3600 % 60;
        return hours + "h " + minutes + "m " + leftSeconds + "s";
    }
}
