package me.huaisu.image_knowledge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImageKnowledgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_knowledge);
    }

    public void imageCompress(View view) {
        startActivity(new Intent(this, ImageCompressActivity.class));
    }

    public void yuvOperation(View view) {
        startActivity(new Intent(this, YUVOperationActivity.class));
    }
}
