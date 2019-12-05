package com.jeferry.android.androidradio.sdk;

import android.media.MediaRecorder;

public enum AudioConfig {

    AAC(44100, MediaRecorder.OutputFormat.AAC_ADTS, MediaRecorder.AudioEncoder.AAC, ".aac"),

    AMR(44100, MediaRecorder.OutputFormat.AMR_WB, MediaRecorder.AudioEncoder.AMR_WB, ".amr");

    public int audioSamplingRate;

    public int outputFormat;

    public int audioEncoder;

    public String suffixName;

    AudioConfig(int audioSamplingRate, int outputFormat, int audioEncoder, String suffixName) {
        this.audioSamplingRate = audioSamplingRate;
        this.outputFormat = outputFormat;
        this.audioEncoder = audioEncoder;
        this.suffixName = suffixName;
    }
}