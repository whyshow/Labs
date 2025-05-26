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

    private final Observable<T> source;
    private LifecycleProvider mLifecycleProvider;

    public static <T> AndroidObservable<T> create(Observable<T> observable) {
        return new AndroidObservable<>(observable);
    }

    public AndroidObservable(Observable<T> source) {
        this.source = source.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public AndroidObservable<T> with(LifecycleOwner owner) {
        mLifecycleProvider = new LifecycleProvider(owner);
        return this;
    }

    public AndroidObservable<T> with(LifecycleProvider provider) {
        mLifecycleProvider = provider;
        return this;
    }

    @Override
    protected void subscribeActual(@NonNull Observer<? super T> observer) {
        if (mLifecycleProvider != null){
            mLifecycleProvider.with(source).subscribe(observer);
        }else {
            source.subscribe(observer);
        }
    }

    @NonNull
    @Override
    public ObservableSource<T> source() {
        return source;
    }
}
