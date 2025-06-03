package club.ccit.network.news.model;


import java.util.ArrayList;
import java.util.List;

// 新闻列表请求返回数据
public class NewsListResultModel {
    private String reason;
    private List<NewsListResultData> result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<NewsListResultData> getResult() {
        return result == null ? new ArrayList<>() : result;
    }

    public void setResult(List<NewsListResultData> result) {
        this.result = result;
    }
}
