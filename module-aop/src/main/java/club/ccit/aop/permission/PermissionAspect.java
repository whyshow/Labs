package club.ccit.aop.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 权限检查切面类
 * 处理所有被@NeedPermission注解标记的方法的权限检查逻辑
 */
@Aspect
public class PermissionAspect {
    private static final String TAG = "PermissionAspect";
    // 权限请求码
    private static final int PERMISSION_REQUEST_CODE = 1001;

    /**
     * 定义切点：拦截所有被@NeedPermission注解的方法
     */
    @Pointcut("execution(@club.ccit.aop.permission.NeedPermission * *(..))")
    public void methodWithPermissionCheck() {
        // 切点方法体为空，仅用于定义切点
    }

    /**
     * 权限检查切面逻辑
     * @param joinPoint 连接点
     * @throws Throwable 可能抛出的异常
     */
    @Around("methodWithPermissionCheck()")
    public Object checkAndRequestPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Context context = getContextFromJoinPoint(joinPoint);
        if (context == null) {
            Log.e(TAG, "无法获取Context对象");
            return null;
        }

        NeedPermission annotation = getNeedPermissionAnnotation(joinPoint);
        if (annotation == null) {
            Log.w(TAG, "未找到NeedPermission注解");
            return joinPoint.proceed();
        }

        String[] requiredPermissions = annotation.value();
        if (hasAllPermissions(context, requiredPermissions)) {
            Log.d(TAG, "已拥有所有所需权限");
            return joinPoint.proceed();
        } else {
            requestPermissions(context, requiredPermissions);
            return null;
        }
    }

    /**
     * 从连接点获取Context对象
     * @param joinPoint 连接点
     * @return Context对象或null
     */
    private Context getContextFromJoinPoint(ProceedingJoinPoint joinPoint) {
        Object target = joinPoint.getThis();
        return (target instanceof Context) ? (Context) target : null;
    }

    /**
     * 获取NeedPermission注解
     * @param joinPoint 连接点
     * @return NeedPermission注解实例
     */
    private NeedPermission getNeedPermissionAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(NeedPermission.class);
    }

    /**
     * 检查是否拥有所有权限
     * @param context Context对象
     * @param permissions 需要检查的权限数组
     * @return true-拥有所有权限 false-缺少权限
     */
    private boolean hasAllPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "缺少权限: " + permission);
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     * @param context Context对象
     * @param permissions 需要请求的权限数组
     */
    private void requestPermissions(Context context, String[] permissions) {
        if (context instanceof Activity) {
            Log.d(TAG, "请求权限: " + Arrays.toString(permissions));
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    permissions,
                    PERMISSION_REQUEST_CODE
            );
        } else {
            Log.e(TAG, "Context不是Activity实例，无法请求权限");
        }
    }
}