package club.ccit.network.demo;

import java.util.List;

/**
 * FileName: NewsListBean
 *
 * @author: swzhang3
 * Date: 2021/12/1 9:24 上午
 * Description:
 * Version:
 */
public class NewsListBean {
    @Override
    public String toString() {
        return "NewsListBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", page=" + page +
                ", result=" + result +
                ", time='" + time + '\'' +
                '}';
    }

    private int code;
    private String message;
    private Page page;
    private List<Result> result;
    private String time;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public class Page {
        @Override
        public String toString() {
            return "Page{" +
                    "currpage=" + currpage +
                    ", firstpage=" + firstpage +
                    ", lastpage=" + lastpage +
                    ", pages=" + pages +
                    ", totalpages=" + totalpages +
                    '}';
        }

        private int currpage;
        private int firstpage;
        private int lastpage;
        private List<Integer> pages;
        private int totalpages;

        public void setCurrpage(int currpage) {
            this.currpage = currpage;
        }

        public int getCurrpage() {
            return currpage;
        }

        public void setFirstpage(int firstpage) {
            this.firstpage = firstpage;
        }

        public int getFirstpage() {
            return firstpage;
        }

        public void setLastpage(int lastpage) {
            this.lastpage = lastpage;
        }

        public int getLastpage() {
            return lastpage;
        }

        public void setPages(List<Integer> pages) {
            this.pages = pages;
        }

        public List<Integer> getPages() {
            return pages;
        }

        public void setTotalpages(int totalpages) {
            this.totalpages = totalpages;
        }

        public int getTotalpages() {
            return totalpages;
        }

    }

    public class Result {

        private String Article_id;
        private String Article_title;
        private String Article_user;
        private String Article_html;
        private String Article_text;
        private String Article_category;
        private String Article_date;
        private String Article_display;
        private int Article_hot;

        @Override
        public String toString() {
            return "Result{" +
                    "Article_id='" + Article_id + '\'' +
                    ", Article_title='" + Article_title + '\'' +
                    ", Article_user='" + Article_user + '\'' +
                    ", Article_html='" + Article_html + '\'' +
                    ", Article_text='" + Article_text + '\'' +
                    ", Article_category='" + Article_category + '\'' +
                    ", Article_date='" + Article_date + '\'' +
                    ", Article_display='" + Article_display + '\'' +
                    ", Article_hot=" + Article_hot +
                    '}';
        }

        public void setArticle_id(String Article_id) {
            this.Article_id = Article_id;
        }

        public String getArticle_id() {
            return Article_id;
        }

        public void setArticle_title(String Article_title) {
            this.Article_title = Article_title;
        }

        public String getArticle_title() {
            return Article_title;
        }

        public void setArticle_user(String Article_user) {
            this.Article_user = Article_user;
        }

        public String getArticle_user() {
            return Article_user;
        }

        public void setArticle_html(String Article_html) {
            this.Article_html = Article_html;
        }

        public String getArticle_html() {
            return Article_html;
        }

        public void setArticle_text(String Article_text) {
            this.Article_text = Article_text;
        }

        public String getArticle_text() {
            return Article_text;
        }

        public void setArticle_category(String Article_category) {
            this.Article_category = Article_category;
        }

        public String getArticle_category() {
            return Article_category;
        }

        public void setArticle_date(String Article_date) {
            this.Article_date = Article_date;
        }

        public String getArticle_date() {
            return Article_date;
        }

        public void setArticle_display(String Article_display) {
            this.Article_display = Article_display;
        }

        public String getArticle_display() {
            return Article_display;
        }

        public void setArticle_hot(int Article_hot) {
            this.Article_hot = Article_hot;
        }

        public int getArticle_hot() {
            return Article_hot;
        }

    }
}
