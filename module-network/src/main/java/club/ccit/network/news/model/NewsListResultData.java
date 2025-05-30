package club.ccit.network.news.model;

// 新闻列表数据
public class NewsListResultData {
    private String uniquekey;
    private String title;
    private String date;
    private String category;
    private String author_name;
    private String url;
    private String thumbnail_pic_s;
    private String thumbnail_pic_s02;
    private String thumbnail_pic_s03;
    private String is_content;

    public String getUniquekey() {
        return uniquekey == null ? "" : uniquekey.trim();

    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey == null ? "" : uniquekey;
    }

    public String getTitle() {
        return title == null ? "" : title.trim();

    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getDate() {
        return date == null ? "" : date.trim();

    }

    public void setDate(String date) {
        this.date = date == null ? "" : date;
    }

    public String getCategory() {
        return category == null ? "" : category.trim();

    }

    public void setCategory(String category) {
        this.category = category == null ? "" : category;
    }

    public String getAuthor_name() {
        return author_name == null ? "" : author_name.trim();

    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name == null ? "" : author_name;
    }

    public String getUrl() {
        return url == null ? "" : url.trim();

    }

    public void setUrl(String url) {
        this.url = url == null ? "" : url;
    }

    public String getThumbnail_pic_s() {
        return thumbnail_pic_s == null ? "" : thumbnail_pic_s.trim();

    }

    public void setThumbnail_pic_s(String thumbnail_pic_s) {
        this.thumbnail_pic_s = thumbnail_pic_s == null ? "" : thumbnail_pic_s;
    }

    public String getThumbnail_pic_s02() {
        return thumbnail_pic_s02 == null ? "" : thumbnail_pic_s02.trim();

    }

    public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
        this.thumbnail_pic_s02 = thumbnail_pic_s02 == null ? "" : thumbnail_pic_s02;
    }

    public String getThumbnail_pic_s03() {
        return thumbnail_pic_s03 == null ? "" : thumbnail_pic_s03.trim();

    }

    public void setThumbnail_pic_s03(String thumbnail_pic_s03) {
        this.thumbnail_pic_s03 = thumbnail_pic_s03 == null ? "" : thumbnail_pic_s03;
    }

    public String getIs_content() {
        return is_content == null ? "" : is_content.trim();

    }

    public void setIs_content(String is_content) {
        this.is_content = is_content == null ? "" : is_content;
    }
}