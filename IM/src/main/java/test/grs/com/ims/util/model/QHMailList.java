package test.grs.com.ims.util.model;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHMailList {

    /**
     * nickname : 189****1111
     * userId : 252
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     * phoneNum : 18911111111
     */

    private List<ResultsEntity> results;

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        private String nickname;
        private String userId;
        private String avatarUrl;
        private String phoneNum;

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getNickname() {
            return nickname;
        }

        public String getUserId() {
            return userId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        @Override
        public String toString() {
            return "ResultsEntity{" +
                    "nickname='" + nickname + '\'' +
                    ", userId='" + userId + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", phoneNum='" + phoneNum + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QHMailList{" +
                "results=" + results +
                '}';
    }
}
