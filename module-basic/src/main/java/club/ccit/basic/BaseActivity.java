package club.ccit.basic;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

import club.ccit.basic.action.ClickAction;
import club.ccit.basic.action.ToastWidget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * FileName: BaseViewDataActivity
 *
 * @author: 张帅威
 * Date: 2023/1/10 09:52
 * Description:
 * Version:
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements ClickAction, ToastWidget {
    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 视图
        binding = onSetViewBinding();
        setContentView(binding.getRoot());
        // 设置屏幕方向
        setScreenDirection();
        onCreate();
    }

    protected void onCreate() {

    }

    /**
     * 寻找点击事件的id
     **/
    @Override
    public <T extends View> T findViewByIds(int id) {
        return binding.getRoot().findViewById(id);
    }

    /**
     * 视图绑定
     * 如果子类继承没有实现此方法以及没有返回 setLayoutId()
     * 那么将会以反射的形式进行绑定。
     * 性能可能会降低
     *
     * @return ActivityXXXBinding.inflate(getLayoutInflater ());
     */
    protected T onSetViewBinding() {
        return reflectViewBinding();
    }

    /**
     * 设置屏幕方向
     *
     * @return ScreenDirection.PORTRAIT 竖屏 ,ScreenDirection.LANDSCAPE 横屏 默认竖屏
     */
    protected ScreenDirection setScreenOrientation() {
        return ScreenDirection.PORTRAIT;
    }

    /**
     * 反射获取binding
     **/
    private T reflectViewBinding() {
        Type superclass = getClass().getGenericSuperclass();
        // 1. 增加类型安全检查
        if (!(superclass instanceof ParameterizedType)) {
            throw new IllegalStateException("必须指定泛型类型");
        }
        Type[] typeArgs = ((ParameterizedType) superclass).getActualTypeArguments();
        if (typeArgs.length == 0) {
            throw new IllegalStateException("缺少泛型参数");
        }
        try {
            // 2. 添加空指针检查
            Class<?> aClass = (Class<?>) typeArgs[0];
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            return (T) method.invoke(null, getLayoutInflater());
        }
        // 3. 细化异常处理
        catch (NoSuchMethodException e) {
            throw new RuntimeException("ViewBinding 必须包含 inflate(LayoutInflater) 方法", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问 inflate 方法", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("inflate 方法执行异常", e.getCause());
        }
        // 4. 移除冗余的成员变量赋值
        // 5. 添加泛型类型安全检查
        catch (ClassCastException e) {
            throw new RuntimeException("类型转换异常，请确认泛型类型正确", e);
        }
    }

    // 设置屏幕方向
    private void setScreenDirection() {
        if (setScreenOrientation() == ScreenDirection.LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (setScreenOrientation() == ScreenDirection.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (setScreenOrientation() == ScreenDirection.UNSPECIFIED) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * 屏幕方向
     * 竖屏，横屏，不指定
     * 默认竖屏
     * 使用示例：setScreenOrientation (ScreenDirection.PORTRAIT);
     */
    public enum ScreenDirection {
        PORTRAIT, // 竖屏
        LANDSCAPE, // 横屏
        UNSPECIFIED // 不指定
    }

    /**
     * 结束回调
     **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}