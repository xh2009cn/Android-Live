package me.huaisu.audio.encode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

public class AacEncodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aac_encode);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void generateAacFile(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请先授予存储权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        final File pcmFile = new File(Environment.getExternalStorageDirectory(), "huaisu/tdjm.pcm");
        if (!pcmFile.exists()) {
            Toast.makeText(this, "pcm文件不存在!" + pcmFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return;
        }
        final File aacFile = new File(pcmFile.getAbsolutePath().replace(".pcm", ".aac"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                AacEncoder encoder = new AacEncoder();
                int ret = encoder.encodePcmFile(pcmFile.getAbsolutePath(), aacFile.getAbsolutePath());
                final String toastStr = ret == 0 ? "pcm文件编码成功，路径：" + aacFile.getAbsolutePath() : "pcm文件编码失败";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AacEncodeActivity.this, toastStr, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }
}
