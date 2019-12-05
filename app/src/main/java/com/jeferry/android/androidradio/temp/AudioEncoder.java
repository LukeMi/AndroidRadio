package com.jeferry.android.androidradio.temp;

import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioEncoder implements AudioCodec {


    //    private Client mClient;
    private Worker mWorker;
    private final String TAG = "AudioEncoder";
    private byte[] mFrameByte;

    //    public AudioEncoder(Client client) {
//        mClient=client;
//    }
    public void start() {
        if (mWorker == null) {
            mWorker = new Worker();
            mWorker. setRunning(true);
            mWorker.start();
        }

    }

    public void stop() {
        if (mWorker != null) {
            mWorker.setRunning(false);
            mWorker = null;
        }
//        if(!mClient.hasRelease()){
//            mClient.release();
//        }
    }


    private class Worker extends Thread {
        private final int mFrameSize = 2048;
        private byte[] mBuffer;
        private boolean isRunning = false;
        private MediaCodec mEncoder;
        private AudioRecord mRecord;
        MediaCodec.BufferInfo mBufferInfo;

        @Override
        public void run() {
            if (!prepare()) {
                Log.d(TAG, "音频编码器初始化失败");
                isRunning = false;
            }
            while (isRunning) {
                int num = mRecord.read(mBuffer, 0, mFrameSize);
                Log.d(TAG, "buffer = " + mBuffer.toString() + ", num = " + num);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    encode(mBuffer);
                }
            }
            release();
        }

        public void setRunning(boolean run) {
            isRunning = run;
        }

        /**
         * 释放资源
         */
        private void release() {
            if (mEncoder != null) {
                mEncoder.stop();
                mEncoder.release();
            }
            if (mRecord != null) {
                mRecord.stop();
                mRecord.release();
                mRecord = null;
            }
        }

        /**
         * 连接服务端，编码器配置
         *
         * @return true配置成功，false配置失败
         */
        private boolean prepare() {
            try {
//                mClient.connectToServer();
                mBufferInfo = new MediaCodec.BufferInfo();
                mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
                MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                        KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
                mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                        KEY_AAC_PROFILE);
                mEncoder.configure(mediaFormat, null, null,
                        MediaCodec.CONFIGURE_FLAG_ENCODE);
                mEncoder.start();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            mBuffer = new byte[mFrameSize];
            int minBufferSize = AudioRecord.getMinBufferSize(KEY_SAMPLE_RATE, CHANNEL_MODE,
                    AUDIO_FORMAT);
            mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    KEY_SAMPLE_RATE, CHANNEL_MODE, AUDIO_FORMAT, minBufferSize * 2);
            mRecord.startRecording();
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void encode(byte[] data) {
            int inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mEncoder.getInputBuffer(inputBufferIndex);
                inputBuffer.clear();
                inputBuffer.put(data);
                inputBuffer.limit(data.length);
                mEncoder.queueInputBuffer(inputBufferIndex, 0, data.length,
                        System.nanoTime(), 0);
            }

            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                //给adts头字段空出7的字节
                int length = mBufferInfo.size + 7;
                if (mFrameByte == null || mFrameByte.length < length) {
                    mFrameByte = new byte[length];
                }
                addADTStoPacket(mFrameByte, length);
                outputBuffer.get(mFrameByte, 7, mBufferInfo.size);
//                boolean isSusscess1=mClient.sendInt(length);
//                boolean isSusscess2=mClient.send(mFrameByte,0,length);
//                if(!(isSusscess1&&isSusscess2)){
//                    isRunning=false;
//                    mClient.release();
            }
            mEncoder.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
        }
    }

    /**
     * 给编码出的aac裸流添加adts头字段
     *
     * @param packet    要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}

