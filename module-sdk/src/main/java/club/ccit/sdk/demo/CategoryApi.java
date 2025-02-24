package club.ccit.sdk.demo;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.POST;

/**
 * @author: 张帅威
 * Date: 2021/12/1 9:00 上午
 * Description: 新闻列表接口
 * Version:
 */
public interface CategoryApi {
    /**
     * 选择品种-聚合页
     * @return
     */
    @POST(ApiUrls.API_CATEGORY)
    Observable<AggregatePageBean> getAggregatePage();
}
