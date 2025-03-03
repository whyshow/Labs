package club.ccit.network.demo;

import club.ccit.network.net.BaseApiProvider;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * FileName: DraftApiProvider
 *
 * @author: 张帅威
 * Date: 2021/12/3 2:25 下午
 * Description:
 * Version:
 */
public class NewsApiProvider extends BaseApiProvider {

    NewsApi newsApi;
    /**
     * 实例化一些连接网络配置
     */
    public NewsApiProvider() {
        super();
        // 创建新闻API
        newsApi = getRetrofit().create(NewsApi.class);
    }

    @Override
    protected String baseUrl() {
        return "http://api.ccit.club/";
    }

    /**
     * 获取新闻列表
     * @return
     */
    public NewsApi getNewsList() {
        return newsApi;
    }

    @Override
    protected Request setHeader(Interceptor.Chain chain) {
        return chain.request()
                .newBuilder()
                .addHeader("Authorization", "1986f37e-083d-4d79-8f03-a66995168ec5")
                .addHeader("client-id", "android_app_myb")
                .build();
    }
}
