package com.jeferry.android.androidradio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

/**
 * 音视频记录类别
 */
public class AVActivity extends AppCompatActivity {
    private Button mBtnAvStart;
    private Button mBtnAvStop;
    private Button mBtnAvUpload;
    private SurfaceView mSv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_av);
        initView();
    }

    private void initView() {
        mBtnAvStart = findViewById(R.id.btn_av_start);
        mBtnAvStop = findViewById(R.id.btn_av_stop);
        mBtnAvUpload = findViewById(R.id.btn_av_upload);
        mSv = findViewById(R.id.sv);
        mBtnAvStart.setOnClickListener(this::onClick);
        mBtnAvStop.setOnClickListener(this::onClick);
        mBtnAvUpload.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_av_start:
                break;
            case R.id.btn_av_stop:
                break;
            case R.id.btn_av_upload:
                break;
            default:
                break;
        }
    }

    private void start() {

    }
}
