package club.ccit.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import club.ccit.basic.action.ClickAction;
import club.ccit.basic.action.ToastWidget;

/**
 * @author: 张帅威
 * Date: 2021/11/23 08:16
 * Description: Fragment 基类
 * Version:
 */
public abstract class BaseViewDataFragment<T extends ViewDataBinding> extends Fragment implements ClickAction, ToastWidget {
    protected T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = onSetViewBinding();
        if (setStatusBackgroundColor() != -1) {
            Window window = requireActivity().getWindow();
            window.setStatusBarColor(setStatusBackgroundColor());
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onCreate();
    }

    protected void onCreate() {

    }

    /**
     * 寻找点击事件的id
     **/
    @Override
    public <V extends View> V findViewByIds(int id) {
        return getView().findViewById(id);
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
     * 反射获取binding
     **/
    private T reflectViewBinding() {
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            binding = (T) method.invoke(null, getLayoutInflater());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return binding;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 结束回调
     **/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected int setStatusBackgroundColor() {
        return -1;
    }

}
