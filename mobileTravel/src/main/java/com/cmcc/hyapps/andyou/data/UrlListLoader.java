
package com.cmcc.hyapps.andyou.data;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.Paginator;
import com.cmcc.hyapps.andyou.model.ResultList;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.CommonUtils;
import com.cmcc.hyapps.andyou.util.Log;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UrlListLoader<T extends ResultList> implements DataLoader<T> {
    private String mRequestTag;
    private Paginator mPaginator = new Paginator();
    private String mUrl;
    private GsonRequest<T> mRequestInFly;
    private GsonRequestForAESEncrpt<T> mTGsonRequestForAESEncrpt;
    private Class<T> mClazz;
    private int mPageLimit = Const.LIST_DEFAULT_LOAD_ITEMS;
    private boolean mUseCacae = false;
    protected boolean hasNext = true;
    protected String nextUrl;
    public int allcount;

    public UrlListLoader(String requestTag, Class<T> clazz) {
        mRequestTag = requestTag;
        mClazz = clazz;
    } public UrlListLoader(String requestTag, Class<T> clazz,Paginator mPaginator ) {
        mRequestTag = requestTag;
        mClazz = clazz;
        this.mPaginator = mPaginator;
    }

    public void setUseCache(boolean cache) {
        mUseCacae = cache;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setPageLimit(int limit) {
        if (limit >= 0) {
            mPageLimit = limit;
        }
    }

    public Paginator getPaginator() {
        return mPaginator;
    }

    @Override
    public void loadMoreData(final DataLoader.DataLoaderCallback<T> cb, final int mode) {
        if (mode == DataLoader.MODE_REFRESH) {
            mPaginator.reset();
        } else if (!mPaginator.hasMorePages()) {
            cb.onLoadFinished(null, mode);
            return;
        }
        if (!CommonUtils.isValidUrl(mUrl)) {
            throw new IllegalArgumentException("Illegal url:" + mUrl);
        }
        checkUrlIsSpecial();
        final String url = ServerAPI.appendListParams(mUrl, mPageLimit, mPaginator.nextLoadOffset());
        Log.d("Loading list from %s", url);
        mRequestInFly = RequestManager.getInstance().sendGsonRequest(Method.GET, url, mClazz, null,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T responseList) {
                        Log.d("***** xxxxxx list loaded, response=%s",
                                responseList);
                        mPaginator.addPage(responseList.pagination);
                        cb.onLoadFinished(responseList, mode);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Load list error, error=%s", error);
                        cb.onLoadError(mode);
                        if (mRequestInFly != null) {
                            mRequestInFly.markDelivered();
                        }
                    }
                }, mUseCacae, mRequestTag);
    }

    public void loadMoreQHData(final DataLoader.DataLoaderCallback<T> cb, final int mode){

        if(mode == DataLoader.MODE_LOAD_MORE && hasNext != true){
            cb.onLoadFinished(null, mode);
            return;
        }

        if (!CommonUtils.isValidUrl(mUrl)) {
            throw new IllegalArgumentException("Illegal url:" + mUrl);
        }
        checkUrlIsSpecial();
//        final String url = ServerAPI.appendListParams(mUrl, mPageLimit, mPaginator.nextLoadOffset());
        String url = mUrl;
        if(mode == MODE_LOAD_MORE && !TextUtils.isEmpty(nextUrl)){
            url = nextUrl;
        }
        //这样处理是因为volley请求的参数中带中文会乱码。eg 关键字搜索
        if (url.contains("?")){
            Uri uri = Uri.parse(url);
            Set<String> stringSet = uri.getQueryParameterNames();
            Map<String,String> params = new HashMap<String, String>();
            if (stringSet != null){
                for (String item : stringSet) {
                    String value = uri.getQueryParameter(item);
                    params.put(item,value);
                }

            }
            String path = UrlPage(url);
            Uri.Builder builder = Uri.parse(path).buildUpon();
            for (Map.Entry<String,String> item : params.entrySet()){
                builder.appendQueryParameter(item.getKey(), item.getValue());
            }
            url = builder.build().toString();
        }
        Log.d("Loading list from %s", url);
        mRequestInFly = RequestManager.getInstance().sendGsonRequest(Method.GET, url, mClazz, null,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T responseList) {
                        Log.d("***** xxxxxx list loaded, response=%s",
                                responseList);
                        nextUrl = responseList.next;
                        allcount = responseList.count;
                        if (TextUtils.isEmpty(nextUrl)) {
                            hasNext = false;
                        } else {
                            hasNext = true;
                        }
//                        mPaginator.addPage(responseList.next);
                        cb.onLoadFinished(responseList, mode);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Load list error, error=%s", error);
                        cb.onLoadError(mode);
                        if (mRequestInFly != null) {
                            mRequestInFly.markDelivered();
                        }
                    }
                }, mUseCacae, mRequestTag);
    }

    public void loadMoreQHDataAES(final DataLoader.DataLoaderCallback<T> cb, final int mode){

        if(mode == DataLoader.MODE_LOAD_MORE && hasNext != true){
            cb.onLoadFinished(null, mode);
            return;
        }

        if (!CommonUtils.isValidUrl(mUrl)) {
            throw new IllegalArgumentException("Illegal url:" + mUrl);
        }
        checkUrlIsSpecial();
//        final String url = ServerAPI.appendListParams(mUrl, mPageLimit, mPaginator.nextLoadOffset());
        String url = mUrl;
        if(mode == MODE_LOAD_MORE && !TextUtils.isEmpty(nextUrl)){
            url = nextUrl;
        }
        Log.d("Loading list from %s", url);
        mTGsonRequestForAESEncrpt = RequestManager.getInstance().sendGsonRequestAES(Method.GET, url, mClazz, null,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T responseList) {
                        Log.d("***** xxxxxx list loaded, response=%s",
                                responseList);
                        nextUrl = responseList.next;
                        allcount = responseList.count;
                        if (TextUtils.isEmpty(nextUrl)) {
                            hasNext = false;
                        } else {
                            hasNext = true;
                        }
//                        mPaginator.addPage(responseList.next);
                        cb.onLoadFinished(responseList, mode);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Load list error, error=%s", error);
                        cb.onLoadError(mode);
                        if (mTGsonRequestForAESEncrpt != null) {
                            mTGsonRequestForAESEncrpt.markDelivered();
                        }
                    }
                }, mUseCacae, mRequestTag, AppUtils.dynamicKey);
    }

    public boolean delivered() {
        if (mRequestInFly != null) {
            return mRequestInFly.hasHadResponseDelivered();
        }
        return false;
    }

    public void cancel() {
        if (mRequestInFly != null && !mRequestInFly.isCanceled()) {
            mRequestInFly.cancel();
        }
    }

    @Override
    public void onLoaderDestory() {
        cancel();
    }

    /*判断是否是特殊接口*/
    public void checkUrlIsSpecial(){
        if(mUrl.contains("scenic_spots")){//景点列表默认一次加载200
            mPageLimit = Const.LIST_SECNIC_SPOTS_LOAD_ITEMS;
        }
    }

    public static String UrlPage(String strURL)
    {
        String strPage=null;
        String[] arrSplit=null;

        strURL=strURL.trim().toLowerCase();

        arrSplit=strURL.split("[?]");
        if(strURL.length()>0)
        {
            if(arrSplit.length>1)
            {
                if(arrSplit[0]!=null)
                {
                    strPage=arrSplit[0];
                }
            }
        }

        return strPage;
    }

}
