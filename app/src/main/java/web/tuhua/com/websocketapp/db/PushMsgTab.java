package web.tuhua.com.websocketapp.db;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 推送消息
 * Created by yangtufa on 2017/3/29.
 */
@Table(name = "push_msg", id = "ID")
public class PushMsgTab extends Model {
    @Column(name = "ID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long msgId;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "PUSH_TIME")
    private Long pushTime;

    public PushMsgTab() {
        super();
    }

    public PushMsgTab(Long msgId, String content, Long pushTime) {
        this.msgId = msgId;
        this.content = content;
        this.pushTime = pushTime;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }
}
