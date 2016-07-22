package com.cmcc.hyapps.andyou.model;

import java.util.List;

/**
 * Created by gaoruishan on 15/11/5.
 */
public class QHMyInfo {

    /**
     * count : 3
     * next : null
     * previous : null
     * results : [{"infoId":112,"infoText":"我的动态，动态是我的！！求点赞啊！！","longitude":101.851671359166,"latitude":36.5721334460024,"address":"北京市西城区西便门内大街","isPublic":1,"createTime":"2015-11-2 15:14:34","commentNum":168,"praiseNum":1880,"isPraised":1},{"infoId":113,"infoText":"我的动态，动态是我的！！求点赞啊！！","longitude":101.851671359166,"latitude":36.5721334460024,"address":"北京市西城区西便门内大街","isPublic":1,"createTime":"2015-11-2 15:14:34","commentNum":168,"praiseNum":1880,"isPraised":1},{"infoId":114,"infoText":"我的动态，动态是我的！！求点赞啊！！","longitude":101.851671359166,"latitude":36.5721334460024,"address":"北京市西城区西便门内大街","isPublic":1,"createTime":"2015-11-2 15:14:34","commentNum":168,"praiseNum":1880,"isPraised":0}]
     */

    private int count;
    private String next;
    private Object previous;
    /**
     * infoId : 112
     * infoText : 我的动态，动态是我的！！求点赞啊！！
     * longitude : 101.851671359166
     * latitude : 36.5721334460024
     * address : 北京市西城区西便门内大街
     * isPublic : 1
     * createTime : 2015-11-2 15:14:34
     * commentNum : 168
     * praiseNum : 1880
     * isPraised : 1
     */

    private List<ResultsEntity> results;

    public void setCount(int count) {
        this.count = count;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setPrevious(Object previous) {
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

    public Object getPrevious() {
        return previous;
    }

    public List<ResultsEntity> getResults() {
        return results;
    }

    public static class ResultsEntity {
        private int infoId;
        private String infoText;
        private double longitude;
        private double latitude;
        private String address;
        private int isPublic;
        private String createTime;
        private int commentNum;
        private int praiseNum;
        private int isPraised;

        public void setInfoId(int infoId) {
            this.infoId = infoId;
        }

        public void setInfoText(String infoText) {
            this.infoText = infoText;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setIsPublic(int isPublic) {
            this.isPublic = isPublic;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }

        public void setPraiseNum(int praiseNum) {
            this.praiseNum = praiseNum;
        }

        public void setIsPraised(int isPraised) {
            this.isPraised = isPraised;
        }

        public int getInfoId() {
            return infoId;
        }

        public String getInfoText() {
            return infoText;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getAddress() {
            return address;
        }

        public int getIsPublic() {
            return isPublic;
        }

        public String getCreateTime() {
            return createTime;
        }

        public int getCommentNum() {
            return commentNum;
        }

        public int getPraiseNum() {
            return praiseNum;
        }

        public int getIsPraised() {
            return isPraised;
        }
    }
}
