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

    private MediaRecorderHelper() {
        mediaRecorder = new MediaRecorder();
    }

    public static MediaRecorderHelper getInstance() {
        if (mediaRecorderHelper != null) {
            return mediaRecorderHelper;
        }
        if (mediaRecorderHelper == null) {
            synchronized (MediaRecorderHelper.class) {
                mediaRecorderHelper = new MediaRecorderHelper();
            }
        }
        return mediaRecorderHelper;
    }

    public void init() throws IOException {
        mediaRecorder.reset();
        // 设置音频来源(一般为麦克风)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置单声道
        mediaRecorder.setAudioChannels(1);
        // 设置音频输出格式（默认的输出格式）
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        // 设置比特率
//        mediaRecorder.setAudioEncodingBitRate(16_000);
        // 设置采样率
        mediaRecorder.setAudioSamplingRate(44100);
        // 设置音频编码方式（默认的编码方式）
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        // 创建一个临时的音频输出文件.record_是文件的前缀名 .amr是后缀名
        audioFile = File.createTempFile("record_qqq", ".amr", Environment.getExternalStorageDirectory());
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

        });
    }

    public void start() throws IllegalStateException {
        mediaRecorder.start();
    }

    public void stop() throws IllegalStateException {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
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
}
