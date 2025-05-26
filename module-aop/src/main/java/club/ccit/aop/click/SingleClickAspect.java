package club.ccit.aop.click;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class SingleClickAspect {
    private static final String TAG = "SingleClickAspect";
    private static final long DEFAULT_CLICK_INTERVAL = 500; // 默认点击间隔500ms
    private static long lastClickTime = 0L;

    /**
     * 定义切点：拦截所有被@SingleClick注解的方法
     */
    @Pointcut("execution(@club.ccit.aop.click.SingleClick * *(..))")
    public void singleClickPointcut() {
        // 切点方法体为空，仅用于定义切点
    }

    /**
     * 处理点击事件的切面逻辑
     * @param joinPoint 连接点
     * @throws Throwable 可能抛出的异常
     */
    @Around("singleClickPointcut()")
    public void handleSingleClick(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isFastClick(joinPoint)) {
            return;
        }
        // 更新最后点击时间
        updateLastClickTime();
        // 执行原方法
        joinPoint.proceed();
    }

    /**
     * 判断是否为快速点击
     * @param joinPoint 连接点
     * @return true-快速点击 false-正常点击
     */
    private boolean isFastClick(ProceedingJoinPoint joinPoint) {
        // 获取方法的SingleClick注解
        SingleClick annotation = getSingleClickAnnotation(joinPoint);
        if (annotation == null) {
            return false;
        }

        // 获取注解中的点击间隔时间
        long clickInterval = annotation.value() > 0 ? annotation.value() : DEFAULT_CLICK_INTERVAL;
        long currentTime = System.currentTimeMillis();
        return currentTime - lastClickTime < clickInterval;
    }

    /**
     * 获取SingleClick注解
     * @param joinPoint 连接点
     * @return SingleClick注解实例
     */
    private SingleClick getSingleClickAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(SingleClick.class);
    }

    /**
     * 更新最后点击时间
     */
    private void updateLastClickTime() {
        lastClickTime = System.currentTimeMillis();
    }
}