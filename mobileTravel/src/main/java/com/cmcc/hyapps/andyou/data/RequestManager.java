
package com.cmcc.hyapps.andyou.data;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.cmcc.hyapps.andyou.BuildConfig;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.QHFriendToken;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.umeng.message.proguard.T;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestManager单例设计模式
 * <p/>
 * Created by kuloud on 14-8-15.
 */
public class RequestManager {
    private static RequestManager sInstance = new RequestManager();
    private Context mContext;

    private RequestManager() {
    }


    public static RequestManager getInstance() {
        return sInstance;
    }

    //volley的网络请求队列
    private RequestQueue mRequestQueue;
    //volley的图片加载器
    private ImageLoader mImageLoader;

    //快速请求队列，区别？
    private RequestQueue mSoapRequestQueue;
    //volley的http堆，用来干嘛？执行http请求，得到响应信息，并放到mSoapHttpStack中
    private HttpStack mSoapHttpStack = new HttpStack() {

        @Override
        public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
                throws IOException, AuthFailureError {

            SoapRequest r = (SoapRequest) request;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = r.getSoapObject();
            envelope.setOutputSoapObject(r.getSoapObject());

            HttpResponse res = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion(
                    "HTTP", 1, 1), 200, ""));
            HttpTransportSE transport = new HttpTransportSE(r.getUrl());
            try {
                transport.call("", envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                res.setEntity(entityFromConnection(r, result));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }
    };

    //将服务器返回的response实体化
    private HttpEntity entityFromConnection(SoapRequest request, SoapObject result)
            throws UnsupportedEncodingException {
        BasicHttpEntity entity = new BasicHttpEntity();
        String content = "";
        // TODO: find a better solution
        if (ServerAPI.LiveVideos.METHOD_GET_PLAY_URL.equals(request.getSoapObject().getName())) {
            if (result.getPropertyCount() == 2) {
                content = result.getPropertyAsString("rtspUrl");
            }
        } else if (ServerAPI.LiveVideos.METHOD_GET_PIC_LIST.equals(request.getSoapObject()
                .getName())) {
            if (result.getPropertyCount() == 2) {
                if (result.getProperty(1) instanceof SoapObject) {
                    SoapObject picList = (SoapObject) result.getProperty(1);
                    if (picList.getPropertyCount() > 0) {
                        SoapObject picNode = (SoapObject) picList.getProperty(0);
                        content = picNode.getPropertyAsString("picUrl");
                    }
                }
            }
        }

        entity.setContent(new ByteArrayInputStream(content.getBytes(
                "UTF-8")));
        entity.setContentLength(content.length());
        // entity.setContentEncoding(connection.getContentEncoding());
        // entity.setContentType(connection.getContentType());
        return entity;
    }

    public void init(Context context) {

        mRequestQueue = Volley.newRequestQueue(context);
        mContext = context;

        int memCls = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/4th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memCls;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));

        mSoapRequestQueue = Volley.newRequestQueue(context, mSoapHttpStack);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    public void addSoapRequest(SoapRequest request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mSoapRequestQueue.add(request);
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache}
     * which effectively means that no memory caching is used. This is useful
     * for images that you know that will be show only once.
     *
     * @return
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }



    /**
     * Get original image.
     *
     * @param url
     * @param listener
     * @param errorListener
     * @param tag
     */
    public ImageRequest requestImage(String url,
                                     Response.Listener<Bitmap> listener,
                                     Response.ErrorListener errorListener, Object tag) {
        return requestImage(url, listener, errorListener, 0, 0, tag);
    }

    /**
     * Get image with given max width/height.
     *
     * @param url
     * @param listener
     * @param errorListener
     * @param maxWidth
     * @param maxHeight
     * @param tag
     */
    public ImageRequest requestImage(String url,
                                     Response.Listener<Bitmap> listener,
                                     Response.ErrorListener errorListener, int maxWidth, int maxHeight, Object tag) {
        ImageRequest imgRequest = new ImageRequest(url, listener, maxWidth, maxHeight,
                Config.ARGB_4444, errorListener);
        addRequest(imgRequest, tag);
        return imgRequest;
    }

    /**
     * Loading a image and dismiss the progress bar after finish loading.
     *
     * @param url
     * @param imageView
     * @param loadingView
     * @param tag
     * @return
     */
    public ImageRequest loadImage(String url, final ImageView imageView, final View loadingView,
                                  Object tag) {
        return requestImage(url, new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                imageView.setImageBitmap(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                imageView.setImageResource(R.drawable.bg_image_error);
            }
        }, 0, 0, tag);
    }

    public <T> GsonRequest<T> sendGsonRequest(int method, String url, Class<T> cls, String body,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, boolean deliverExpiredCache, Object tag) {
        return sendGsonRequest(method, url, cls, body, listener, errorListener,
                deliverExpiredCache, null, null);
    }

    /**
     * Send post request with params
     *
     * @param url
     * @param cls
     * @param listener
     * @param errorListener
     * @param deliverExpiredCache
     * @param params
     */
    public <T> void sendGsonRequest(String url, Class<T> cls,
                                    Response.Listener<T> listener, Response.ErrorListener errorListener,
                                    boolean deliverExpiredCache, Map<String, String> params, Object tag) {
        sendGsonRequest(Method.POST, url, cls, null, listener, errorListener, deliverExpiredCache,
                params, tag);
    }
    public <T> void sendMultipartGsonRequest(String url, Class<T> cls,Response.Listener<T> listener, Response.ErrorListener errorListener,
                                    boolean deliverExpiredCache, Map<String, String> params, Object tag) {
        sendGsonMultipartRequest(Method.POST, url, cls, null, listener, errorListener, deliverExpiredCache, params, tag);
    }
    public <T> GsonRequest<T> sendGsonRequest(int method, String url, Class<T> cls, String body,
                                              Response.Listener<T> listener, Response.ErrorListener errorListener,
                                              boolean deliverExpiredCache, final Map<String, String> params, Object tag) {
        // Now send the request to fetch data
        if (BuildConfig.DEBUG) {
            android.util.Log.e("联网请求url:",""+url);
        }
        if(null!=params&&params.size()>0){
            String strPar = ConstTools.map2string(params);
        }

        GsonRequest<T> gsonRequest = new GsonRequest<T>(method, url, cls, body, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String uid = AppUtils.getUid(mContext);
                if (!TextUtils.isEmpty(uid)) {
                    headers.put("x-uid", uid);
//                    Content-type: application/x-www-form-urlencoded
                    headers.put("Content-Type","application/json");
//                    headers.put("Content-Type","application/x-www-form-urlencoded");
                }
                if(AppUtils.getTokenInfo(mContext)!=null){
                    QHToken tokenInfo = AppUtils.getQHToken(mContext);
                    if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                     //   headers.put("Authorization", "JWT " + tokenInfo.token);
                        headers.put("Authorization", tokenInfo.token);
                        //添加 jsessionid
                        if (AppUtils.mjsessionid!=null){
                            headers.put("Cookie", "JSESSIONID=" + AppUtils.mjsessionid);
                        }else {
//                            QHFriendToken token = AppUtils.getFriendToken(mContext);
//                            String jsessionid = token.getJsessionid();
                            if (!TextUtils.isEmpty(AppUtils.mjsessionid)){
                                headers.put("Cookie", "JSESSIONID=" + AppUtils.mjsessionid);
                            }
                        }
                    }
                }

                return headers;
            }

//            @Override
//            public RetryPolicy getRetryPolicy() {
//                RetryPolicy retryPolicy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                return retryPolicy;
//            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public String getBodyContentType() {
                // If params not null, set content-type form
                return (params == null) ? super.getBodyContentType()
                        : "application/json; charset="
                        + getParamsEncoding();
            }
        };

        boolean cacheDeliverd = false;
        if (deliverExpiredCache) {
            Cache cache = getRequestQueue().getCache();
            Entry entry = cache.get(gsonRequest.getCacheKey());
            if (entry != null/** && entry.isExpired() */
                    ) {
                Log.d("[CACHE] Using expired cache for url %s", url);
                Response<T> response = gsonRequest.parseNetworkResponse(
                        new NetworkResponse(entry.data, entry.responseHeaders));
                android.util.Log.i("law", "RequestManager+" + response.toString());
                gsonRequest.deliverResponse(response.result);
                cacheDeliverd = true;
            }
        }

        if (!cacheDeliverd && !NetUtils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
            gsonRequest.deliverError(new NoConnectionError());
        }

        addRequest(gsonRequest, tag);
        if (BuildConfig.DEBUG) {
            Log.d("Sending gson request for url %s", url);
        }

        return gsonRequest;
    }
    public <T> GsonRequest<T> sendGsonMultipartRequest(int method, String url, Class<T> cls, String body,
                                              Response.Listener<T> listener, Response.ErrorListener errorListener,
                                              boolean deliverExpiredCache, final Map<String, String> params, Object tag) {
        // Now send the request to fetch data
        android.util.Log.e("联网请求url:",""+url);
        if(null!=params&&params.size()>0){
            String strPar = ConstTools.map2string(params);
            android.util.Log.e("联网请求params:",""+strPar);
        }

        GsonRequest<T> gsonRequest = new GsonRequest<T>(method, url, cls, body, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String uid = AppUtils.getUid(mContext);
                if (!TextUtils.isEmpty(uid)) {
                    headers.put("x-uid", uid);
                    headers.put("Content-Type","multipart/form-data");
                }
                QHToken tokenInfo = AppUtils.getQHToken(mContext);
                if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                   // headers.put("Authorization", "JWT " + tokenInfo.token);
                    headers.put("Authorization", tokenInfo.token);
                }
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public String getBodyContentType() {
                // If params not null, set content-type form
                return (params == null) ? super.getBodyContentType()
                        : "application/json; charset="
                        + getParamsEncoding();
            }
        };

        boolean cacheDeliverd = false;
        if (deliverExpiredCache) {
            Cache cache = getRequestQueue().getCache();
            Entry entry = cache.get(gsonRequest.getCacheKey());
            if (entry != null/** && entry.isExpired() */
                    ) {
                Log.d("[CACHE] Using expired cache for url %s", url);
                Response<T> response = gsonRequest.parseNetworkResponse(
                        new NetworkResponse(entry.data, entry.responseHeaders));
                android.util.Log.i("law", "RequestManager+" + response.toString());
                gsonRequest.deliverResponse(response.result);
                cacheDeliverd = true;
            }
        }

        if (!cacheDeliverd && !NetUtils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
//            gsonRequest.deliverError(new NoConnectionError());
        }
//        if(!cacheDeliverd)//使用缓存就不在联网，否则掉两次onresponse，导致后面的联网连续请求两次。

        //有网络的时候从新请求，避免没网络的时候报 volleyError.
        if(NetUtils.isNetworkAvailable(mContext))addRequest(gsonRequest, tag);
        Log.d("Sending gson request for url %s", url);

        return gsonRequest;
    }
    public <T> GsonRequest<T> sendGsonRequest(String url, Class<T> cls,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, Object tag) {
        return sendGsonRequest(Method.GET, url, cls, null, listener, errorListener, false, tag);
    }

    public <T> GsonRequest<T> sendGsonRequest(int method, String url, String body, Class<T> cls,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, Object tag) {
        return sendGsonRequest(method, url, cls, body, listener, errorListener, false, tag);
    }

    public interface RequestCallback {
        public byte[] getBody();

        public String getBodyContentType();

        public Map<String, String> getHeaders();

        public Map<String, String> getParams();
    }

    /**
     * get
     * @param url
     * @param cls
     * @param listener
     * @param errorListener
     * @param tag
     * @param key
     * @param <T>
     * @return
     */
    public <T> GsonRequestForAESEncrpt<T> sendGsonRequestAESforGET(String url, Class<T> cls,
                                              Response.Listener<T> listener,
                                              Response.ErrorListener errorListener, Object tag,String key) {
        return sendGsonRequestAES(Method.GET, url, cls, null, listener, errorListener, false, tag,key);
    }

    /**
     * post
     * @param url
     * @param cls
     * @param listener
     * @param errorListener
     * @param tag
     * @param key
     * @param <T>
     * @return
     */

    public <T> GsonRequestForAESEncrpt<T> sendGsonRequestAESforPOST(String url, Class<T> cls,String body,
                                                                    Response.Listener<T> listener,
                                                                    Response.ErrorListener errorListener, Object tag,String key){
        return sendGsonRequestAES(Method.POST, url, cls, body, listener, errorListener, false, tag,key);
    }

    /**
     * 用于朋友圈的请求加密处理
     * @param method
     * @param url
     * @param cls
     * @param body
     * @param listener
     * @param errorListener
     * @param deliverExpiredCache
     * @param tag
     * @param <T>
     * @return
     */
    public <T> GsonRequestForAESEncrpt<T> sendGsonRequestAES(int method, String url, Class<T> cls,final String body,
                                              Response.Listener<T> listener, Response.ErrorListener errorListener,
                                              boolean deliverExpiredCache, Object tag,String key) {
        // Now send the request to fetch data
        if (BuildConfig.DEBUG) {
            android.util.Log.e("联网请求url:",""+url);
        }
        GsonRequestForAESEncrpt<T> gsonRequest = new GsonRequestForAESEncrpt<T>(method, url, cls, body, listener, errorListener,key) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String uid = AppUtils.getUid(mContext);
                if (!TextUtils.isEmpty(uid)) {
                    headers.put("x-uid", uid);
//                    Content-type: application/x-www-form-urlencoded
                    headers.put("Content-Type","application/json");
//                    headers.put("Content-Type","application/x-www-form-urlencoded");
                }
                if(AppUtils.getTokenInfo(mContext)!=null){
                    QHToken tokenInfo = AppUtils.getQHToken(mContext);
                    if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                        //   headers.put("Authorization", "JWT " + tokenInfo.token);
                        headers.put("Authorization", tokenInfo.token);
                        //添加 jsessionid
                        if (AppUtils.mjsessionid!=null){
                            headers.put("Cookie", "JSESSIONID=" + AppUtils.mjsessionid);
                        }else {
//                            QHFriendToken token = AppUtils.getFriendToken(mContext);
//                            String jsessionid = token.getJsessionid();
                            if (!TextUtils.isEmpty(AppUtils.mjsessionid)){
                                headers.put("Cookie", "JSESSIONID=" + AppUtils.mjsessionid);
                            }
                        }
                    }
                }

                return headers;
            }

            @Override
            public String getBodyContentType() {
                // If params not null, set content-type form
                return (body != null) ? super.getBodyContentType()
                        : "application/json; charset="
                        + getParamsEncoding();
            }
        };

        boolean cacheDeliverd = false;
        if (deliverExpiredCache) {
            Cache cache = getRequestQueue().getCache();
            Entry entry = cache.get(gsonRequest.getCacheKey());
            if (entry != null/** && entry.isExpired() */
                    ) {
                Log.d("[CACHE] Using expired cache for url %s", url);
                Response<T> response = gsonRequest.parseNetworkResponse(
                        new NetworkResponse(entry.data, entry.responseHeaders));
                android.util.Log.i("law", "RequestManager+" + response.toString());
                gsonRequest.deliverResponse(response.result);
                cacheDeliverd = true;
            }
        }

        if (!cacheDeliverd && !NetUtils.isNetworkAvailable(mContext)) {
            Toast.makeText(mContext, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show();
            gsonRequest.deliverError(new NoConnectionError());
        }

        addRequest(gsonRequest, tag);
        if (BuildConfig.DEBUG) {
            Log.d("Sending gson request for url %s", url);
        }

        return gsonRequest;
    }

    public String appendParameter(String url,Map<String,String> params){
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for(Map.Entry<String,String> entry:params.entrySet()){
            builder.appendQueryParameter(entry.getKey(),entry.getValue());
        }
        return builder.build().getQuery();
    }
}
