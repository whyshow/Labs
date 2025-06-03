package club.ccit.network.news.model;

public class NewsListRequestModel {
    private String type = NewsType.TOP.getValue(); // top(推荐,默认),guonei(国内),tiyu(体育),keji(科技),youxi(游戏)
    private int page = 1; // 当前页数, 默认1, 最大50
    private int page_size = 10; // 每页返回条数, 默认30 , 最大30

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

}
