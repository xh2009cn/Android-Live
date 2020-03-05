package me.huaisu.image_knowledge;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.huaisu.common.utils.FileUtils;
import me.huaisu.common.utils.IOUtils;
import me.huaisu.image_knowledge.utils.DCT2Transformer;
import me.huaisu.image_knowledge.utils.IDCT2Transformer;
import me.huaisu.image_knowledge.utils.ImageCompressUtils;
import me.huaisu.image_knowledge.utils.YUVUtils;

public class ImageCompressActivity extends AppCompatActivity {

    ImageView ivOrigin, ivDCT, ivIDCT, ivBlockDct,ivBlockIdct, ivQuantization;
    private static int N = 128;
    private static int BLOCK_SIZE = 8;
    float[] singleDCT;
    float[][] dctBlocks;
    float[][] quantizationBlocks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_compress);
        ivOrigin = findViewById(R.id.iv_origin);
        ivDCT = findViewById(R.id.iv_dct_transform);
        ivIDCT = findViewById(R.id.iv_idct);
        ivQuantization = findViewById(R.id.iv_quantization);
        ivBlockDct = findViewById(R.id.iv_dct_block_transform);
        ivBlockIdct = findViewById(R.id.iv_idct_block_transform);

        byte[] yuv = FileUtils.openRawResources(R.raw.lena_128x128_yuv420p);
        Bitmap bitmap = YUVUtils.yToBitmap(yuv, N, N);
        ivOrigin.setImageBitmap(bitmap);
    }

    public void dctTransform(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] yuv = FileUtils.openRawResource(R.raw.lena_128x128_yuv420p);
                singleDCT = DCT2Transformer.transform(yuv, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(IOUtils.toByteArray(singleDCT), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivDCT.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    public void idctTransform(View view) {
        if (singleDCT == null) {
            Toast.makeText(this, "请先做DCT变换", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] idct = IDCT2Transformer.transform(singleDCT, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(IOUtils.toByteArray(idct), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivIDCT.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    public void dctBlockTransform(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] yuv = FileUtils.openRawResource(R.raw.lena_128x128_yuv420p);
                // 先将图片分成一个个8x8的小块
                int[][] blocks = ImageCompressUtils.splitToBlocks(yuv, N);
                // 依次对每个分块做DCT变换
                dctBlocks = new float[blocks.length][];
                for (int i = 0; i < blocks.length; i++) {
                    dctBlocks[i] = DCT2Transformer.transform(blocks[i], BLOCK_SIZE);
                }
                // 将DCT变换之后的分块合并，转成bitmap展示出来
                float[] dctData = ImageCompressUtils.concatBlocks(dctBlocks, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(IOUtils.toByteArray(dctData), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivBlockDct.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    public void quantization(View view) {
        if (dctBlocks == null) {
            Toast.makeText(this, "请先做DCT分块变换", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int len = dctBlocks.length;
                quantizationBlocks = new float[len][];
                for (int i = 0; i < len; i++) {
                    float[] dct = dctBlocks[i];
                    float[] quantizationBlock = ImageCompressUtils.quantization(dct);
                    quantizationBlocks[i] = quantizationBlock;
                }
                float[] quantizationData = ImageCompressUtils.concatBlocks(quantizationBlocks, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(IOUtils.toByteArray(quantizationData), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivQuantization.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    public void idctBlockTransform(View view) {
        final float[][] blocks = quantizationBlocks != null ? quantizationBlocks : dctBlocks;
        if (blocks == null) {
            Toast.makeText(this, "请先做分块DCT变换", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 依次将每个8x8的分块做DCT反变换
                int len = blocks.length;
                int[][] idctBlocks = new int[len][];
                for (int i = 0; i < len; i++) {
                    float[] dct = blocks[i];
                    int[] idctBlock = IDCT2Transformer.transform(dct, BLOCK_SIZE);
                    idctBlocks[i] = idctBlock;
                }
                // 将反变换的分块合并，展示出来
                int[] idctData = ImageCompressUtils.concatBlocks(idctBlocks, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(IOUtils.toByteArray(idctData), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivBlockIdct.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }
}
