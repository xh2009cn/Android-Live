package me.huaisu.android_live;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import me.huaisu.audio.encode.AacEncodeActivity;
import me.huaisu.pcm_audio_recorder.PCMEditActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPcmModule(View view) {
        startActivity(new Intent(this, PCMEditActivity.class));
    }

    public void encodePcmToAac(View view) {
        startActivity(new Intent(this, AacEncodeActivity.class));
    }
}
