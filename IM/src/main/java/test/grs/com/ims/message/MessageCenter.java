package test.grs.com.ims.message;

/**
 * Created by gaoruishan on 15/10/27.
 */
public class MessageCenter {
    private int id;
    private String title;
    private String content;
    private long time;
    private int type;//1 系统；2 活动；3 回复；
    private int tag;//0 不提醒；1 提醒；

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public MessageCenter() {
    }

    public MessageCenter(String title, String content, long time, int type, int tag) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.type = type;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "MessageCenter{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", type=" + type +
                ", tag=" + tag +
                '}';
    }
}
