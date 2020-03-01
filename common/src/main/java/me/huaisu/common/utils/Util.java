package me.huaisu.common.utils;

public class Util {

    public static int toUnsignedInt16(byte high, byte low) {
        return ((high & 0xff) << 8) | (low & 0xff);
    }

    public static byte[] unsignedInt16ToByteArray(int unsignedInt16) {
        byte high = (byte) ((unsignedInt16 & 0xff00) >>> 8);
        byte low = (byte) (unsignedInt16 & 0xff);
        return new byte[]{high, low};
    }
}
