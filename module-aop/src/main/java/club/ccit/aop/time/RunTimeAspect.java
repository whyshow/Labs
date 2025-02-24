package club.ccit.aop.time;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author swzhang3
 * name: RunTimeAspect
 * date: 2024/6/19 12:52
 * description:
 **/
@Aspect
public class RunTimeAspect {

    /**
     * 测量使用 `@RunTime` 注解注释的方法的执行时间的指标。
     * <p>
     * 该方面拦截使用 `@RunTime` 注解的方法的执行，并测量执行该方法所需的时间。
     * 如果执行时间超过 500 毫秒，就会向用户显示吐司信息。
     * <p>
     * joinPoint 表示方法执行的连接点。
     *
     * @return 方法执行的结果。
     * @throws Throwable 如果在方法执行过程中出现异常。
     */
    @Around("execution(@com.nepviewer.aop.time.RunTime * *(..))")
    public Object aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getDeclaringTypeName() + "." + methodSignature.getName();
        Context context = (Context) joinPoint.getThis();
        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        long end = System.nanoTime();
        long duration = end - start;
        Log.i("LOG111", "Method " + methodName + " execution time: " + duration / 1_000_000 + "ms");
        if (context != null && duration > 500_000_000) { // 只在执行时间超过500毫秒时显示Toast
            Toast.makeText(context, "Method " + methodName + " execution time: " + duration / 1_000_000 + "ms", Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}