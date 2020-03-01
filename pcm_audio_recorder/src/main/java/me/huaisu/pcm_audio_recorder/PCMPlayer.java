package me.huaisu.pcm_audio_recorder;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.huaisu.common.App;
import me.huaisu.common.utils.IOUtils;
import me.huaisu.common.utils.LogUtils;

public class PCMPlayer {

    public static final int STATE_PLAYING = 1;
    public static final int STATE_STOP = 2;

    private AudioTrack mAudioTrack;
    private int mState = STATE_STOP;

    public OnPlayStateChangeListener mPlayStateChangeListener;

    public PCMPlayer() {

    }

    public void play(final File pcmFile, int channelConfig) {
        if (isPlaying()) {
            LogUtils.i("is in playing state");
            return;
        }
        if (!pcmFile.exists() || pcmFile.length() == 0) {
            Toast.makeText(App.get(), "PCM文件不存在，" + pcmFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return;
        }
        releaseAudioTrack();
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                PCMConfig.SAMPLE_RATE_IN_HZ,
                channelConfig,
                PCMConfig.AUDIO_FORMAT,
                (int) pcmFile.length(),
                AudioTrack.MODE_STATIC);
        changeState(STATE_PLAYING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(pcmFile);
                    byte[] buffer = new byte[1024 * 1024];
                    int len = 0;
                    while ( (len = fis.read(buffer)) != -1) {
                        mAudioTrack.write(buffer, 0, len);
                    }
                    mAudioTrack.play();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(fis);
                    changeState(STATE_STOP);
                }
            }
        }).start();
    }

    public void play(final File pcmFile) {
        play(pcmFile, PCMConfig.CHANNEL);
    }

    private void changeState(int pendingState) {
        if (mState != pendingState) {
            mState = pendingState;
            if (mPlayStateChangeListener != null) {
                mPlayStateChangeListener.onPlayStateChanged(pendingState);
            }
        }
    }

    public boolean isPlaying() {
        return mState == STATE_PLAYING;
    }

    public void setOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        mPlayStateChangeListener = listener;
    }

    private void releaseAudioTrack() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public void stop() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            changeState(STATE_STOP);
            releaseAudioTrack();
        }
    }

    public interface OnPlayStateChangeListener {

        void onPlayStateChanged(int state);
    }
}
