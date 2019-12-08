package com.jeferry.android.androidradio.tmp.audio;

import com.socks.library.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioRecording {
    private static final String TAG = "AudioRecording";
    private File file;
    private OnAudioRecordListener onAudioRecordListener;
    private static final int IO_ERROR = 1;
    private static final int RECORDER_ERROR = 2;
    public static final int FILE_NULL = 3;

    private Thread mRecordingThread;

    private long mStartingTimeMillis = 0;

    public AudioRecording() {
    }

    public void setOnAudioRecordListener(OnAudioRecordListener onAudioRecordListener) {
        this.onAudioRecordListener = onAudioRecordListener;
    }

    public void setFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            file.createNewFile();
        }
        this.file = file;
    }

    public synchronized void startRecording() {
        if (file == null) {
            onAudioRecordListener.onError(FILE_NULL);
            return;
        }
        mStartingTimeMillis = System.currentTimeMillis();
        try {
            if (mRecordingThread != null) stopRecording();
            mRecordingThread = new Thread(new AudioRecordThread(outputStream(file), new AudioRecordThread.OnRecorderFailedListener() {
                @Override
                public void onRecorderFailed() {
                    onAudioRecordListener.onError(RECORDER_ERROR);
                    stopRecording();
                }

                @Override
                public void onRecorderStarted() {
                    onAudioRecordListener.onRecordingStarted();
                }
            }));
            mRecordingThread.setName("AudioRecordingThread");


            mRecordingThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否删除文件
     */
    public synchronized void stopRecording() {

        KLog.d(TAG, "Recording stopped ");

        if (mRecordingThread != null) {

            mRecordingThread.interrupt();
            mRecordingThread = null;


            if (file.length() == 0L) {
                onAudioRecordListener.onError(IO_ERROR);
                return;
            }
            onAudioRecordListener.onRecordFinished(file.getPath());
        }
    }

    private void deleteFile() {
        if (file != null && file.exists())
            KLog.d(TAG, String.format("deleting file success %b ", file.delete()));
    }

    private OutputStream outputStream(File file) {
        if (file == null) {
            throw new RuntimeException("file is null !");
        }
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "could not build OutputStream from" + " this file " + file.getName(), e);
        }
        return outputStream;
    }

    public interface OnAudioRecordListener {

        void onRecordFinished(String filePath);

        void onError(int errorCode);

        void onRecordingStarted();

    }
}
