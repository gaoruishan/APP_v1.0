
package com.cmcc.hyapps.andyou.data;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cmcc.hyapps.andyou.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.cmcc.hyapps.andyou.app.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Created by kuloud on 14-8-15.
 */
public class GsonRequest<T> extends Request<T> {
    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", ServerAPI.PROTOCOL_CHARSET);

    private  Gson mGson = new Gson();
    private final Class<T> mClass;
    private final Response.Listener<T> mListener;
    private final String mRequestBody;

    public static final int RESPONSE_OK = 0;

    public int getStateCode() {
        return stateCode;
    }

    private static final String RESPONSE_KEY_CODE = "code";
    private static final String RESPONSE_KEY_MESSAGE = "message";
    private static final String RESPONSE_KEY_DATA = "data";

    private int stateCode = 0;
    /**
     * Creates a new request with the given method (one of the values from
     * {@link com.android.volley.Request.Method}), URL, and error listener. Note
     * that the normal response listener is not provided here as delivery of
     * responses is provided by subclasses, who have a better idea of how to
     * deliver an already-parsed response.
     * 
     * @param url
     * @param errorListener
     * @param cls
     * @param listener
     */
    public GsonRequest(String url, Response.ErrorListener errorListener, Class<T> cls,
            Response.Listener<T> listener) {
        this(Method.GET, url, cls, null, listener, errorListener);
    }

    /**
     * Creates a new request with the given method (one of the values from
     * {@link com.android.volley.Request.Method}), URL, and error listener. Note
     * that the normal response listener is not provided here as delivery of
     * responses is provided by subclasses, who have a better idea of how to
     * deliver an already-parsed response.
     * 
     * @param method
     * @param url
     * @param errorListener
     * @param cls
     * @param requestBody
     * @param listener
     */
    public GsonRequest(int method, String url, Class<T> cls, String requestBody,
            Response.Listener<T> listener,
            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mRequestBody = requestBody;
        this.mClass = cls;
        this.mListener = listener;
    }

    /**
     * Subclasses must implement this to parse the raw network response and
     * return an appropriate response type. This method will be called from a
     * worker thread. The response will not be delivered if you return null.
     * 
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        stateCode = response.statusCode;
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            String json = new String(response.data, "UTF-8");
            if(json.contains(RESPONSE_KEY_CODE)){
                if (BuildConfig.DEBUG) {
                    android.util.Log.e("laws",":json数据："+json);
                }
                JSONObject jsonObj = new JSONObject(json);
                int retCode = jsonObj.getInt(RESPONSE_KEY_CODE);
                if (BuildConfig.DEBUG) {
                    android.util.Log.d("laws",":1^^^^^^^^^^^^^^"+retCode);
                }
                if (retCode != RESPONSE_OK ) {
                    return Response.error(new ResponseError(retCode, jsonObj
                            .getString(RESPONSE_KEY_MESSAGE)));
                } else{
                    JSONObject dataObj = jsonObj.getJSONObject(RESPONSE_KEY_DATA);
                    T t = mGson.fromJson(dataObj.toString(), mClass);
                    Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
//                    return Response.success(mGson.fromJson(dataObj.toString(), mClass),
//                            HttpHeaderParser.parseCacheHeaders(response));
                    return Response.success(t,entry);
                }
            }

            if (BuildConfig.DEBUG) {
                android.util.Log.e("laws",":json数据："+json);
            }
            if (json==null){
                return null;
            }
            JSONObject jsonObj = new JSONObject(json);
            T t = mGson.fromJson(jsonObj.toString(), mClass);
            return Response.success(t,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed response
     * to their listeners. The given response is guaranteed to be non-null;
     * responses that fail to parse are not delivered.
     * 
     * @param response The parsed response returned by
     *            {@link #parseNetworkResponse(com.android.volley.NetworkResponse)}
     */
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? super.getBody() : mRequestBody
                    .getBytes(ServerAPI.PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, ServerAPI.PROTOCOL_CHARSET);
        } catch (AuthFailureError e) {
            VolleyLog.wtf("AuthFailureError while trying to get the bytes of %s using %s",
                    mRequestBody, ServerAPI.PROTOCOL_CHARSET);
        }
        return null;
    }

    @Override
    public String getCacheKey() {
        Uri uri = Uri.parse(getUrl());
        Uri.Builder builder = Uri.parse(
                uri.getScheme() + "://" + uri.getAuthority() + uri.getPath()).buildUpon();
        Set<String> names = uri.getQueryParameterNames();
        if (names != null) {
            for (String param : names) {
                if ("latitude".equals(param) || "longitude".equals(param)) {
                    continue;
                }
                builder.appendQueryParameter(param, uri.getQueryParameter(param));
            }
        }

        return builder.build().toString();
    }

}
