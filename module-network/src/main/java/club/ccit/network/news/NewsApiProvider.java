package club.ccit.network.news;

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
        return "http://192.168.8.136:9001";
    }

    /**
     * 获取新闻列表
     *
     * @return
     */
    public NewsApi getNewsList() {
        return newsApi;
    }

    @Override
    protected Request setHeader(Interceptor.Chain chain) {
        return chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
    }
}
