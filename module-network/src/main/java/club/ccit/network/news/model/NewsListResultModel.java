package club.ccit.network.news.model;

import java.util.ArrayList;
import java.util.List;

// 新闻列表请求返回数据
public class NewsListResultModel {
    private String reason;
    private Result result;

    public String getReason() {
        return reason == null ? "" : reason.trim();

    }

    public void setReason(String reason) {
        this.reason = reason == null ? "" : reason;
    }

    public Result getResult() {
        return result == null ? new Result() : result;

    }

    public void setResult(Result result) {
        this.result = result;
    }

    // 请求结果
    public class Result {
        private String stat;
        private List<NewsListResultData> data;

        public String getStat() {
            return stat == null ? "" : stat.trim();

        }

        public void setStat(String stat) {
            this.stat = stat == null ? "" : stat;
        }

        public List<NewsListResultData> getData() {
            return data == null ? new ArrayList<>() : data;

        }

        public void setData(List<NewsListResultData> data) {
            this.data = data;
        }
    }

}
