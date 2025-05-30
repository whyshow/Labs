package club.ccit.network.net;

import static club.ccit.network.net.InternetStateCode.CODE401;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * @author: 张帅威
 * Date: 2021/12/2 9:54 上午
 * Description: 接口回调 处理401 等代码
 * Version:
 */
public abstract class AbstractApiObserver<T> extends DisposableObserver<T> {

    @Override
    public void onNext(@NonNull T t) {
        succeed(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof HttpException) {
            HttpException ex = (HttpException) e;
            if (ex.code() == CODE401) {
                // 业务逻辑

            }
        }
    }

    /**
     * 解除
     */
    @Override
    public void onComplete() {

    }

    /**
     * 请求数据成功
     *
     * @param t 返回的数据
     */
    protected abstract void succeed(T t);

    /**
     * 请求数据错误
     *
     * @param code     错误码
     * @param message  错误信息
     */
    protected abstract void error(int code, String message);

}
