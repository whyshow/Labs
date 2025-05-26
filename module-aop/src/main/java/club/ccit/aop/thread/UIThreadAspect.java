package club.ccit.aop.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * UI线程切面处理类
 * 确保被@UiThread注解的方法在UI线程执行
 */
@Aspect
public class UIThreadAspect {
    private static final String TAG = "UIThreadAspect";
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 定义切点：拦截所有被@UiThread注解的方法
     */
    @Pointcut("execution(@club.ccit.aop.thread.UiThread * *(..))")
    public void uiThreadAnnotatedMethod() {
        // 切点方法体为空，仅用于定义切点
    }

    /**
     * 处理UI线程切换逻辑
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("uiThreadAnnotatedMethod()")
    public Object executeOnUiThread(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isOnMainThread()) {
            Log.d(TAG, "Already on UI thread, proceed directly");
            return joinPoint.proceed();
        }

        Log.d(TAG, "Switching to UI thread for method execution");
        MAIN_HANDLER.post(() -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                handleExecutionException(e);
            }
        });
        return null;
    }

    /**
     * 检查当前是否在主线程
     *
     * @return true-在主线程 false-不在主线程
     */
    private boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 处理方法执行异常
     *
     * @param e 异常对象
     */
    private void handleExecutionException(Throwable e) {
        Log.e(TAG, "Error executing method on UI thread", e);
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException("Execution failed on UI thread", e);
    }
}