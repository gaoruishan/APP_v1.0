package test.grs.com.ims.message;

/**
 * Created by gaoruishan on 15/10/2.
 */
public class IMMessage {
    private int id;
    private String _content = "";
    private String packetId = "";
    private String _from = "";
    private String fromNick = "";
    private String _to = "";
    private int sendOrRecv;
    private int contentType;
    private int chatType;
    private String groupId="";
    private int status;
    private long _time;
    private long guid;
    private String fileName = "";
    private long fileLength;
    private String localPath = "";
    private String originalUri = "";
    private int duration;
    private String middleUri = "";
    private String smallUri = "";
    private String extra = "";
    private int width;
    private int height;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String get_content() {
        return _content;
    }

    public void set_content(String _content) {
        this._content = _content;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public String get_to() {
        return _to;
    }

    public void set_to(String _to) {
        this._to = _to;
    }

    public long get_time() {
        return _time;
    }

    public void set_time(long _time) {
        this._time = _time;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getFrom() {
        return _from;
    }

    public void setFrom(String from) {
        this._from = from;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getTo() {
        return _to;
    }

    public void setTo(String to) {
        this._to = to;
    }

    public int getSendOrRecv() {
        return sendOrRecv;
    }

    public void setSendOrRecv(int sendOrRecv) {
        this.sendOrRecv = sendOrRecv;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return _time;
    }

    public void setTime(long time) {
        this._time = time;
    }

    public long getGuid() {
        return guid;
    }

    public void setGuid(long guid) {
        this.guid = guid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMiddleUri() {
        return middleUri;
    }

    public void setMiddleUri(String middleUri) {
        this.middleUri = middleUri;
    }

    public String getSmallUri() {
        return smallUri;
    }

    public void setSmallUri(String smallUri) {
        this.smallUri = smallUri;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String _content) {
        this._content = _content;
    }

    public IMMessage() {
    }

    public IMMessage(int id, String _content, String packetId, String _from, String fromNick, String _to, int sendOrRecv, int contentType, int chatType, int status, long _time, long guid, String fileName, long fileLength, String localPath, String originalUri, int duration, String middleUri, String smallUri, int width, int height) {
        this.id = id;
        this._content = _content;
        this.packetId = packetId;
        this._from = _from;
        this.fromNick = fromNick;
        this._to = _to;
        this.sendOrRecv = sendOrRecv;
        this.contentType = contentType;
        this.chatType = chatType;
        this.status = status;
        this._time = _time;
        this.guid = guid;
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.localPath = localPath;
        this.originalUri = originalUri;
        this.duration = duration;
        this.middleUri = middleUri;
        this.smallUri = smallUri;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "IMMessage{" +
                "id=" + id +
                ", _content='" + _content + '\'' +
                ", packetId='" + packetId + '\'' +
                ", _from='" + _from + '\'' +
                ", fromNick='" + fromNick + '\'' +
                ", _to='" + _to + '\'' +
                ", sendOrRecv=" + sendOrRecv +
                ", contentType=" + contentType +
                ", chatType=" + chatType +
                ", status=" + status +
                ", _time=" + _time +
                ", guid=" + guid +
                ", fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", localPath='" + localPath + '\'' +
                ", originalUri='" + originalUri + '\'' +
                ", duration=" + duration +
                ", middleUri='" + middleUri + '\'' +
                ", smallUri='" + smallUri + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
