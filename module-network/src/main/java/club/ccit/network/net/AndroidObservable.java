package club.ccit.network.net;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: 张帅威
 * Date: 2021/12/1 1:44 下午
 * Description: Android 生命周期进行关联
 * Version:
 */
public class
AndroidObservable<T> extends Observable<T> implements HasUpstreamObservableSource<T> {

    private final Observable<T> source; // 被观察者
    private LifecycleProvider mLifecycleProvider; // 生命周期

    /**
     * 关联生命周期
     *
     * @param observable 被观察者
     * @param <T>        泛型
     * @return AndroidObservable
     */
    public static <T> AndroidObservable<T> create(Observable<T> observable) {
        return new AndroidObservable<>(observable);
    }

    /**
     * 关联生命周期
     *
     * @param source 被观察者
     */
    public AndroidObservable(Observable<T> source) {
        this.source = source.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 关联生命周期
     *
     * @param owner 生命周期
     * @return AndroidObservable
     */
    public AndroidObservable<T> with(LifecycleOwner owner) {
        mLifecycleProvider = new LifecycleProvider(owner);
        return this;
    }

    /**
     * 关联生命周期
     *
     * @param provider 生命周期
     * @return AndroidObservable
     */
    public AndroidObservable<T> with(LifecycleProvider provider) {
        mLifecycleProvider = provider;
        return this;
    }

    /**
     * 解除生命周期
     *
     * @param observer the incoming {@code Observer}, never {@code null}
     */
    @Override
    protected void subscribeActual(@NonNull Observer<? super T> observer) {
        if (mLifecycleProvider != null) {
            mLifecycleProvider.with(source).subscribe(observer);
        } else {
            source.subscribe(observer);
        }
    }

    /**
     * 被观察者
     *
     * @return ObservableSource
     */
    @NonNull
    @Override
    public ObservableSource<T> source() {
        return source;
    }
}
