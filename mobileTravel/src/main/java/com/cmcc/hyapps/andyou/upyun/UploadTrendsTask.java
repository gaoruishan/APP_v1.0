/**
 * com.upyun.api Upload.java
 */
package com.cmcc.hyapps.andyou.upyun;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.StatusLine;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.FileEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.google.gson.Gson;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.LocationUtil;

/**
 * Upload.java
 *
 * @author vincent chen
 * @since 2012 Jun 18, 2012 4:52:49 PM
 */
public class UploadTrendsTask {

    public static String upload(Context mContext, List<File> imgFiles,QHFriendInfo qhFriendInfo,boolean isLocation)throws UpYunException {
        String returnStr = null;
        DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
//        HttpPost localHttpPost = new HttpPost(ServerAPI.ADDRESS +"friends/publishInformation.do");
        HttpPost localHttpPost = new HttpPost(ServerAPI.ADDRESS +"friends/circle/publishInformation.do");
        MultipartEntity localMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        try
        {
            QHToken tokenInfo = AppUtils.getQHToken(mContext);
            if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                //  localHttpPost.addHeader("Authorization", "JWT " + tokenInfo.token);
                localHttpPost.addHeader("Authorization",  tokenInfo.token);
                if (AppUtils.mjsessionid!=null){
                    localHttpPost.addHeader("Cookie","JSESSIONID=" + AppUtils.mjsessionid);
                }
            }
            Map<String,Object> maps = new HashMap<String, Object>();
            if (isLocation){
                double latitude = LocationUtil.getInstance(mContext).getLatitude();
                double longitude = LocationUtil.getInstance(mContext).getLongitude();
                String address = LocationUtil.getInstance(mContext).getCurrentAddress();
//                localMultipartEntity.addPart("latitude", new StringBody(""+latitude));
//                localMultipartEntity.addPart("longitude", new StringBody(""+longitude));
//                localMultipartEntity.addPart("address", new StringBody(address));
                maps.put("latitude",""+latitude);
                maps.put("longitude",""+longitude);
                maps.put("address", address);

            }else {
//                localMultipartEntity.addPart("latitude", new StringBody("0"));
//                localMultipartEntity.addPart("longitude", new StringBody("0"));
                maps.put("latitude","0");
                maps.put("longitude","0");
            }
//            localMultipartEntity.addPart("infoText", new StringBody(qhFriendInfo.getInfoText()));
//            localMultipartEntity.addPart("isPublic", new StringBody(""+qhFriendInfo.getIsPublic()));
            maps.put("infoText",qhFriendInfo.getInfoText());
            maps.put("isPublic", qhFriendInfo.getIsPublic()+"");
            String data = null;
            try {
                data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
                localMultipartEntity.addPart("data", new StringBody(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(int i=0;i<imgFiles.size();i++){
                FileBody localFileBody = new FileBody(imgFiles.get(i), "image/jpg");
                localMultipartEntity.addPart("image_"+i, localFileBody);
            }
            localHttpPost.setEntity(localMultipartEntity);
            HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpPost);
            String str = EntityUtils.toString(localHttpResponse.getEntity());
            int code =  localHttpResponse.getStatusLine().getStatusCode();
            if (code != HttpStatus.SC_OK&&code != HttpStatus.SC_CREATED&&code != HttpStatus.SC_ACCEPTED) {
                Log.e("Uploader 发布信息失败","失败原因："+str);
                JSONObject obj = new JSONObject(str);
                String msg = obj.getString("message");
                msg = new String(msg.getBytes("UTF-8"), "UTF-8");
                String url = obj.getString("url");
                long time = obj.getLong("time");
                boolean isSigned = false;
                String signString = "";
                if (!obj.isNull("sign")) {
                    signString = obj.getString("sign");
                    isSigned = true;
                } else if (!obj.isNull("non-sign")) {
                    signString = obj.getString("non-sign");
                    isSigned = false;
                }
                UpYunException exception = new UpYunException(code, msg);
                exception.isSigned = isSigned;
                exception.url = url;
                exception.time = time;
                exception.signString = signString;
                throw exception;
            } else {
                JSONObject obj = new JSONObject(str);
                returnStr = str;
            }
        }
        catch (Exception e)
        {
            Log.d("exception", e.toString());
        }
        return returnStr;
    }

    private static void postFile(String url,File file,Context mContext) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {

            File uploadFile = new File(file.getPath());
            //定义FileEntity对象
            HttpEntity entity = new FileEntity(uploadFile,"binary/octet-stream");
            //为httpPost设置头信息
//            httpPost.setHeader("filename", URLEncoder.encode(fileName,"utf-8"));//服务器可以读取到该文件名
            httpPost.setHeader("Content-Length", String.valueOf(entity.getContentLength()));//设置传输长度
            httpPost.setHeader("Content-Type", "multipart/form-data");
            QHToken tokenInfo = AppUtils.getQHToken(mContext);
            if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                //  httpPost.addHeader("Authorization", "JWT " + tokenInfo.token);
                httpPost.addHeader("Authorization", tokenInfo.token);
            }
            httpPost.setEntity(entity); //设置实体对象






            // httpClient执行httpPost提交
            HttpResponse response = httpClient.execute(httpPost);
            // 得到服务器响应实体对象
            HttpEntity responseEntity = response.getEntity();




            if (responseEntity != null) {
                String str = EntityUtils.toString(responseEntity, "utf-8");
                Log.e("++++++++++++",str);
            } else {
                System.out.println("服务器无响应！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpClient.getConnectionManager().shutdown();
        }
    }

}
