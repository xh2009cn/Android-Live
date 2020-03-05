package me.huaisu.image_knowledge;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.huaisu.common.utils.FileUtils;
import me.huaisu.image_knowledge.utils.YUVUtils;

public class YUVOperationActivity extends AppCompatActivity {

    ImageView ivOrigin, ivY, ivU, ivV, ivHalfLuma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv_operation);
        ivOrigin = findViewById(R.id.iv_origin);
        ivY = findViewById(R.id.iv_y);
        ivU = findViewById(R.id.iv_u);
        ivV = findViewById(R.id.iv_v);
        ivHalfLuma = findViewById(R.id.iv_half_luma);

        byte[] yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p);
        Bitmap bitmap = YUVUtils.yuv420pToBitmap(yuv, 256, 256);
        ivOrigin.setImageBitmap(bitmap);
    }

    public void filterY(View view) {
        byte[] yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p);
        YUVUtils.filterY(yuv, 256, 256);
        Bitmap bitmap = YUVUtils.yuv420pToBitmap(yuv, 256, 256);
        ivY.setImageBitmap(bitmap);
    }

    public void filterU(View view) {
        byte[] yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p);
        YUVUtils.filterU(yuv, 256, 256);
        Bitmap bitmap = YUVUtils.yuv420pToBitmap(yuv, 256, 256);
        ivU.setImageBitmap(bitmap);
    }

    public void filterV(View view) {
        byte[] yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p);
        YUVUtils.filterV(yuv, 256, 256);
        Bitmap bitmap = YUVUtils.yuv420pToBitmap(yuv, 256, 256);
        ivV.setImageBitmap(bitmap);
    }

    public void halfLuminance(View view) {
        byte[] yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p);
        YUVUtils.halfLuminance(yuv, 256, 256);
        Bitmap bitmap = YUVUtils.yuv420pToBitmap(yuv, 256, 256);
        ivHalfLuma.setImageBitmap(bitmap);
    }
}
