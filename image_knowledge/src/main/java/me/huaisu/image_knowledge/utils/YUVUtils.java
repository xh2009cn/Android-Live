package me.huaisu.image_knowledge.utils;

import android.graphics.Bitmap;


import me.huaisu.common.utils.NumberUtils;

public class YUVUtils {

    /**
     * yuv420p 4x4图片的数据分布如下
     * yyyyyyyyyyyyyyyyuuuuvvvv
     * 像素采样如下
     * y0     y1   y2     y3
     *    uv0         uv1
     * y4     y5   y6     y7
     * y8     y9   y10   y11
     *    uv2         uv3
     * y12    y13  y14   y15
     * 其中y0、y1、y4、y5公用uv0，y2、y3、y6、y7公用uv1
     * y8、y9、y12、y13公用uv2，y10、y11、y14、y15公用uv3
     */
    public static Bitmap yuv420pToBitmap(byte[] yuv420p, int w, int h) {
        int[] argb = new int[w * h];
        int frame = w * h;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int y = NumberUtils.byteToInt(yuv420p[i * w + j]);
                int uv_j = j / 2;
                int uv_i = i / 2;
                int uv_width = w / 2;
                int uv_index = uv_i * uv_width + uv_j;
                int u = NumberUtils.byteToInt(yuv420p[frame + uv_index]);
                int v = NumberUtils.byteToInt(yuv420p[frame + frame / 4 + uv_index]);
                int r = (int) (1.164d * y + 1.596d * v - 222.9d);
                int g = (int) (1.164d * y - 0.392d * u - 0.823d * v + 135.6d);
                int b = (int) (1.164d * y + 2.017d * u - 276.8d);
                argb[i * w + j] = 0xFF000000 + (r << 16) + (g << 8) + b;
            }
        }
        return Bitmap.createBitmap(argb, w, h, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap yToBitmap(byte[] gray, int w, int h) {
        int[] argb = new int[w * h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int y = NumberUtils.byteToInt(gray[i * w + j]);
                int u = NumberUtils.byteToInt(Byte.MIN_VALUE);
                int v = NumberUtils.byteToInt(Byte.MIN_VALUE);
                int r = (int) (1.164d * y + 1.596d * v - 222.9d);
                int g = (int) (1.164d * y - 0.392d * u - 0.823d * v + 135.6d);
                int b = (int) (1.164d * y + 2.017d * u - 276.8d);
                argb[i * w + j] = 0xFF000000 + (r << 16) + (g << 8) + b;
            }
        }
        return Bitmap.createBitmap(argb, w, h, Bitmap.Config.ARGB_8888);
    }

    public static void filterY(byte[] yuv420p, int w, int h) {
        int len = yuv420p.length;
        int frame = w * h;
        for (int i = frame; i < len; i++) {
            yuv420p[i] = Byte.MIN_VALUE;
        }
    }

    public static void filterU(byte[] yuv420p, int w, int h) {
        int len = yuv420p.length;
        int frame = w * h;
        for (int i = 0; i < frame; i++) {
            yuv420p[i] = Byte.MIN_VALUE;
        }
        int offset = frame + frame / 4;
        for (int i = offset; i < len; i++) {
            yuv420p[i] = Byte.MIN_VALUE;
        }
    }

    public static void filterV(byte[] yuv420p, int w, int h) {
        int frame = w * h;
        for (int i = 0; i < frame + frame / 4; i++) {
            yuv420p[i] = Byte.MIN_VALUE;
        }
    }

    public static void halfLuminance(byte[] yuv420p, int w, int h) {
        int frame = w * h;
        for (int i = 0; i < frame; i++) {
            int luminance = NumberUtils.byteToInt(yuv420p[i]);
            yuv420p[i] = NumberUtils.intToByte(luminance / 2);
        }
    }

    public static byte[] Y2YUV(byte[] y) {
        int len = y.length;
        byte[] yuv = new byte[(int) (len * 1.5)];
        System.arraycopy(y, 0, yuv, 0, len);
        int yuvLen = yuv.length;
        for (int i = len; i < yuvLen; i++) {
            yuv[i] = Byte.MIN_VALUE;
        }
        return yuv;
    }
}
