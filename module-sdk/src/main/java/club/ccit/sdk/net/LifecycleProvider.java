package club.ccit.sdk.net;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: 张帅威
 * Date: 2021/12/1 3:51 下午
 * Description: 生命周期控制，如果生命周期结束，未处理完的事件会自动解绑以避免内存泄漏。
 * Version: 1.0
 */
public class LifecycleProvider {
    /**
     * 生命周期绑定
     */
    private final com.trello.rxlifecycle4.LifecycleProvider<Lifecycle.Event> lifecycleProvider;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    public LifecycleProvider(LifecycleOwner view) {
        this(AndroidLifecycle.createLifecycleProvider(view));
    }

    private LifecycleProvider(com.trello.rxlifecycle4.LifecycleProvider<Lifecycle.Event> mLifecycleProvider) {
        this.lifecycleProvider = mLifecycleProvider;
        compositeDisposable = new CompositeDisposable();
    }

    /**
     * 关联到生命周期里面去
     */
    public <T> Observable<T> with(Observable<T> observable) {
        observable = observable.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) {
                compositeDisposable.add(disposable);
            }
        });
        return observable.compose(lifecycleProvider.<T>bindToLifecycle());
    }

    /**
     * 释放所有，生命周期会自动释放
     */
    public void dispose() {
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}
