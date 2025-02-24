package club.ccit.aop.verify;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
 * description:检查输入内容长度切片
 **/
@Aspect
public class CheckInputLengthAspect {
    private boolean isOk;

    /**
     * 定义切点，标记切点为所有被@CheckInputLength注解的方法
     */
    @Pointcut("execution(@com.nepviewer.aop.verify.CheckInputLength * *(..))")
    public void CheckInputLength() {
    }

    /**
     * 定义切点，标记切点为所有被@CheckInputLength注解的方法
     */
    @Pointcut("execution(@com.nepviewer.aop.verify.CheckInput * *(..))")
    public void CheckInput() {
    }

    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("CheckInputLength()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method == null) {
            return;
        }
        CheckInputLength checkInput = method.getAnnotation(CheckInputLength.class);
        if (checkInput == null) {
            return;
        }
        String inputString = getInputString(joinPoint.getArgs());
        if (inputString == null) {
            return;
        }
        int minLength = checkInput.minLength();
        int maxLength = checkInput.maxLength();
        if (inputString.length() < minLength) {
            Log.i("LOG111", "输入长度小于" + minLength + "位字符");
            return;
        }
        if (inputString.length() > maxLength) {
            Log.i("LOG111", "输入长度大于" + maxLength + "位字符");
            return;
        }
        Log.i("LOG111", "输入的字符：" + inputString);
        joinPoint.proceed();
    }

    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("CheckInput()")
    public void aroundJoinPointCheckInput(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isOk) {
            joinPoint.proceed();
        } else {
            Context context = (Context) joinPoint.getThis();
            if (context != null) {
                Toast.makeText(context, "字符不符合", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private String getInputString(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String) {
                return (String) arg;
            }
        }
        return null;
    }
}