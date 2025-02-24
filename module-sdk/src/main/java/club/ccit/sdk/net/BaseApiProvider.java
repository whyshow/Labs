package club.ccit.sdk.net;

import android.util.Log;

import com.trello.rxlifecycle4.android.lifecycle.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: 张帅威
 * Date: 2021/12/1 4:37 下午
 * Description: 请求网络
 * Version:
 */
public abstract class BaseApiProvider {
    protected Retrofit mRetrofit;

    /**
     * 实例化一些连接网络配置
     *
     */
    public BaseApiProvider() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
            //打印retrofit日志
            Log.i("OKHTTP", "retrofitBack = " + message);
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

        //  连接超时
        builder.connectTimeout(30, TimeUnit.SECONDS)
                // 流读取超时
                .readTimeout(120, TimeUnit.SECONDS)
                // 流写入超时
                .writeTimeout(120, TimeUnit.SECONDS);

        builder.addInterceptor(chain -> {
            // 设置 Header
            Request request = setHeader(chain);
            return chain.proceed(request);
        });
        // 如果是DEBUG模式设置打印日志
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor);
        }
        OkHttpClient client = builder.build();
        mRetrofit = new Retrofit.Builder()
                // 加入配置
                .client(client)
                // 基本的域名
                .baseUrl(baseUrl())
                // 绑定RxJava
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                // 处理body为空而报 End of input at line 1 column 1
                .addConverterFactory(new NullOnEmptyConverterFactory())
                // 使用gson 自动解析数据
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 获取 Retrofit 实例
     *
     * @return Retrofit
     */
    protected Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 设置域名
     *
     * @return 域名
     */
    protected abstract String baseUrl();

    protected Request setHeader(Interceptor.Chain chain){
        return chain.request()
                .newBuilder()
                .addHeader("Authorization", "1986f37e-083d-4d79-8f03-a66995168ec5")
                .addHeader("client-id", "app_myb_android")
                .build();
    }
}
