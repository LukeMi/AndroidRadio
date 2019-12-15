package com.jeferry.android.androidradio.tmp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jeferry.android.androidradio.R;
import com.jeferry.android.androidradio.tmp.audio.AudioRecordManager;
import com.jeferry.android.androidradio.tmp.audio.AudioRecordService;
import com.jeferry.android.androidradio.tmp.audio.AudioRecording;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordActivity extends AppCompatActivity {

    private Button mBtnStart;
    private Button mBtnStop;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TextView mTvDurationTime;
    private AudioRecording mAudioRecording;
    private long startTime;
    private AudioRecordManager instance;

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

        instance = AudioRecordManager.getInstance(getApplicationContext());
        instance.checkUnUploadFiles();
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                startR();
//                startRecording();
                break;
            case R.id.btn_stop:
//                stopRecording();
                stopR();
                break;
        }
    }

    private void startR() {
        instance.setOrderUuid("osacomu");
        instance.bind();
    }

    private void stopR() {
        instance.unBind();

    }

    @Deprecated
    private void startRecording() {
        mTvStartTime.setText("");
        mTvEndTime.setText("");
        mTvDurationTime.setText("");
        AudioRecording.OnAudioRecordListener onRecordListener = new AudioRecording.OnAudioRecordListener() {

            @Override
            public void onRecordFinished(String filePath) {
                File file = new File(filePath);
                long totalSpace = 0;
                if (file.exists()) {
                    totalSpace = file.getTotalSpace();
                    totalSpace = totalSpace / 8 / 1024;

                }
                Log.d(AudioRecordService.TAG, "onFinish " + filePath + " ;totalSpace : " + totalSpace);
            }

            @Override
            public void onError(int e) {
                Log.d(AudioRecordService.TAG, "onError " + e);
            }

            @Override
            public void onRecordingStarted() {
                Log.d(AudioRecordService.TAG, "onStart ");
            }
        };

        String filePath = new File(Environment.getExternalStorageDirectory(), "Recorder") + "/" + System.currentTimeMillis() + ".aac";

        mAudioRecording.setOnAudioRecordListener(onRecordListener);
        try {
            mAudioRecording.setFile(filePath);
            mAudioRecording.startRecording();
            startTime = System.currentTimeMillis();
            mTvStartTime.setText("开始时间： " + formatDate(startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private void stopRecording() {
        if (mAudioRecording != null) {
            mAudioRecording.stopRecording();
            long l = System.currentTimeMillis();
            mTvEndTime.setText("结束时间： " + formatDate(l));
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

    private String formatDate(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(mills));
    }
}
