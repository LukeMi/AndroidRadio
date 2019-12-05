package com.jeferry.android.androidradio.sdk;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @author chenmz
 * @date 2019-11-05 09:49
 * @des
 * @mail chenmingzhi@ccclubs.com
 */
public class MediaRecorderHelper {

    private static MediaRecorderHelper mediaRecorderHelper;

    private MediaRecorder mediaRecorder;

    private File audioFile;

    private OnRecordListener mOnRecordListener;

    private MediaRecorderHelper() {
        mediaRecorder = new MediaRecorder();
    }

    private String orderUuid;

    public static MediaRecorderHelper getInstance() {
        if (mediaRecorderHelper != null) {
            return mediaRecorderHelper;
        }
        if (mediaRecorderHelper == null) {
            synchronized (MediaRecorderHelper.class) {
                if (mediaRecorderHelper == null) {
                    mediaRecorderHelper = new MediaRecorderHelper();
                }
            }
        }
        return mediaRecorderHelper;
    }

    public void init() throws IOException {
        mediaRecorder.reset();
        // 设置音频来源(一般为麦克风)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置单声道
        mediaRecorder.setAudioChannels(2);
        // 设置音频输出格式（默认的输出格式）
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        // 设置比特率
//        mediaRecorder.setAudioEncodingBitRate(16_000);
        // 设置采样率
        mediaRecorder.setAudioSamplingRate(44100);
        // 设置音频编码方式（默认的编码方式）
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 创建一个临时的音频输出文件.record_是文件的前缀名 .amr是后缀名
        audioFile = File.createTempFile("record_qqq", ".aac", Environment.getExternalStorageDirectory());
//        audioFile = new File(Environment.getExternalStorageDirectory()+File.separator+"record_qqq"+System.currentTimeMillis()+".aac");
//        if (!audioFile.exists()){
//            audioFile.getParentFile().mkdirs();
//        }
//        audioFile.createNewFile();
        // audioFile =new  File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/sound.amr");
        // 设置录制器的文件保留路径
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.setOnInfoListener((mediaRecorder, i, i1) -> {

        });
        mediaRecorder.setOnErrorListener((mediaRecorder, i, i1) -> {
            if (mOnRecordListener != null) {
                mOnRecordListener.onRecordError();
            }
        });
    }

    public void start() throws IllegalStateException {
        mediaRecorder.start();
        if (mOnRecordListener != null) {
            mOnRecordListener.onStartRecord();
        }
    }

    public void stop() throws IllegalStateException {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            if (mOnRecordListener != null) {
                mOnRecordListener.onStopRecord();
            }
        }
    }

    public void reset() throws IllegalStateException {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
        }
    }

    public void release() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
    }

    public void setOnRecordListener(OnRecordListener listener) {
        mOnRecordListener = listener;
    }

    public interface OnRecordListener {

        /**
         * 初始化录音失败
         */
        void onInitRecordError();

        /**
         * 开始录音失败
         */
        void onStartRecordError();

        /**
         * 录音失败
         */
        void onRecordError();

        /**
         * 开始录音
         */
        void onStartRecord();

        /**
         * 停止录音
         */
        void onStopRecord();
    }
}
