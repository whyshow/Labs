package club.ccit.network.news.model;


import java.util.ArrayList;
import java.util.List;

// 新闻列表请求返回数据
public class NewsListResultModel {
    private List<NewsListResultData> result;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NewsListResultData> getResult() {
        return result == null ? new ArrayList<>() : result;
    }

    public void setResult(List<NewsListResultData> result) {
        this.result = result;
    }
}
