package club.ccit.aop.thread;
/**
 * @author swzhang3
 * name: BackgroundThreadAspect
 * date: 2024/7/8 16:36
 * description:
 **/

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
public class RunOneThreadAspect {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Pointcut("execution(@com.nepviewer.aop.thread.RunOnThread * *(..))")
    public void methodAnnotatedWithRunOnBackground() {
    }

    @Around("methodAnnotatedWithRunOnBackground()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        executor.execute(() -> {
            try {
                joinPoint.proceed(); // 在后台线程中执行方法
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return null; // 对于异步方法，可以返回 null
    }
}