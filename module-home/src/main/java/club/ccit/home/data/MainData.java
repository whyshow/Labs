package club.ccit.home.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import club.ccit.basic.BaseViewModel;
import club.ccit.network.news.model.NewsListResultData;

// 主页数据
public class MainData extends BaseViewModel {
    // 保存的数据，key是所属的新闻类型type，value是新闻列表数据
    public MutableLiveData<Map<String, List<NewsListResultData>>> saveNewsListResultData;// 测试数据

    public MainData(@NonNull Application application) {
        super(application);
        // 初始化需要保存的数据
        saveNewsListResultData = new MutableLiveData<>();
    }

    /**
     * 清空指定类型的新闻列表数据
     *
     * @param category 新闻类型
     */
    public void clearNewsList(String category) {
        // 清空指定类型的新闻列表数据
        Objects.requireNonNull(saveNewsListResultData.getValue()).remove(category);
    }

    // TODO 保存数据到数据库
    public void saveDataBase() {
        // 保存数据到数据库

    }

    // TODO 从数据库读取数据
    public void readNewsListResultData() {
        // 读取数据后，设置到saveNewsListResultData中

    }

}
