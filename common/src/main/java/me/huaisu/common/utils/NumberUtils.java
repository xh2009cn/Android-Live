package me.huaisu.common.utils;

public class NumberUtils {

    public static byte intToByte(int i) {
        return (byte) (i & 0xFF);
    }

    public static int byteToInt(byte b) {
        return b & 0xFF;
    }
}
