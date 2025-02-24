package club.ccit.sdk.net;

import static club.ccit.sdk.net.InternetStateCode.CODE401;
import static club.ccit.sdk.net.InternetStateCode.CODE404;

import android.util.Log;

import java.io.IOException;

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
            if (ex.code() == CODE401){
                // 业务逻辑
                try {
                    Log.i("LOG111",((HttpException) e).response().errorBody().string());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else if (ex.code() == CODE404){
                error(((HttpException) e).response().code(),((HttpException) e).response().message());
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
     * @param t
     */
    protected abstract void succeed(T t);
 /**
     * 请求数据错误
  * @param code
  * @param message
  */
    protected abstract void error(int code, String message);

}
