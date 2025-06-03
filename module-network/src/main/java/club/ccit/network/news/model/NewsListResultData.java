package club.ccit.network.news.model;

// 新闻列表数据
public class NewsListResultData {
    private String Uniquekey;
    private String Title;
    private String Date;
    private String Category;
    private String author_name;
    private String Url;
    private String thumbnail_pic_s;
    private String thumbnail_pic_s02;
    private String thumbnail_pic_s03;
    private String is_content;

    public String getUniquekey() {
        return Uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        Uniquekey = uniquekey;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getThumbnail_pic_s() {
        return thumbnail_pic_s == null ? "" : thumbnail_pic_s;
    }

    public void setThumbnail_pic_s(String thumbnail_pic_s) {
        this.thumbnail_pic_s = thumbnail_pic_s;
    }

    public String getThumbnail_pic_s02() {
        return thumbnail_pic_s02 == null? "" : thumbnail_pic_s02    ;
    }

    public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
        this.thumbnail_pic_s02 = thumbnail_pic_s02;
    }

    public String getThumbnail_pic_s03() {
        return thumbnail_pic_s03 == null? "" : thumbnail_pic_s03;
    }

    public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
        this.thumbnail_pic_s03 = thumbnail_pic_s03;
    }

    public String getIs_content() {
        return is_content;
    }

    public void setIs_content(String is_content) {
        this.is_content = is_content;
    }
}