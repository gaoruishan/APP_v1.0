package com.cmcc.hyapps.andyou.data;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonRequest;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 2015/6/15.
 */
public class GsonObjectRequest extends JsonRequest<JSONArray> {
    public GsonObjectRequest(int method, String url, JSONArray jsonRequest,
                             Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    public GsonObjectRequest(String url, JSONArray jsonRequest, Listener<JSONArray> listener,
                             ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data,Charset.defaultCharset());
            JSONArray jsonObject = new JSONArray(jsonString);
            return Response.success(jsonObject,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
