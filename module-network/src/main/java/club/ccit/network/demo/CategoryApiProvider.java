package club.ccit.network.demo;

import club.ccit.network.net.BaseApiProvider;

/**
 * FileName: DraftApiProvider
 *
 * @author: 张帅威
 * Date: 2021/12/3 2:25 下午
 * Description:
 * Version:
 */
public class CategoryApiProvider extends BaseApiProvider {

    CategoryApi categoryApi;
    /**
     * 实例化一些连接网络配置
     */
    public CategoryApiProvider() {
        super();
        // 创建新闻API
        categoryApi = getRetrofit().create(CategoryApi.class);
    }

    @Override
    protected String baseUrl() {
        return "https://api.miaoyibao.com";

        // return "http://47.118.80.138:9999";
    }

    public CategoryApi getAggregatePage() {
        return categoryApi;
    }
}
