package me.huaisu.audio.encode;

public class AacEncoder {

    static {
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avdevice");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("aac_encoder");
    }

    public native int encodePcmFile(String pcmFile, String aacFile);
}
