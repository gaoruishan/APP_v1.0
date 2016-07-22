package test.grs.com.ims.util.model;


import java.util.List;

/**
 * Created by Administrator on 2015/5/29.
 */
public class QHAttention {

    /**
     * nickname : 189****1111
     * userId : 252
     * avatarUrl : http://xxxx/xxxx/xxx.jpg
     * isfriend : 0
     * introduction : 论演员的自我修养
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
        private int isfriend;
        private String introduction;

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setIsfriend(int isfriend) {
            this.isfriend = isfriend;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
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

        public int getIsfriend() {
            return isfriend;
        }

        public String getIntroduction() {
            return introduction;
        }

        @Override
        public String toString() {
            return "ResultsEntity{" +
                    "nickname='" + nickname + '\'' +
                    ", userId='" + userId + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", isfriend=" + isfriend +
                    ", introduction='" + introduction + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QHAttention{" +
                "results=" + results +
                '}';
    }
}
