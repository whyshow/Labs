package club.ccit.home.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import club.ccit.basic.BaseViewModel;
import club.ccit.network.net.AbstractApiObserver;
import club.ccit.network.net.AndroidObservable;
import club.ccit.network.news.NewsApi;
import club.ccit.network.news.NewsApiProvider;
import club.ccit.network.news.model.NewsListRequestModel;
import club.ccit.network.news.model.NewsListResultData;
import club.ccit.network.news.model.NewsListResultModel;

// 新闻模块数据
public class NewsModuleData extends BaseViewModel {
    private final NewsApi newsApi;
    public MutableLiveData<List<NewsListResultData>> newsListLiveData = new MutableLiveData<>();// 新闻列表数据
    public MutableLiveData<NewsListRequestModel> newsListRequestModelMutableLiveData = new MutableLiveData<>();// 新闻列表请求参数
    
    public NewsModuleData(@NonNull Application application) {
        super(application);
        // 获取接口实例
        newsApi = new NewsApiProvider().getNewsList();
        // 初始化默认请求参数
        newsListRequestModelMutableLiveData.setValue(new NewsListRequestModel());
    }

    /**
     * 获取新闻列表
     *
     * @param requestModel 新闻列表请求参数
     */
    public void getNewsList(NewsListRequestModel requestModel) {
        AndroidObservable.create(newsApi.getNewsList(
                requestModel.getKey(),
                requestModel.getType(),
                requestModel.getPage(),
                requestModel.getPage_size(),
                requestModel.getIs_filter()
        )).subscribe(new AbstractApiObserver<NewsListResultModel>() {
            @Override
            protected void succeed(NewsListResultModel newsListResultModel) {
                if (newsListResultModel.getReason().equals("success!")){
                    // 请求成功
                    newsListLiveData.setValue(newsListResultModel.getResult().getData());
                    ok.setValue(true);
                    // 保存到持久数据中

                }else {
                    // 请求失败
                    messageText.setValue(newsListResultModel.getReason());
                    ok.setValue(false);
                }
            }

            @Override
            protected void error(int code, String message) {
                // 请求失败
                messageText.setValue("列表请求失败！");
                ok.setValue(false);
            }
        });
    }
}
