package com.lpf.hitalkyw.fremwork;

import android.app.Application;

import com.lpf.hitalkyw.crash.CrashHandler;

/**
 * Created by sky90 on 2017/2/9.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 全局异常捕获
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());



    }
}
