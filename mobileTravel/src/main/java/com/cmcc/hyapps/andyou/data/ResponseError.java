
package com.cmcc.hyapps.andyou.data;

import com.android.volley.VolleyError;

@SuppressWarnings("serial")
public class ResponseError extends VolleyError {
    public final String message;
    public final int errCode;

    public ResponseError() {
        super();
        errCode = -1;
        message = "";
    }

    public ResponseError(int errCode, String message) {
        super();
        this.errCode = errCode;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseError [message=" + message + ", errCode=" + errCode + "]";
    }

}
