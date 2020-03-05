package me.huaisu.common.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int[] toIntArray(byte[] data) {
        int len = data.length;
        int[] intArray = new int[len];
        for (int i = 0; i < len; i++) {
            intArray[i] = data[i] & 0xFF;
        }
        return intArray;
    }

    public static byte[] toByteArray(int[] data) {
        int len = data.length;
        byte[] byteArray = new byte[len];
        for (int i = 0; i < len; i++) {
            byteArray[i] = (byte) (data[i] & 0xFF);
        }
        return byteArray;
    }

    public static byte[] toByteArray(float[] data) {
        int len = data.length;
        byte[] byteArray = new byte[len];
        for (int i = 0; i < len; i++) {
            byteArray[i] = (byte) ((int) data[i] & 0xFF);
        }
        return byteArray;
    }
}
