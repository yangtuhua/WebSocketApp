package web.tuhua.com.websocketapp;

import java.io.Serializable;

/**
 * Created by yangtufa on 2017/10/15.
 */

public class MsgBean implements Serializable{

    /**
     * id : 41
     * msg : 请叫我凸伐
     * creat_time : 1508060211000
     */

    private int id;
    private String msg;
    private long creat_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(long creat_time) {
        this.creat_time = creat_time;
    }
}
