package club.ccit.sdk.demo;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author: 张帅威
 * Date: 2021/12/1 9:00 上午
 * Description: 新闻列表接口
 * Version:
 */
public interface NewsApi {

    /**
     * 新闻列表
     * @param p 页码
     * @return
     */
    @GET(ApiUrls.API_NEW_LIST)
    Single<NewsListBean> getNewsList(@Query("p") int p);
    /**
     * 新闻列表
     * @param p 页码
     * @return
     */
    @GET(ApiUrls.API_NEW_LIST)
    Observable<NewsListBean> getNewsList2(@Query("p") int p);

}
