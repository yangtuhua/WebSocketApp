package web.tuhua.com.websocketapp.http;

import java.io.Serializable;

/**
 * 提示提示返回 {@link Feedback}
 * <p>
 * Created by yangtufa on 2017/4/1.
 */

public class Feedback<T> implements Serializable {

    /**
     * code : 401
     * success : false
     * error : true
     * msg : 请先登录
     */

    private String code;
    private boolean success;
    private boolean error;
    private String msg;
    private T vo = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getVo() {
        return vo;
    }

    public void setVo(T vo) {
        this.vo = vo;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "code='" + code + '\'' +
                ", success=" + success +
                ", error=" + error +
                ", msg='" + msg + '\'' +
                ",vo=" + vo + '\'' +
                '}';
    }
}
