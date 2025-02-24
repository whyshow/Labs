package com.nepviewer.storage.shared.base;

import android.annotation.SuppressLint;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * @author swzhang3
 * name: ApplicationContext
 * date: 2023/6/15 16:31
 * description:
 **/
public interface ApplicationContext {
    /**
     * 获取上下文
     *
     * @return
     */
    static Context getContext() {
        synchronized (ApplicationContext.class) {
            try {
                @SuppressLint("PrivateApi") Class<?> activityThread = Class.forName("android.app.ActivityThread");
                Method method = activityThread.getMethod("currentActivityThread");
                //获取currentActivityThread 对象
                Object currentActivityThread = method.invoke(activityThread);
                Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                //获取 Context对象
                return (Context) method2.invoke(currentActivityThread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
