package me.huaisu.pcm_audio_recorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import me.huaisu.common.utils.IOUtils;
import me.huaisu.common.utils.LogUtils;

public class PCMRecorder {

    public static final String TAG = "PCMRecorder";

    public static final int STATE_IDLE = 0;
    public static final int STATE_RECORDING = 1;
    public static final int STATE_STOP = 2;

    private int mState;
    private String mOutputPath;
    private final AudioRecord mAudioRecord;
    private final int mBufferSize;
    private List<OnRecordStateChangeListener> mRecordStateChangeListeners = new LinkedList<>();
    private boolean mRecording;

    public PCMRecorder() {
        mBufferSize = AudioRecord.getMinBufferSize(PCMConfig.SAMPLE_RATE_IN_HZ
                , PCMConfig.CHANNEL
                , PCMConfig.AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, PCMConfig.SAMPLE_RATE_IN_HZ,
                PCMConfig.CHANNEL, PCMConfig.AUDIO_FORMAT, mBufferSize);
    }

    public void addOnRecordStateChangeListener(OnRecordStateChangeListener listener) {
        mRecordStateChangeListeners.add(listener);
    }

    public void setOutputPath(String outputPath) {
        this.mOutputPath = outputPath;
    }

    public String getOutputFilePath() {
        return mOutputPath;
    }

    public File getOutputFile() {
        if (mOutputPath == null) {
            return null;
        }
        return new File(mOutputPath);
    }

    public int getState() {
        return mAudioRecord.getState();
    }

    public boolean isRecording() {
        return mState == STATE_RECORDING;
    }

    public void startRecord() {
        if (isRecording()) {
            LogUtils.i("is in recording state");
            return;
        }
        if (TextUtils.isEmpty(mOutputPath)) {
            LogUtils.i("output path null");
            return;
        }
        notifyRecordStateChange(STATE_RECORDING);
        LogUtils.i("start recording PCM");
        mRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mOutputPath);
                    mAudioRecord.startRecording();
                    byte[] byteBuffer = new byte[mBufferSize];
                    while (mRecording) {
                        int end = mAudioRecord.read(byteBuffer, 0, byteBuffer.length);
                        fos.write(byteBuffer, 0, end);
                    }
                    fos.flush();
                    mAudioRecord.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(fos);
                    notifyRecordStateChange(STATE_STOP);
                    mRecording = false;
                }
            }
        }).start();
    }

    private void notifyRecordStateChange(int state) {
        mState = state;
        for (OnRecordStateChangeListener listener : mRecordStateChangeListeners) {
            listener.onRecordStateChanged(state);
        }
    }

    public void stopRecord() {
        mRecording = false;
    }

    public interface OnRecordStateChangeListener {

        void onRecordStateChanged(int state);
    }
}
