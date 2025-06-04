package club.ccit.basic;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;

import java.util.Arrays;

import club.ccit.network.NetworkConfig;

/**
 * @author swzhang3
 */
public class BaseApplication extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        ARouter.init(this);
        setARouter();
        // 初始化网络配置
         NetworkConfig.setBaseUrl(getBaseUrl());
         NetworkConfig.setIsDebug(isDebug());
    }

    public void setARouter() {
        if (isDebug()) {
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

    private boolean isDebug() {
        ApplicationInfo appInfo;
        String msg = "";
        try {
            appInfo = this.getPackageManager()
                    .getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString("MODEL");
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        if (msg == null) {
            return false;
        } else {
            return msg.equals("DEBUG");
        }
    }

    // 获取BaseUrl
    private String getBaseUrl() {
        ApplicationInfo appInfo;
        String msg = "";
        try {
            appInfo = this.getPackageManager()
                    .getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString("BASE_URL");
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return msg;
    }


}
