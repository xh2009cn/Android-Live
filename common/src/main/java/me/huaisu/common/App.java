package me.huaisu.common;

import android.app.Application;

public class App extends Application {

    public static final boolean DEBUG_ON = true;
    private static App sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static App get() {
        return sApp;
    }
}
