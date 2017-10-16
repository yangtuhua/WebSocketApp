package web.tuhua.com.websocketapp.http;

import java.io.Serializable;

/**
 * 网络请求返回封装实体类
 * Created by yangtufa on 2017/7/24.
 */

public class FeedResult<T> implements Serializable {
    private boolean status;
    private Integer errorCode;
    private String message;
    private T result;

    public FeedResult() {
    }

    public FeedResult(boolean status, Integer errorCode, String message, T result) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.result = result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
