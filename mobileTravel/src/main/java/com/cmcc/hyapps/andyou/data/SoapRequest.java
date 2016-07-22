
package com.cmcc.hyapps.andyou.data;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.ksoap2.serialization.SoapObject;

public class SoapRequest extends StringRequest {

    public interface Listener<T> {
        public void onResponse(int liveId, T response);
    }

    private SoapObject mSoapObject;
    private Listener<String> mListener;
    private int mLiveId;

    public SoapRequest(String serverUrl, int liveId, SoapObject soapObject, SoapRequest.Listener<String> listener,
            Response.ErrorListener errorListener) {
        super(serverUrl, null, errorListener);
        mLiveId = liveId;
        mSoapObject = soapObject;
        mListener = listener;
    }

    public SoapObject getSoapObject() {
        return mSoapObject;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(mLiveId, response);
    }
}
