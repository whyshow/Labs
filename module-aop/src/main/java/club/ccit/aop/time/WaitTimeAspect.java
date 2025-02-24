package club.ccit.aop.time;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * WaitTimeAspect "类是一个方面，用于拦截使用"@WaitTime "注解的方法的执行。
 * <p>
 * WaitTime() "指向匹配任何使用"@WaitTime "注解的方法。
 * <p>
 * 在执行与 `WaitTime()` 注解匹配的方法时，会执行 `aroundJoinPoint()` 建议方法。它的作用如下：
 * <p>
 * 1. 从方法中读取 `WaitTime` 注解。
 * 2. 在指定的持续时间（毫秒）内暂停方法的执行。
 * 3. 如果指定的持续时间大于 8000 毫秒，方法将暂停 8000 毫秒。
 * 继续执行原始方法。
 * <p>
 * 此功能用于在某些方法的执行过程中引入延迟，这对于在测试或调试过程中模拟网络延迟或其他耗时操作非常有用。
 */
@Aspect
public class WaitTimeAspect {
    /**
     * 与任何注解为 `@WaitTime` 的方法相匹配的快捷方式。
     */
    @Pointcut("execution(@com.nepviewer.aop.time.WaitTime * *(..))")
    public void WaitTime() {
    }

    /**
     * 处理使用`@WaitTime`注解的方法的执行的方面。
     * 该方面将在指定的持续时间（毫秒）内暂停执行注解的方法。
     * 如果指定的持续时间大于 8000 毫秒，方法将暂停执行 8000 毫秒。
     * <p>
     * param joinPoint 表示正在执行的方法的 `ProceedingJoinPoint` 对象。
     * 如果方法执行过程中出现异常，则抛出 Throwable。
     */
    @Around("WaitTime()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method == null) {
            return;
        }
        WaitTime waitTime = method.getAnnotation(WaitTime.class);
        if (waitTime == null) {
            return;
        }
        Thread.sleep(waitTime.value() >= 8000 ? 8000 : waitTime.value());
        joinPoint.proceed();
    }
}