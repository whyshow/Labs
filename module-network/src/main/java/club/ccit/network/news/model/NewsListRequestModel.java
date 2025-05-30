package club.ccit.network.news.model;

public class NewsListRequestModel {
    private String key; // 开发者key
    private String type = NewsType.TOP.getValue(); // top(推荐,默认),guonei(国内),guoji(国际),yule(娱乐),tiyu(体育),junshi(军事),keji(科技),caijing(财经),youxi(游戏), qiche(汽车), jiankang(健康)
    private int page = 1; // 当前页数, 默认1, 最大50
    private int page_size = 30; // 每页返回条数, 默认30 , 最大30
    private int is_filter = 0; // 是否只返回有内容详情的新闻, 1:是, 默认0

    public String getKey() {
        return key == null ? "" : key.trim();

    }

    public void setKey(String key) {
        this.key = key == null ? "" : key;
    }

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

    public int getIs_filter() {
        return is_filter;

    }

    public void setIs_filter(int is_filter) {
        this.is_filter = is_filter;
    }
}
