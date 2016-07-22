package test.grs.com.ims.util.model;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/9.
 */
public class QHRecommends {

    /**
     * count : 0
     * results : [{"userId":40,"username":"15158462561","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-06-29 16:29:46","nickname":"my000000040","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-06-29 16:29:46"},{"userId":581,"username":"13709759467","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-09-02 17:35:24","nickname":"my000000581","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-09-02 17:35:24"},{"userId":391,"username":"13909758233","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/avatar/7051a8028fb69a8baf81350427ddab55_AL3pY4C","lastLogin":"2015-08-09 17:29:16","nickname":"my000000391","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-08-09 17:29:16"},{"userId":83,"username":"18852362021","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-07-18 14:25:52","nickname":"my000000083","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-07-18 14:25:52"},{"userId":621,"username":"13997338409","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-09-25 17:20:45","nickname":"my000000621","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-09-25 17:20:45"},{"userId":426,"username":"15109752127","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-08-12 11:45:30","nickname":"my000000426","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-08-12 11:45:30"},{"userId":90,"username":"13709759480","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/avatar/368c19a87abc69285b02a464e2b1839b","lastLogin":"2015-07-23 14:12:16","nickname":"小调","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-07-23 14:12:16"},{"userId":191,"username":"14797029538","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-08-05 10:46:14","nickname":"玩神","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-08-05 10:46:14"},{"userId":416,"username":"13897769674","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/","lastLogin":"2015-08-11 13:45:41","nickname":"my000000416","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-08-11 13:45:41"},{"userId":62,"username":"13811428725","password":"B0CDA560DFB07267DA65855A8D4F4BFA","avatarUrl":"http://111.13.97.77/media/avatar/ddc1518eea8766225b48bf39f5d545eb","lastLogin":"2015-07-08 17:49:31","nickname":"噜啦啦","gender":0,"email":"","locationProvnice":null,"locationArea":null,"introduction":null,"dateJoined":"2015-07-08 17:49:31"}]
     * next : http://127.0.0.1:8080/friends/getRecommendUsers.do?limit=10&offset=50
     * previous : http://127.0.0.1:8080/friends/getRecommendUsers.do?limit=10&offset=30
     */

    private int count;
    private String next;
    private String previous;
    /**
     * userId : 40
     * username : 15158462561
     * password : B0CDA560DFB07267DA65855A8D4F4BFA
     * avatarUrl : http://111.13.97.77/media/
     * lastLogin : 2015-06-29 16:29:46
     * nickname : my000000040
     * gender : 0
     * email :
     * locationProvnice : null
     * locationArea : null
     * introduction : null
     * dateJoined : 2015-06-29 16:29:46
     */

    private List<ResultsEntity> results;

    public void setCount(int count) {
        this.count = count;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public void setResults(List<ResultsEntity> results) {
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        private int userId;
        private String username;
        private String password;
        private String avatarUrl;
        private String lastLogin;
        private String nickname;
        private int gender;
        private String email;
//        private Object locationProvnice;
//        private String locationArea;
//        private String introduction;
        private String dateJoined;

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setEmail(String email) {
            this.email = email;
        }

//        public void setLocationProvnice(String locationProvnice) {
//            this.locationProvnice = locationProvnice;
//        }
//
//        public void setLocationArea(String locationArea) {
//            this.locationArea = locationArea;
//        }
//
//        public void setIntroduction(String introduction) {
//            this.introduction = introduction;
//        }

        public void setDateJoined(String dateJoined) {
            this.dateJoined = dateJoined;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public String getNickname() {
            return nickname;
        }

        public int getGender() {
            return gender;
        }

        public String getEmail() {
            return email;
        }

//
//        public String getLocationProvnice() {
//            return locationProvnice;
//        }
//
//        public String getLocationArea() {
//            return locationArea;
//        }
//
//        public String getIntroduction() {
//            return introduction;
//        }

        public String getDateJoined() {
            return dateJoined;
        }

        @Override
        public String toString() {
            return "ResultsEntity{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", lastLogin='" + lastLogin + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", gender=" + gender +
                    ", email='" + email + '\'' +
                    ", dateJoined='" + dateJoined + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QHRecommends{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                ", results=" + results +
                '}';
    }
}
