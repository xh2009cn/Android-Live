package me.huaisu.image_knowledge;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.math3.linear.RealMatrix;

import me.huaisu.common.utils.FileUtils;
import me.huaisu.common.utils.IOUtils;
import me.huaisu.common.utils.MatrixUtils;
import me.huaisu.image_knowledge.utils.DCTUtils;
import me.huaisu.image_knowledge.utils.ImageCompressUtils;
import me.huaisu.image_knowledge.utils.YUVUtils;

public class ImageCompressActivity extends AppCompatActivity {

    ImageView ivOrigin, ivDCT, ivIDCT, ivBlockDct,ivBlockIdct, ivQuantization;
    private static int N = 256;
    private static int BLOCK_SIZE = 8;
    byte[] yuv;
    RealMatrix singleDCT;
    double[][] dctBlocks;
    double[][] quantizationBlocks;

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

        Bitmap bitmap = YUVUtils.yToBitmap(getYUV(), N, N);
        ivOrigin.setImageBitmap(bitmap);
    }

    public void dctTransform(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                singleDCT = DCTUtils.dct2(getYUV(), N);
                final Bitmap bitmap = YUVUtils.yToBitmap(MatrixUtils.getMatrixByteData(singleDCT), N, N);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivDCT.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }

    private byte[] getYUV() {
        if (yuv == null) {
            yuv = FileUtils.openRawResources(R.raw.lena_256x256_yuv420p_gray);
        }
        return yuv;
    }

    public void idctTransform(View view) {
        if (singleDCT == null) {
            Toast.makeText(this, "请先做DCT变换", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                RealMatrix signalMatrix = DCTUtils.idct2(singleDCT, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(MatrixUtils.getMatrixByteData(signalMatrix), N, N);
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
                // 先将图片分成一个个8x8的小块
                byte[][] blocks = ImageCompressUtils.splitToBlocks(getYUV(), N);
                // 依次对每个分块做DCT变换
                dctBlocks = new double[blocks.length][];
                for (int i = 0; i < blocks.length; i++) {
                    RealMatrix dctMatrix = DCTUtils.dct2(blocks[i], BLOCK_SIZE);
                    dctBlocks[i] = MatrixUtils.getMatrixDoubleData(dctMatrix);
                }
                // 将DCT变换之后的分块合并，转成bitmap展示出来
                double[] dctData = ImageCompressUtils.concatBlocks(dctBlocks, N);
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
                quantizationBlocks = new double[len][];
                for (int i = 0; i < len; i++) {
                    double[] dct = dctBlocks[i];
                    double[] quantizationBlock = ImageCompressUtils.quantization(dct);
                    quantizationBlocks[i] = quantizationBlock;
                }
                double[] quantizationData = ImageCompressUtils.concatBlocks(quantizationBlocks, N);
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
        final double[][] blocks = quantizationBlocks != null ? quantizationBlocks : dctBlocks;
        if (blocks == null) {
            Toast.makeText(this, "请先做分块DCT变换", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 依次将每个8x8的分块做DCT反变换
                int len = blocks.length;
                byte[][] idctBlocks = new byte[len][];
                for (int i = 0; i < len; i++) {
                    double[] dct = blocks[i];
                    RealMatrix signalMatrix = DCTUtils.idct2(MatrixUtils.toMatrix(dct, BLOCK_SIZE), BLOCK_SIZE);
                    byte[] idctBlock = MatrixUtils.getMatrixByteData(signalMatrix);
                    idctBlocks[i] = idctBlock;
                }
                // 将反变换的分块合并，展示出来
                byte[] idctData = ImageCompressUtils.concatBlocks(idctBlocks, N);
                final Bitmap bitmap = YUVUtils.yToBitmap(idctData, N, N);
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
