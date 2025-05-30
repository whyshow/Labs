package club.ccit.basic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.lang.ref.WeakReference;

/**
 * FileName: BaseViewModel
 *
 * @author: 张帅威
 * Date: 2025/1/12 3:49 下午
 * Description: ViewModel 基类
 * Version:
 */
public class BaseViewModel extends AndroidViewModel {
    public MutableLiveData<String> messageText = new MutableLiveData<>(); // 信息
    public MutableLiveData<Integer> networkError = new MutableLiveData<>(); // 网络错误
    public MutableLiveData<Boolean> ok = new MutableLiveData<>(); // 请求是否成功
    public final int SUCCESS = 200;

    private WeakReference<Context> contextRef; // Context弱引用
    private WeakReference<AppCompatActivity> activityRef; // AppCompatActivity弱引用
    private WeakReference<ViewDataBinding> bindingRef; // 添加ViewBinding弱引用

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    // 设置AppCompatActivity
    public void seSafetActivity(AppCompatActivity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    // 设置上下文
    public void setSafeContext(Context context) {
        this.contextRef = new WeakReference<>(context);
    }


    // 设置Binding
    public void setBinding(ViewDataBinding binding) {
        this.bindingRef = new WeakReference<>(binding);
    }

    // 获取Binding
    protected ViewDataBinding getBinding() {
        return bindingRef != null ? bindingRef.get() : null;
    }

    // 获取安全的上下文
    Context getSafeContext() {
        Context context = contextRef != null ? contextRef.get() : null;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return null;
            }
        }
        return context;
    }

    // 获取安全的Activity
    AppCompatActivity getSafeActivity() {
        AppCompatActivity activity = activityRef != null ? activityRef.get() : null;
        if (activity != null && (activity.isDestroyed() || activity.isFinishing())) {
            return null;
        }
        return activity;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clear();
        // 清除弱引用
        if (contextRef != null) {
            contextRef.clear();
        }
        if (activityRef != null) {
            activityRef.clear();
        }
        if (bindingRef != null) {
            bindingRef.clear();
        }
    }

    protected void clear() {

    }
}
