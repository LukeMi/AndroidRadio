package com.jeferry.android.androidradio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeferry.android.androidradio.tmp.AudioRecording;

import java.io.File;
import java.io.IOException;

public class AudioRecordActivity extends AppCompatActivity {

    private Button mBtnStart;
    private Button mBtnStop;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TextView mTvDurationTime;
    private AudioRecording mAudioRecording;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        mAudioRecording = new AudioRecording();

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
                startRecording();
                break;
            case R.id.btn_stop:
                stopRecording();
                break;
        }
    }

    private void startRecording() {

        AudioRecording.OnAudioRecordListener onRecordListener = new AudioRecording.OnAudioRecordListener() {

            @Override
            public void onRecordFinished() {
                Log.d("MAIN", "onFinish ");
            }

            @Override
            public void onError(int e) {
                Log.d("MAIN", "onError " + e);
            }

            @Override
            public void onRecordingStarted() {
                Log.d("MAIN", "onStart ");

            }
        };

        String filePath = new File(Environment.getExternalStorageDirectory(), "Recorder") + "/" + System.currentTimeMillis() + ".aac";

        mAudioRecording.setOnAudioRecordListener(onRecordListener);
        try {
            mAudioRecording.setFile(filePath);
            mAudioRecording.startRecording();
            startTime = System.currentTimeMillis();
            mTvStartTime.setText("开始时间： " + String.valueOf(startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mAudioRecording != null) {
            mAudioRecording.stopRecording(false);
            long l = System.currentTimeMillis();
            mTvEndTime.setText("结束时间： " + String.valueOf(l));
            long duration = l - Long.valueOf(startTime);
            mTvDurationTime.setText("持续时间： " + format(duration));
        }
    }

    private String format(long millsTime) {
        long seconds = millsTime / 1000;
        long hours = seconds / 3600;
        long minutes = seconds % 3600 / 60;
        long leftSeconds = seconds % 3600 % 60;
        return hours + "h " + minutes + "m " + leftSeconds + "s";
    }
}
