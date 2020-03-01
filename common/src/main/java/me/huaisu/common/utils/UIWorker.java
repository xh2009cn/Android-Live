package me.huaisu.common.utils;

import android.os.Handler;
import android.os.Looper;

public class UIWorker {

    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run();
        } else {
            sUiHandler.post(runnable);
        }
    }
}
