package club.ccit.aop.click;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author swzhang3
 * name: PreventDoubleClickAspect
 * date: 2024/6/19 12:52
 * description:
 **/
@Aspect
public class SingleClickAspect {
    /**
     * 记录上次点击的时间
     */
    private static long lastClickTime;

    /**
     * 定义切点，标记切点为所有被@SingleClick注解的方法
     */
    @Pointcut("execution(@com.nepviewer.aop.click.SingleClick * *(..))")
    public void SingleClick() {

    }

    /**
     * 拦截方法调用并检查方法是否在指定时间间隔内被调用的 Aspect。
     * 如果方法已在指定时间间隔内被调用，则跳过方法调用，以防止快速点击。
     */
    @Around("SingleClick()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出方法的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        SingleClick singleClick = method.getAnnotation(SingleClick.class);
        if (singleClick != null) {
            long delayMillis = singleClick.value();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < delayMillis) {
                return;
            }
            lastClickTime = currentTime;
        }
        joinPoint.proceed();
    }
}