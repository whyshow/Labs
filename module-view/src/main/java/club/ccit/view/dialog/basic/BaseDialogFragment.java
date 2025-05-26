package club.ccit.view.dialog.basic;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import club.ccit.view.R;

/**
 * 对话框基类，封装了通用对话框功能
 *
 * @param <T> ViewDataBinding类型
 */
public abstract class BaseDialogFragment<T extends ViewDataBinding> extends DialogFragment implements ClickAction {
    protected T binding; // ViewDataBinding实例
    private LifecycleEventObserver lifecycleObserver; // 生命周期观察者
    private AppCompatActivity attachedActivity; // 关联的Activity
    private Fragment attachedFragment; // 关联的Fragment
    private long lastClickTime = 0; // 上次点击时间戳
    private static final long CLICK_INTERVAL = 500; // 点击防抖间隔(毫秒)

    /**
     * 构造方法 - 关联Activity
     *
     * @param activity 对话框所属的Activity
     */
    public BaseDialogFragment(@NonNull AppCompatActivity activity) {
        this.attachedActivity = activity;
    }

    /**
     * 构造方法 - 关联Fragment
     *
     * @param fragment 对话框所属的Fragment
     */
    public BaseDialogFragment(@NonNull Fragment fragment) {
        this.attachedFragment = fragment;
    }

    /**
     * 创建视图回调
     *
     * @param inflater           布局填充器
     * @param container          父容器
     * @param savedInstanceState 保存的实例状态
     * @return 对话框根视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = initializeViewBinding();
        return binding.getRoot();
    }

    /**
     * 视图创建完成回调
     * 配置对话框窗口属性和生命周期观察者
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureDialogWindow(); // 配置窗口属性
        setupLifecycleObserver(); // 设置生命周期观察
        initialize(); // 初始化逻辑
        onCreate();// 创建逻辑
    }

    /**
     * 配置对话框窗口属性
     * 包括：背景、动画、位置、大小等
     */
    private void configureDialogWindow() {
        Window window = requireDialog().getWindow();
        if (window == null) return;
        setCancelable(setCancel());
        // 基础配置
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setWindowAnimations(getWindowAnimationStyle());

        // 布局参数配置
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = getGravity(); // 位置
        params.y = -getBottomMargin(); // 底部边距
        params.width = calculateWindowWidth(); // 宽度
        params.height = shouldHeightMatchWidth() ? params.width : WindowManager.LayoutParams.WRAP_CONTENT; // 高度

        window.setAttributes(params);
        setCancelable(shouldCancelOnTouchOutside()); // 点击外部取消
    }

    /**
     * 寻找点击事件的id
     **/
    @Override
    public <T extends View> T findViewByIds(int id) {
        return binding.getRoot().findViewById(id);
    }

    /**
     * 设置生命周期观察者
     * 在宿主销毁时自动关闭对话框
     */
    private void setupLifecycleObserver() {
        lifecycleObserver = (owner, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                safelyDismissDialog(); // 安全关闭
                cleanupResources(); // 清理资源
            }
        };

        // 添加到Activity或Fragment
        LifecycleOwner owner = attachedFragment != null ? attachedFragment : attachedActivity;
        if (owner != null) {
            owner.getLifecycle().addObserver(lifecycleObserver);
        }
    }

    private void safelyDismissDialog() {
        try {
            if (isAdded() && !isStateSaved()) {
                // 使用 FragmentTransaction 确保安全关闭
                FragmentManager fm = getParentFragmentManager();
                fm.beginTransaction()
                        .remove(this)
                        .commitAllowingStateLoss();
            } else {
                // 如果无法安全关闭，则清理资源
                cleanupResources();
            }
        } catch (IllegalStateException e) {
            // 捕获可能的异常并记录日志
            Log.e("BaseDialog", "Failed to dismiss dialog safely", e);
            cleanupResources();
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (canShowDialog(manager)) {
            manager.executePendingTransactions();
            super.show(manager, tag);
        }
    }

    @Override
    public void dismiss() {
        if (isAdded() && isVisible()) {
            super.dismiss();
        } else {
            cleanupResources();
        }
    }

    /**
     * 清理对话框相关资源
     * 1. 移除生命周期观察者
     * 2. 清空关联的Activity/Fragment引用
     * 3. 调用对话框关闭回调
     */
    private void cleanupResources() {
        if (lifecycleObserver != null) {
            LifecycleOwner owner = attachedFragment != null ? attachedFragment : attachedActivity;
            if (owner != null) {
                owner.getLifecycle().removeObserver(lifecycleObserver);
            }
            lifecycleObserver = null;
        }
        attachedActivity = null;
        attachedFragment = null;
        onDialogDismissed();
    }

    /**
     * 检查是否可以显示对话框
     *
     * @param manager Fragment管理器
     * @return true-可以显示 false-不能显示
     * 检查条件：
     * 1. 对话框未添加
     * 2. 状态未保存
     * 3. Fragment管理器未销毁
     * 4. 未快速连续点击
     */
    private boolean canShowDialog(FragmentManager manager) {
        return !isAdded() &&
                !isStateSaved() &&
                !manager.isDestroyed() &&
                !isClickTooFast();
    }

    /**
     * 检查是否点击过快（防抖处理）
     *
     * @return true-点击过快 false-正常点击
     */
    private boolean isClickTooFast() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > CLICK_INTERVAL) {
            lastClickTime = currentTime;
            return false;
        }
        return true;
    }

    /**
     * 计算对话框宽度
     *
     * @return 计算后的宽度值(px)
     * 根据getWidthRatio()返回的比例计算实际宽度
     */
    private int calculateWindowWidth() {
        double ratio = getWidthRatio();
        if (ratio <= 0) {
            return WindowManager.LayoutParams.MATCH_PARENT;
        }
        return (int) (getResources().getDisplayMetrics().widthPixels * ratio);
    }

    /**
     * 初始化ViewBinding
     * 使用反射方式创建对应的ViewBinding实例
     *
     * @return ViewBinding实例
     * @throws RuntimeException 如果初始化失败
     */
    @SuppressWarnings("unchecked")
    private T initializeViewBinding() {
        try {
            Type superclass = getClass().getGenericSuperclass();
            Class<?> bindingClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
            Method method = bindingClass.getDeclaredMethod("inflate", LayoutInflater.class);
            return (T) method.invoke(null, getLayoutInflater());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize view binding", e);
        }
    }

    /**
     * 对话框关闭回调
     * 1. 调用父类关闭逻辑
     * 2. 清理相关资源
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        cleanupResources();
    }

    /**
     * 视图销毁回调
     * 1. 调用父类销毁逻辑
     * 2. 清空ViewBinding引用防止内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /* ========== 可重写方法 ========== */

    protected void initialize() {
    }

    protected void onDialogDismissed() {
    }

    protected void onCreate() {

    }

    protected boolean shouldCancelOnTouchOutside() {
        return false;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getBottomMargin() {
        return 0;
    }

    protected int getWindowAnimationStyle() {
        return AnimAction.ANIM_SCALE;
    }

    protected double getWidthRatio() {
        return 0.8;
    }

    protected boolean shouldHeightMatchWidth() {
        return false;
    }

    protected boolean setCancel() {
        return false;
    }

    public interface AnimAction {
        int ANIM_DEFAULT = -1;
        int ANIM_EMPTY = 0;
        int ANIM_SCALE = R.style.ScaleAnimStyle;
        int ANIM_TOAST = android.R.style.Animation_Toast;
        int ANIM_BOTTOM = R.style.BottomAnimStyle;
    }
}