package club.ccit.network.demo;

import java.util.List;

/**
 * FileName: AggregatePageBean
 *
 * @author: swzhang3
 * Date: 2021/9/9 16:12
 * Description:
 * Version:
 */
public class AggregatePageBean {
    @Override
    public String toString() {
        return "AggregatePageBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", ok=" + ok +
                '}';
    }

    private int code;
    private String msg;
    private List<Data> data;
    private boolean ok;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public boolean getOk() {
        return ok;
    }

    public static class TagList {
        @Override
        public String toString() {
            return "TagList{" +
                    "id=" + id +
                    ", tagName='" + tagName + '\'' +
                    '}';
        }

        private long id;
        private String tagName;

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getTagName() {
            return tagName;
        }

    }

    public static class Data {
        @Override
        public String toString() {
            return "Data{" +
                    "id=" + id +
                    ", className='" + className + '\'' +
                    ", tagList=" + tagList +
                    '}';
        }

        private long id;
        private String className;
        private List<TagList> tagList;

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public void setTagList(List<TagList> tagList) {
            this.tagList = tagList;
        }

        public List<TagList> getTagList() {
            return tagList;
        }

    }
}
