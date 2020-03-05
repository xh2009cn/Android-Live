package me.huaisu.common.utils;

import android.util.Log;

import me.huaisu.common.App;

public class LogUtils {

    private static final String TAG = "AndroidLive";

    public static void i(String log) {
        Log.i(TAG, log);
    }

    public static void d(String log) {
        if (App.DEBUG_ON) {
            Log.d(TAG, log);
        }
    }
}
