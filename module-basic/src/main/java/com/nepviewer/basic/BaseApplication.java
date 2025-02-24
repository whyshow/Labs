package com.nepviewer.basic;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;

import java.util.Arrays;

/**
 * @author swzhang3
 */
public abstract class BaseApplication extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        ARouter.init(this);
    }

    public void setARouter(boolean isDebug) {
        if (isDebug) {
            // 打印日志
            ARouter.openLog();
            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DefenseCrash.initialize(this);
        DefenseCrash.install(new IExceptionHandler() {
            @Override
            public void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode, boolean isCrashInChoreographer) throws Throwable {
                Log.i("LOG111", "异常捕获原因：" + throwable.getLocalizedMessage());
                Log.i("LOG111", "异常捕获位置" + Arrays.toString(Arrays.stream(throwable.getStackTrace()).toArray()));
            }
        });
    }
}
