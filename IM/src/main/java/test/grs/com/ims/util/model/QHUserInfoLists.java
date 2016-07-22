package test.grs.com.ims.util.model;

import java.util.List;

/**
 * Created by gaoruishan on 15/12/10.
 */
public class QHUserInfoLists {

    /**
     * avatar_url : http://112.54.207.49/media/user/201512/767/1562D571AA3749D3989F1C53522CF0E7
     * date_joined :
     * email :
     * gender : 0
     * hasAttentioned : 0
     * id : 767
     * introduction :
     * isAttentionedNum : 0
     * isBlackUser : 0
     * is_active :
     * is_superuser : 0
     * last_login :
     * location_area :
     * location_provnice :
     * nick_name : 未命名
     * password :
     * payAttentionNum : 0
     * result : 0
     * url :
     * userId : 0
     * user_info : {"avatar_url":"http://112.54.207.49/media/user/201512/767/1562D571AA3749D3989F1C53522CF0E7","date_joined":"","email":"","gender":0,"hasAttentioned":0,"id":767,"introduction":"你若晴天，我便不带伞。","isAttentionedNum":0,"isBlackUser":0,"is_active":"","is_superuser":0,"last_login":"","location_area":"福州市","location_provnice":"福建省","nickname":"未命名","password":"","payAttentionNum":0,"url":"","userId":0,"user_info":null,"username":"18911113333"}
     * username : 18911113333
     */

    private List<ResultsEntity> results;

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        private String avatar_url;
        private String date_joined;
        private String email;
        private int gender;
        private int hasAttentioned;
        private int id;
        private String introduction;
        private int isAttentionedNum;
        private int isBlackUser;
        private String is_active;
        private int is_superuser;
        private String last_login;
        private String location_area;
        private String location_provnice;
        private String nick_name;
        private String password;
        private int payAttentionNum;
        private int result;
        private String url;
        private int userId;
        /**
         * avatar_url : http://112.54.207.49/media/user/201512/767/1562D571AA3749D3989F1C53522CF0E7
         * date_joined :
         * email :
         * gender : 0
         * hasAttentioned : 0
         * id : 767
         * introduction : 你若晴天，我便不带伞。
         * isAttentionedNum : 0
         * isBlackUser : 0
         * is_active :
         * is_superuser : 0
         * last_login :
         * location_area : 福州市
         * location_provnice : 福建省
         * nickname : 未命名
         * password :
         * payAttentionNum : 0
         * url :
         * userId : 0
         * user_info : null
         * username : 18911113333
         */

        private UserInfoEntity user_info;
        private String username;

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public void setDate_joined(String date_joined) {
            this.date_joined = date_joined;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setHasAttentioned(int hasAttentioned) {
            this.hasAttentioned = hasAttentioned;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public void setIsAttentionedNum(int isAttentionedNum) {
            this.isAttentionedNum = isAttentionedNum;
        }

        public void setIsBlackUser(int isBlackUser) {
            this.isBlackUser = isBlackUser;
        }

        public void setIs_active(String is_active) {
            this.is_active = is_active;
        }

        public void setIs_superuser(int is_superuser) {
            this.is_superuser = is_superuser;
        }

        public void setLast_login(String last_login) {
            this.last_login = last_login;
        }

        public void setLocation_area(String location_area) {
            this.location_area = location_area;
        }

        public void setLocation_provnice(String location_provnice) {
            this.location_provnice = location_provnice;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setPayAttentionNum(int payAttentionNum) {
            this.payAttentionNum = payAttentionNum;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setUser_info(UserInfoEntity user_info) {
            this.user_info = user_info;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public String getDate_joined() {
            return date_joined;
        }

        public String getEmail() {
            return email;
        }

        public int getGender() {
            return gender;
        }

        public int getHasAttentioned() {
            return hasAttentioned;
        }

        public int getId() {
            return id;
        }

        public String getIntroduction() {
            return introduction;
        }

        public int getIsAttentionedNum() {
            return isAttentionedNum;
        }

        public int getIsBlackUser() {
            return isBlackUser;
        }

        public String getIs_active() {
            return is_active;
        }

        public int getIs_superuser() {
            return is_superuser;
        }

        public String getLast_login() {
            return last_login;
        }

        public String getLocation_area() {
            return location_area;
        }

        public String getLocation_provnice() {
            return location_provnice;
        }

        public String getNick_name() {
            return nick_name;
        }

        public String getPassword() {
            return password;
        }

        public int getPayAttentionNum() {
            return payAttentionNum;
        }

        public int getResult() {
            return result;
        }

        public String getUrl() {
            return url;
        }

        public int getUserId() {
            return userId;
        }

        public UserInfoEntity getUser_info() {
            return user_info;
        }

        public String getUsername() {
            return username;
        }

        public static class UserInfoEntity {
            private String avatar_url;
            private String date_joined;
            private String email;
            private int gender;
            private int hasAttentioned;
            private int id;
            private String introduction;
            private int isAttentionedNum;
            private int isBlackUser;
            private String is_active;
            private int is_superuser;
            private String last_login;
            private String location_area;
            private String location_provnice;
            private String nickname;
            private String password;
            private int payAttentionNum;
            private String url;
            private int userId;
            private Object user_info;
            private String username;

            public void setAvatar_url(String avatar_url) {
                this.avatar_url = avatar_url;
            }

            public void setDate_joined(String date_joined) {
                this.date_joined = date_joined;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public void setHasAttentioned(int hasAttentioned) {
                this.hasAttentioned = hasAttentioned;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setIntroduction(String introduction) {
                this.introduction = introduction;
            }

            public void setIsAttentionedNum(int isAttentionedNum) {
                this.isAttentionedNum = isAttentionedNum;
            }

            public void setIsBlackUser(int isBlackUser) {
                this.isBlackUser = isBlackUser;
            }

            public void setIs_active(String is_active) {
                this.is_active = is_active;
            }

            public void setIs_superuser(int is_superuser) {
                this.is_superuser = is_superuser;
            }

            public void setLast_login(String last_login) {
                this.last_login = last_login;
            }

            public void setLocation_area(String location_area) {
                this.location_area = location_area;
            }

            public void setLocation_provnice(String location_provnice) {
                this.location_provnice = location_provnice;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public void setPayAttentionNum(int payAttentionNum) {
                this.payAttentionNum = payAttentionNum;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public void setUser_info(Object user_info) {
                this.user_info = user_info;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getAvatar_url() {
                return avatar_url;
            }

            public String getDate_joined() {
                return date_joined;
            }

            public String getEmail() {
                return email;
            }

            public int getGender() {
                return gender;
            }

            public int getHasAttentioned() {
                return hasAttentioned;
            }

            public int getId() {
                return id;
            }

            public String getIntroduction() {
                return introduction;
            }

            public int getIsAttentionedNum() {
                return isAttentionedNum;
            }

            public int getIsBlackUser() {
                return isBlackUser;
            }

            public String getIs_active() {
                return is_active;
            }

            public int getIs_superuser() {
                return is_superuser;
            }

            public String getLast_login() {
                return last_login;
            }

            public String getLocation_area() {
                return location_area;
            }

            public String getLocation_provnice() {
                return location_provnice;
            }

            public String getNickname() {
                return nickname;
            }

            public String getPassword() {
                return password;
            }

            public int getPayAttentionNum() {
                return payAttentionNum;
            }

            public String getUrl() {
                return url;
            }

            public int getUserId() {
                return userId;
            }

            public Object getUser_info() {
                return user_info;
            }

            public String getUsername() {
                return username;
            }
        }
    }
}
