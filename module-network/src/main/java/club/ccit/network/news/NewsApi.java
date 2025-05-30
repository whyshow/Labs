package club.ccit.network.news;

import club.ccit.network.news.model.NewsListRequestModel;
import club.ccit.network.news.model.NewsListResultModel;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
     *
     * @param requestModel 新闻列表请求参数
     * @return NewsListResultModel 新闻列表请求返回数据
     * TODO 接口不支持这种请求
     */
    @POST("/toutiao/index")
    Observable<NewsListResultModel> getNewsList(@Body NewsListRequestModel requestModel);

    /**
     * 新闻列表
     */
    @GET("/toutiao/index")
    Observable<NewsListResultModel> getNewsList(
            @Query("key") String key,
            @Query("type") String type,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("is_filter") int isFilter
    );

}
