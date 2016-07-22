package com.cmcc.hyapps.andyou.model;

/**
 * Created by gaoruishan on 15/11/9.
 */
public class QHFriendToken {

    /**
     * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IjE4NjEwNjc2MjgxIiwib3JpZ19pYXQiOjE0MzUxMzI3MjIsInVzZXJfaWQiOjYsImVtYWlsIjoiIiwiZXhwIjoxNDM1MzA1NTIyfQ.dFGWUMimy7rtAY9yK02hxbQq5RVdDvylaCGRmjfcfnk
     * jsessionid : 5F4771183629C9834F8382E23BE13C4C
     */

    private String token;
    private String jsessionid;

    public void setToken(String token) {
        this.token = token;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    public String getToken() {
        return token;
    }

    public String getJsessionid() {
        return jsessionid;
    }

    @Override
    public String toString() {
        return "QHFriendToken{" +
                "token='" + token + '\'' +
                ", jsessionid='" + jsessionid + '\'' +
                '}';
    }
}
