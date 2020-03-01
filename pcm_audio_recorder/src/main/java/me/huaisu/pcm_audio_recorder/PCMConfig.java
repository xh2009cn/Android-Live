package me.huaisu.pcm_audio_recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Environment;

import java.io.File;

public class PCMConfig {

    public static final int SAMPLE_RATE_IN_HZ = 44100;//采样率44.1kHz
    public static final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;//双声道（左右声道）
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;//每个采样点16bit，即2字节
    public static final int SAMPLE_BYTE_COUNT = 2;

    public static String createOutputFilePath(Context context) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "huaisu/pcm/" + System.currentTimeMillis() + ".pcm");
        outputFile.getParentFile().mkdirs();
        return outputFile.getAbsolutePath();
    }

    public static int calcSampleByteCount(int audioFormat) {
        return audioFormat == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1;
    }
}
