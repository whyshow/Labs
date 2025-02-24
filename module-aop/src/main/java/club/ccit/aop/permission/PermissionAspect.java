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

@Aspect
public class PermissionAspect {

    private static final String TAG = "PermissionAspect";

    // 定义切点，拦截所有需要权限检查的方法
    @Pointcut("execution(@com.nepviewer.aop.permission.NeedPermission * *(..))")
    public void methodAnnotatedWithNeedPermission() {
    }

    // 定义通知方法，在目标方法执行前后执行
    @Around("methodAnnotatedWithNeedPermission()")
    public void checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "Checking permission...");
        // 获取 Context 对象
        Context context = (Context) joinPoint.getThis();
        if (context == null) {
            Log.e(TAG, "Context is null, cannot checkpermission.");
            return;
        }
        // 获取 @NeedPermission 注解
        NeedPermission annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NeedPermission.class);
        String[] permissions = annotation.value();
        // 检查权限
        boolean hasAllPermissions = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false;
                break;
            }
        }
        if (hasAllPermissions) {
            // 拥有所有权限，执行目标方法
            Log.d(TAG, "All permissions granted.");
            joinPoint.proceed();
        } else {
            // 没有所有权限，申请权限
            Log.d(TAG, "Requesting permissions...");
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context, permissions, 1);
            } else {
                Log.e(TAG, "Context is not an Activity, cannot request permissions.");
            }
            return;
        }
    }
}