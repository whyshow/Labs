package club.ccit.view.dialog.basic;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public abstract class BaseDialogFragment<T extends ViewDataBinding> extends DialogFragment implements ClickAction {
    protected T binding;
    private LifecycleEventObserver lifecycleObserver;
    public AppCompatActivity appCompatActivity;
    public Fragment mFragment;
    private long lastShowTime = 0; // 显示防抖时间

    public BaseDialogFragment(AppCompatActivity activity) {
        appCompatActivity = activity;
    }

    public BaseDialogFragment(Fragment fragment) {
        mFragment = fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = onSetViewBinding();
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        assert window != null;
        //设置动画
        window.setWindowAnimations(setAnim());
        WindowManager.LayoutParams params = Objects.requireNonNull(window).getAttributes();
        //设置显示位置
        params.gravity = setGravity();
        params.y = -setBottom();
        if (setWidthRatio() < 0) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            params.width = (int) (setWidthRatio() == 0 ? WindowManager.LayoutParams.WRAP_CONTENT : getResources().getDisplayMetrics().widthPixels * setWidthRatio());
            params.height = setHeightForWidth() ? params.width : WindowManager.LayoutParams.WRAP_CONTENT;
        }
        window.setAttributes(params);
        setCancelable(setCancel());
        window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

        // 添加生命周期观察者
        lifecycleObserver = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (isVisible()) {
                        dismiss();
                    }
                    // 手动关闭时移除观察者
                    if (lifecycleObserver != null) {
                        appCompatActivity.getLifecycle().removeObserver(lifecycleObserver);
                        lifecycleObserver = null;
                    }
                    appCompatActivity = null;
                    mFragment = null;
                    onDialogDismiss();
                }
            }
        };
        // 添加观察者
        if (mFragment != null) {
            mFragment.getLifecycle().addObserver(lifecycleObserver);
        } else if (appCompatActivity != null) {
            appCompatActivity.getLifecycle().addObserver(lifecycleObserver);
        }
        onCreate();
    }

    protected void onCreate() {

    }

    protected void onDialogDismiss() {

    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        // 增加实例状态检查
        if (isAdded() || isStateSaved() || manager.isDestroyed()) {
            return;
        }

        if (!manager.isStateSaved() && !isAdded() && antiShake()) {
            manager.executePendingTransactions();
            super.show(manager, tag);
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isAdded() && isVisible()) {
                super.dismiss();
            } else {
                onDestroy();
                onDialogDismiss();
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * 防抖
     *
     * @return 返回是否在500毫秒内可以点击
     */
    private boolean antiShake() {
        if (System.currentTimeMillis() - lastShowTime > 500) {
            lastShowTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 寻找点击事件的id
     **/
    @Override
    public <T extends View> T findViewByIds(int id) {
        return binding.getRoot().findViewById(id);
    }

    /**
     * 设置点击关闭弹窗
     *
     * @return true: 点击关闭弹窗 false: 不点击关闭弹窗
     */
    protected boolean setCancel() {
        return false;
    }

    /**
     * 设置显示位置
     *
     * @return Gravity.CENTER: 居中显示 Gravity.BOTTOM: 底部显示 Gravity.TOP: 顶部显示 Gravity.START: 左边显示 Gravity.END: 右边显示
     */
    protected int setGravity() {
        return Gravity.CENTER;
    }

    /**
     * 设置底部距离
     *
     * @return px
     */
    protected int setBottom() {
        return 0;
    }

    /**
     * 设置显示动画
     *
     * @return AnimAction.ANIM_SCALE: 缩放动画 AnimAction.ANIM_SLIDE: 滑动动画 AnimAction.ANIM_NONE: 无动画
     */
    protected int setAnim() {
        return AnimAction.ANIM_SCALE;
    }

    /**
     * 设置宽度比例
     *
     * @return 0.8: 宽度为屏幕宽度的80% 0.5: 宽度为屏幕宽度的50%
     */
    protected double setWidthRatio() {
        return 0.8;
    }

    /**
     * 设置高度是否跟随宽度
     *
     * @return true: 高度跟随宽度 false: 高度不跟随宽度
     */
    protected boolean setHeightForWidth() {
        return false;
    }

    /**
     * 反射获取binding
     *
     * @return ActivityXXXBinding.inflate(getLayoutInflater ());
     */
    protected T reflectViewBinding() {
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
     * 视图绑定
     * 如果子类继承没有实现此方法以及没有返回 setLayoutId()
     * 那么将会以反射的形式进行绑定。
     * 性能可能会降低
     *
     * @return 视图绑定
     */
    protected T onSetViewBinding() {
        return reflectViewBinding();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (lifecycleObserver != null) {
            if (appCompatActivity != null) {
                appCompatActivity.getLifecycle().removeObserver(lifecycleObserver);
                appCompatActivity = null;
            } else if (mFragment != null) {
                mFragment.getLifecycle().removeObserver(lifecycleObserver);
                mFragment = null;
            }
        }
        lifecycleObserver = null;
        onDialogDismiss();
    }

    /**
     * 销毁视图绑定
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清空 View Binding 引用，避免内存泄漏
        binding = null;
    }

}