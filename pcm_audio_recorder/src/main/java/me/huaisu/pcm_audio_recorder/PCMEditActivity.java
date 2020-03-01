package me.huaisu.pcm_audio_recorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import me.huaisu.common.utils.UIWorker;

public class PCMEditActivity extends AppCompatActivity implements PCMRecorder.OnRecordStateChangeListener, PCMPlayer.OnPlayStateChangeListener {

    private Button mBtnRecord;
    private PCMRecorder mPCMRecorder;

    private Button mBtnPlay;
    private PCMPlayer mPCMPlayer;

    private Button mBtnPlayHalf;
    private Button mBtnPlayLeft;
    private Button mBtnPlayRight;
    private Button mBtnPlayDouble;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcm);

        mBtnRecord = findViewById(R.id.btn_record_pcm);
        refreshRecordState();
        getPCMRecorder().addOnRecordStateChangeListener(this);

        mBtnPlay = findViewById(R.id.btn_play_pcm);
        mBtnPlayHalf = findViewById(R.id.btn_play_half_pcm);
        mBtnPlayLeft = findViewById(R.id.btn_play_left_channel);
        mBtnPlayRight = findViewById(R.id.btn_play_right_channel);
        mBtnPlayDouble = findViewById(R.id.btn_play_double);
        refreshPlayState();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    private void refreshRecordState() {
        UIWorker.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnRecord.setText(getPCMRecorder().isRecording() ? "停止录制PCM" : "开始录制PCM");
            }
        });
    }

    private PCMRecorder getPCMRecorder() {
        if (mPCMRecorder == null) {
            mPCMRecorder = new PCMRecorder();
        }
        return mPCMRecorder;
    }

    public void recordPCM(View view) {
        if (getPCMRecorder().isRecording()) {
            getPCMRecorder().stopRecord();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请先授予录音权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请先授予存储权限", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
            String outputPath = PCMConfig.createOutputFilePath(this);
            getPCMRecorder().setOutputPath(outputPath);
            getPCMRecorder().startRecord();
        }
    }

    private PCMPlayer getPCMPlayer() {
        if (mPCMPlayer == null) {
            mPCMPlayer = new PCMPlayer();
        }
        return mPCMPlayer;
    }

    private void refreshPlayState() {
        UIWorker.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBtnPlay.setText(getPCMPlayer().isPlaying() ? "停止播放" : "播放原始pcm");
                mBtnPlayHalf.setText(getPCMPlayer().isPlaying() ? "停止播放" : "播放音量减半pcm");
                mBtnPlayLeft.setText(getPCMPlayer().isPlaying() ? "停止播放" : "播放左声道");
                mBtnPlayRight.setText(getPCMPlayer().isPlaying() ? "停止播放" : "播放右声道");
                mBtnPlayDouble.setText(getPCMPlayer().isPlaying() ? "停止播放" : "倍速播放");
            }
        });
    }

    @Override
    public void onRecordStateChanged(int state) {
        refreshRecordState();
    }

    public void playPCM(View view) {
        if (getPCMPlayer().isPlaying()) {
            getPCMPlayer().stop();
        } else {
            File pcmFile = getPCMRecorder().getOutputFile();
            if (!checkPcmFile(pcmFile)) return;
            getPCMPlayer().play(pcmFile);
        }
    }

    @Override
    public void onPlayStateChanged(int state) {
        refreshPlayState();
    }

    public void halfVolume(final View view) {
        final File pcmFile = getPCMRecorder().getOutputFile();
        if (!checkPcmFile(pcmFile)) {
            return;
        }
        view.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean success = PCMEditor.halfVolume(pcmFile);
                UIWorker.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PCMEditActivity.this, success ? "已将pcm文件音量减半，播放一下听听效果吧" : "转换失败", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void splitChannel(final View view) {
        final File pcmFile = getPCMRecorder().getOutputFile();
        if (!checkPcmFile(pcmFile)) {
            return;
        }
        view.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean success = PCMEditor.splitChannel(pcmFile);
                UIWorker.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PCMEditActivity.this, success ? "已将pcm文件左右声道分离，播放一下听听效果吧" : "转换失败", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void playHalfPCM(View view) {
        if (getPCMPlayer().isPlaying()) {
            getPCMPlayer().stop();
        } else {
            File pcmFile = getPCMRecorder().getOutputFile();
            if (!checkPcmFile(pcmFile)) return;
            File pcmHalfFile = new File(pcmFile.getAbsolutePath() + ".half");
            if (pcmHalfFile == null || !pcmHalfFile.exists() || pcmHalfFile.length() == 0) {
                Toast.makeText(this, "pcm音量减半文件不存在，请先点击音量减半按钮", Toast.LENGTH_SHORT).show();
                return;
            }
            getPCMPlayer().play(pcmHalfFile);
        }
    }

    private boolean checkPcmFile(File pcmFile) {
        if (pcmFile == null || !pcmFile.exists() || pcmFile.length() == 0) {
            Toast.makeText(this, "pcm文件不存在，请先录制pcm文件", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void playLeftChannelPCM(View view) {
        if (getPCMPlayer().isPlaying()) {
            getPCMPlayer().stop();
        } else {
            File pcmFile = getPCMRecorder().getOutputFile();
            if (!checkPcmFile(pcmFile)) return;
            File leftChannelFile = new File(pcmFile.getAbsolutePath() + ".left");
            if (leftChannelFile == null || !leftChannelFile.exists() || leftChannelFile.length() == 0) {
                Toast.makeText(this, "pcm左声道文件不存在，请先点分离左右声道按钮", Toast.LENGTH_SHORT).show();
                return;
            }
            getPCMPlayer().play(leftChannelFile, AudioFormat.CHANNEL_IN_LEFT);
        }
    }

    public void playRightChannelPCM(View view) {
        if (getPCMPlayer().isPlaying()) {
            getPCMPlayer().stop();
        } else {
            File pcmFile = getPCMRecorder().getOutputFile();
            if (!checkPcmFile(pcmFile)) return;
            File rightChannelFile = new File(pcmFile.getAbsolutePath() + ".right");
            if (rightChannelFile == null || !rightChannelFile.exists() || rightChannelFile.length() == 0) {
                Toast.makeText(this, "pcm右声道文件不存在，请先点击分离左右声道按钮", Toast.LENGTH_SHORT).show();
                return;
            }
            getPCMPlayer().play(rightChannelFile, AudioFormat.CHANNEL_IN_LEFT);
        }
    }

    public void doubleSpeed(final View view) {
        final File pcmFile = getPCMRecorder().getOutputFile();
        if (!checkPcmFile(pcmFile)) {
            return;
        }
        view.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean success = PCMEditor.doubleSpeed(pcmFile);
                UIWorker.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PCMEditActivity.this, success ? "已将pcm文件提速一倍，播放一下听听效果吧" : "转换失败", Toast.LENGTH_SHORT).show();
                        view.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void playDoubleSpeedPCM(View view) {
        if (getPCMPlayer().isPlaying()) {
            getPCMPlayer().stop();
        } else {
            File pcmFile = getPCMRecorder().getOutputFile();
            if (!checkPcmFile(pcmFile)) return;
            File doubleSpeedFile = new File(pcmFile.getAbsolutePath() + ".speed");
            if (doubleSpeedFile == null || !doubleSpeedFile.exists() || doubleSpeedFile.length() == 0) {
                Toast.makeText(this, "pcm倍速文件不存在，请先点击提升速度一倍", Toast.LENGTH_SHORT).show();
                return;
            }
            getPCMPlayer().play(doubleSpeedFile);
        }
    }
}
