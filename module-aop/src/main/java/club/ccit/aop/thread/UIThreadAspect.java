package club.ccit.aop.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 确保在 UI 线程上执行带有“@UiThread”注释的方法的一个方面。
 * <p>
 * 此方面拦截用“@UiThread”注释的方法调用并切换执行
 * 使用绑定到主 Looper 的“Handler”到 UI 线程。这确保了
 * 带注释的方法始终在 UI 线程上执行，即使它们是从
 * 后台线程。
 */
@Aspect
public class UIThreadAspect {

    private static final String TAG = "UiThreadAspect";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 定义切点，拦截所有需要在 UI 线程执行的方法
    @Pointcut("execution(@com.nepviewer.aop.thread.UiThread * *(..))")
    public void methodAnnotatedWithUiThread() {
    }

    // 定义通知方法，在目标方法执行后执行
    @Around("methodAnnotatedWithUiThread()")
    public void runOnUiThread(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "Switching to UI thread...");
        mainHandler.post(() -> {
            // 在 UI 线程执行的代码
            Log.d(TAG, "Running on UI thread.");
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}