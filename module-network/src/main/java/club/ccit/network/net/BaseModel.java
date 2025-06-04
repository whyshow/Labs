package club.ccit.network.net;

import java.io.Serializable;

/**
 * @author swzhang3
 * name: BaseModel
 * date: 2023/7/18 14:38
 * description:
 **/
public class BaseModel<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
