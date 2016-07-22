package test.grs.com.ims.message;

import com.littlec.sdk.entity.CMMember;

import java.util.List;

/**
 * Created by gaoruishan on 15/10/15.
 */
public class IMGroup {
    private int id;
    private String groupId;
    private String groupName;
    private String memberId = "";
    private String memberNick = "";
    private boolean needApprovalRequired;
    private boolean isMute;
    private String groupDesc;
    private boolean isPublic = true;
    private String chairMan = null;
    private int maxNumbers = 100;

    public IMGroup() {
    }

    @Override
    public String toString() {
        return "IMGroup{" +
                "id=" + id +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", memberId='" + memberId + '\'' +
                ", memberNick='" + memberNick + '\'' +
                ", needApprovalRequired=" + needApprovalRequired +
                ", isMute=" + isMute +
                ", groupDesc='" + groupDesc + '\'' +
                ", isPublic=" + isPublic +
                ", chairMan='" + chairMan + '\'' +
                ", maxNumbers=" + maxNumbers +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberNick() {
        return memberNick;
    }

    public void setMemberNick(String memberNick) {
        this.memberNick = memberNick;
    }

    public boolean isNeedApprovalRequired() {
        return needApprovalRequired;
    }

    public void setNeedApprovalRequired(boolean needApprovalRequired) {
        this.needApprovalRequired = needApprovalRequired;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setIsMute(boolean isMute) {
        this.isMute = isMute;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getChairMan() {
        return chairMan;
    }

    public void setChairMan(String chairMan) {
        this.chairMan = chairMan;
    }

    public int getMaxNumbers() {
        return maxNumbers;
    }

    public void setMaxNumbers(int maxNumbers) {
        this.maxNumbers = maxNumbers;
    }
}
