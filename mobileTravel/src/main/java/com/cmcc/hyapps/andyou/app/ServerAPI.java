
package com.cmcc.hyapps.andyou.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Bucket;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.UploadToken;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerAPI {
    // Test server
    private static final String SERVER_BASE_TEST = "http://tapi.selftravel.com.cn/api";
    // Production server
    private static final String SERVER_BASE_PRODUCT = "http://api.selftravel.com.cn/api";

    public static final String KEY_DEBUG = "key_debug";

    private static String mServerBase = SERVER_BASE_PRODUCT;

    private final static String PARAM_ANONYMOUS = "anonymous";

    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String ENJOY = "enjoy";
    public static final String GUIDE = "guide";
    public static final String ENTERTAINMENT = "entertainment";

    public static final String AESE_KEY = "andtravel@cm.com";
    public static String getServerBase() {
        return Const.DEBUG ? mServerBase : SERVER_BASE_PRODUCT;
    }

    public static void switchServer(boolean debug) {
        mServerBase = debug ? SERVER_BASE_TEST : SERVER_BASE_PRODUCT;
        Bucket.switchServer(debug);
    }

    public static final String PROTOCOL_CHARSET = "utf-8";

    private interface ListParams {
        public static final String PARAM_LIMIT = "limit";
        public static final String PARAM_OFFSET = "offset";
        public static final String PARAM_TOTAL = "total";
    }

    private interface LocationParams {
        public static final String PARAM_LATITUDE = "latitude";
        public static final String PARAM_LONGITUDE = "longitude";
    }

    public static class Splash {
        public static String URL = getServerBase() + "/splash";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_DIVIDE = "divice";

        public static String buildUrl(String city) {
            Uri.Builder builder = Uri.parse(ServerAPI.Splash.URL).buildUpon();
            builder.appendQueryParameter(ServerAPI.Splash.PARAM_CITY, city);
            builder.appendQueryParameter(ServerAPI.Splash.PARAM_DIVIDE, "android");
            return builder.build().toString();
        }
    }

    public static class SearchList{
        public static String URL = BASE_URL + "search/";
    }

    public static class BannerSlides {
        public static final int TYPE_SCENIC = 0;
        public static final int TYPE_ACTIVITY = 1;
        public static final int TYPE_TRIP = 2;

        public static final String META_DATA_SCENIC_ID = "scenic_id";
        public static final String META_DATA_CLICK_URL = "click_url";
        public static final String META_DATA_CITY = "city";

        public static String URL = getServerBase() + "/bannerslides";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_LIMIT = "limit";

        public static String buildUrl(String city) {
            Uri.Builder builder = Uri.parse(ServerAPI.BannerSlides.URL).buildUpon();
            builder.appendQueryParameter(ServerAPI.BannerSlides.PARAM_CITY,
                    city);
            return builder.build().toString();
        }
    }
    private static String getURL(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = TravelApp.getAppContext().getPackageManager()
                    .getApplicationInfo(TravelApp.getAppContext().getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String server_ip=appInfo.metaData.getString("Server_IP");
        if (server_ip==null){
            server_ip="http://112.54.207.48/";
        }
        return server_ip;
    }

//    public static final String BASE_URL = ADDRESS + "/api/";
    //qing hai ip
 //   private static final String ADDRESS = "http://112.54.207.48/";
//    private static final String ADDRESS = "http://111.44.243.117/";
     public static final String ADDRESS = getURL();

    //ours ip
//    public static final String ADDRESS = "http://10.2.44.108:8080/";
//    public static final String ADDRESS = "http://111.44.243.118/";
//    public static final String ADDRESS = "http://112.54.207.49/";
    public static final String BASE_URL = ADDRESS + "api/";


    public static class HomeBanner{
        public static final String URL =  BASE_URL+"banners/";
    }

    public static class QHToken{
        public static final String AUTHE_TOKEN = "api-token-verify/";
        public static String buildAuthToken() {
            String url = ADDRESS +"api/" + AUTHE_TOKEN;
            return url;
        }
    }
    //朋友圈Token
    public static class getFriendToken{

        public static final String AUTHE_TOKEN = "tokenVerify.do";
        public static String buildAuthToken() {
            String url = ADDRESS +"friends/login/" + AUTHE_TOKEN;
            return url;
        }
    }
    //推荐用户
    public static class getRecommendUsers{
        public static final String AUTHE_TOKEN = "getRecommendUsers.do";
        public static String buildString() {
//            String url = ADDRESS +"friends/" + AUTHE_TOKEN;
            String url = ADDRESS +"friends/circle/" + AUTHE_TOKEN;
            return url;
        }
    }
    //我关注的人
    public static class getAttentionList{
        public static final String AUTHE_TOKEN = "getMyAttentionUsers.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //关注我的人
    public static class getPayAttentionList{
        public static final String AUTHE_TOKEN = "getPayAttentionToMeUsers.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //用户详情
    public static class getUserDetailList{
        public static final String AUTHE_TOKEN = "getUsersDetail.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //黑名单
    public static class getBlackList{
        public static final String AUTHE_TOKEN = "getBlackList.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //添加黑名单
    public static class getAddBlackList{
        public static final String AUTHE_TOKEN = "addToBlackList.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //移除黑名单
    public static class getRemoveBlackList{
        public static final String AUTHE_TOKEN = "removeFromBlackList.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //	添加关注用户
    public static class getAddPayAttentionList{
        public static final String AUTHE_TOKEN = "payAttention.do";
        public static String buildString() {
//            String url = ADDRESS +"friends/" + AUTHE_TOKEN;
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //	取消关注用户
    public static class getRemovePayAttentionList{
        public static final String AUTHE_TOKEN = "cancleAttention.do";
        public static String buildString() {
//            String url = ADDRESS +"friends/" + AUTHE_TOKEN;
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //	通讯录好友列表
    public static class getMailListUsersList{
        public static final String AUTHE_TOKEN = "getMailListUsers.do";
        public static String buildString() {
            String url = ADDRESS +"friends/" + AUTHE_TOKEN;
            return url;
        }
    }
    //	 获取TA关注的人
    public static class getMyAttentionListTa{
        public static final String AUTHE_TOKEN = "getHisAttentionUsers.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
        //	 获取关注TA的人
    public static class getPayAttentionListTa{
        public static final String AUTHE_TOKEN = "getPayAttentionToHeUsers.do";
        public static String buildString() {
            String url = ADDRESS +"friends/user/" + AUTHE_TOKEN;
            return url;
        }
    }
    //	 获取多个用户信息
    public static class getUserInfoList{
        //api/users/getUserInfoList/
        public static final String AUTHE_TOKEN = "users/getUserInfoList/";
        public static String buildString(String listId) {
            String url = ADDRESS +"api/" + AUTHE_TOKEN+"?userIds="+listId;
            return url;
        }
    }

    public static class Guide{
        public static final String BASE_GUIDE_SEARCH_URL =  BASE_URL+"guides/";
        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(ServerAPI.Guide.BASE_GUIDE_SEARCH_URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH,""+condition);
            return builder.build().toString();
        }

        public static String buildItemDetailUrl(String id) {
            String detail_url = BASE_GUIDE_SEARCH_URL + id + "/";
            return detail_url;
        }
        //删除攻略
        public static String buildDeleteItemDetailUrl(String id) {
            String detail_url = BASE_GUIDE_SEARCH_URL + id + "/";
            Uri.Builder builder = Uri.parse(detail_url).buildUpon();
            builder.appendQueryParameter(TYPE,""+2);
            return builder.build().toString();
        }
    }

    public static class Route{
        public static final String BASE_ROUTE_SEARCH_URL =  BASE_URL+"routes/";
        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(ServerAPI.Route.BASE_ROUTE_SEARCH_URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH,""+condition);
            return builder.build().toString();
        }

        public static String buildItemDetailUrl(String id) {
            String detail_url = BASE_ROUTE_SEARCH_URL + id + "/";
            return detail_url;
        }
    }
    public static class Entertainments{
        public static final String BASE_ENTERTAINMENTS_URL =  BASE_URL+"entertainments/";
        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(ServerAPI.Route.BASE_ROUTE_SEARCH_URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH,""+condition);
            return builder.build().toString();
        }

        public static String buildItemDetailUrl(String id) {
            String detail_url = BASE_ENTERTAINMENTS_URL + id + "/";
            return detail_url;
        }
    }
    public static class VideoList {
        public static String URL = BASE_URL + "videos/";

        public static String buildItemDetailUrl(String id) {
            String detail_url = URL  + id + "/";
            return detail_url;
        }

    }

    public static class QHApkList {
        public static String URL = BASE_URL + "apks/";
    }

    public static class QHSearchList {
        public static String URL = BASE_URL + "search/";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition).appendQueryParameter(TYPE, "1");
            return builder.build().toString();
        }
    }

    public static class QHScenicList {
        public static String URL = BASE_URL + "scenics/";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }
    }
    public static class StrategyList {
        public static String URL = BASE_URL + "guides/";

        public static String buildStrategyListUrl() {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(TYPE, "" + 1);
            return builder.build().toString();
        }
    }

    public static class NavigationList {
        public static String URL = BASE_URL + "scenics_nav/";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }
    }

    public static class MarketShopList {
        public static String URL = BASE_URL + "shops/";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }
        public static String buildRecommendUrl() {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", "-recommend");
            return builder.build().toString();
        }

    }

    public static class HotelsList {
        public static String URL = BASE_URL + "shops/?stype=1";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketUrl(float latitude, float longitude, String distance, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimtDistanceUrl(float latitude, float longitude, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimitpriceUrl(float latitude, float longitude, String distance, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketAllNotLimitUrl(float latitude, float longitude, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionUrl(String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionByPriceUrl(String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
    }

    public static class RestaurantList {
        public static String URL = BASE_URL + "shops/?stype=2";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketUrl(float latitude, float longitude, String distance, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimtDistanceUrl(float latitude, float longitude, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimitpriceUrl(float latitude, float longitude, String distance, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketAllNotLimitUrl(float latitude, float longitude, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionUrl(String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionByPriceUrl(String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
    }

    public static class SpecialList {
        public static String URL = BASE_URL + "shops/?stype=3";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketUrl(float latitude, float longitude, String distance, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimtDistanceUrl(float latitude, float longitude, String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
        public static String buildMutilConditionMarketNoLimitpriceUrl(float latitude, float longitude, String distance, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("distance", distance).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketAllNotLimitUrl(float latitude, float longitude, String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("latitude", String.valueOf(latitude)).appendQueryParameter("longitude", String.valueOf(longitude)).appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionUrl(String ordering) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering);
            return builder.build().toString();
        }

        public static String buildMutilConditionMarketNoPositionByPriceUrl(String ordering, String average) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter("ordering", ordering).appendQueryParameter("average", average);
            return builder.build().toString();
        }
    }
    public static class FourSlist{
        public static String URL = BASE_URL + "shops/?stype=4";

        public static final String PARAM_SEARCH = "search";

        public static String buildSearchCommentUrl(String condition) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_SEARCH, "" + condition);
            return builder.build().toString();
        }
    }
    public static class EnjoyList{
        public static String URL = ServerAPI.BASE_URL+"entertainments/";
        public static String AREA = "area_id";
        public static String TYPE = "type_id";
        public static String buildEnjoyListUrl(int area_id,int type_id) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(AREA, "" + area_id).appendQueryParameter(TYPE,type_id+"");
            return builder.build().toString();
        }
        public static String buildEnjoyListUrlNoWithType(int area_id) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(AREA, "" + area_id);
            return builder.build().toString();
        }
        public static String buildEnjoyListUrlNoWithArea(int type_id) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(TYPE,type_id+"");
            return builder.build().toString();
        }
    }
    public static class User {
        public static String URL_USER = getServerBase() + "/user";
        public static final String PARAM_PHONE_NUMBER = "phone";
        public static final String PARAM_VERIFY_CODE_TYPE = "type";
        public static final String PARAM_PASSWORD = "password";

        public static final String URL_USER_ACTIVITE = URL_USER + "/verify_code";

        public static final String URL_USER_ACTIVITE_LOGIN = URL_USER + "/verify_code_login";
        public static final String PARAM_GRANT_TYPE = "grant_type";
        public static final String PARAM_SCOPE = "scope";
        public static final String PARAM_CLIENT_ID = "client_id";
        public static final String PARAM_CLIENT_INFO = "client_info";

        public static final String PARAM_ACTIVATION_CODE = "activation_code";

        public static final String URL_USER_RESET_PASSWORD = URL_USER + "/reset_password";
        public static final String URL_USER_RESET_PASSWORD_VERIFY = URL_USER_RESET_PASSWORD
                + "/verify";
        public static final String URL_USER_RESET_PASSWORD_UPDATE = URL_USER_RESET_PASSWORD
                + "/update";
        public static final String PARAM_VERIFY_CODE = "verify_code";
        public static final String PARAM_TOKEN = "token";

        public static final String URL_USER_SELF = URL_USER + "/self";
        public static final String PARAM_NAME = "name";
        public static final String PARAM_AVATAR_URL = "avatar_url";

        public static final String URL_USER_COMMENTS = URL_USER + "/comments/";
        public static final String URL_USER_TRIPS = URL_USER + "/trips";
        public static final String URL_USER_TRIP_DELETE = URL_USER + "/trips/delete";
        public static final String PARAM_ID = "id";


        public static  String BASE_COLLECTION_URL =BASE_URL+ "users/";
        public static  String BASE_PUBLISH_URL = BASE_URL+ "comments";
        public static final String BASE_COMMENT_URL =  BASE_URL+"comments/";
        public static final String BASE_SECNIC_COMMENT_URL =  BASE_URL+"scenics/";
        public static final String BASE_ROUTE_COMMENT_URL =  BASE_URL+"comments/";
        public static final String BASE_RAIDER_COMMENT_URL =  BASE_URL+"comments/";
        public static final String BASE_COMMENT_COMMENT_URL =  BASE_URL+"comments/";

        public static final String BASE_PUBLISH_COMMENT_URL =  BASE_URL+"comments/";
        public static final String BASE_MY_MESSAGE_URL =  BASE_URL+"comment_messages/";
        public static final String BASE_WRITE_RAIDERS_URL =  BASE_URL+"guides/";
        public static final String BASE_WRITE_RAIDERS_INFO_URL =  BASE_URL+"guide_infos/";

        public static final String PARAM_TYPE = "obj_type";
        public static final String PARAM_OBJECT_ID = "object_id";
        public static final String PARAM_CTYPE = "ctype";


        public static String buildMessageReadUrl(String id) {
            String detail_url = BASE_MY_MESSAGE_URL + id + "/";
            return detail_url;
        }

        public static String buildItemDetailUrl(String id) {
            String detail_url = BASE_COMMENT_COMMENT_URL + id + "/";
            return detail_url;
        }

        /*obj_type=2是收藏的攻略 obj_type=3是收藏的路线*/
        public static String buildCollectionInfo(int type,int userId) {
            BASE_COLLECTION_URL =BASE_URL+ "users/"+userId+"/collects/";
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_COLLECTION_URL).buildUpon();
            builder.appendQueryParameter(PARAM_TYPE,""+type);
            return builder.build().toString();
        }
        /*obj_type=2是发布的攻略 obj_type=3是发布的点评*/
        public static String buildPublishInfo(int type,int userId) {
            if(type==2)BASE_PUBLISH_URL =BASE_URL+ "users/"+userId+"/guides/";
            else if(type==3)BASE_PUBLISH_URL =BASE_URL+ "users/"+userId+"/comments/";

            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_PUBLISH_URL).buildUpon();
//            builder.appendQueryParameter(PARAM_TYPE,""+type);
            return builder.build().toString();
        }

        public static String buildCommentInfo(int commentId) {
            BASE_PUBLISH_URL =BASE_URL+ "comments/" + commentId;

            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_PUBLISH_URL).buildUpon();
//            builder.appendQueryParameter(PARAM_TYPE,""+type);
            return builder.build().toString();
        }

        //不添加obj_type可以搜索到
        public static String buildCollectionAllInfo(int userId) {
            BASE_COLLECTION_URL =BASE_URL+ "users/"+userId+"/collects/";
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_COLLECTION_URL).buildUpon();
            return builder.build().toString();
        }
        /*评论列表*/
        public static String buildCommentUrl(int type) {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_COMMENT_URL).buildUpon();
            return builder.build().toString();
        }
        /*景区评论*/
        public static String buildSecnicCommentUrl(int seniciId) {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_SECNIC_COMMENT_URL+""+seniciId+"/comments/").buildUpon();
            return builder.build().toString();
        }
        /*评论的评论*/
        public static String buildCommentCommentUrl(int seniciId) {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_COMMENT_COMMENT_URL+""+seniciId+"/comments/").buildUpon();
            return builder.build().toString();
        }
        /*商城的评论*/
        public static String buildMarketCommentUrl(int marketId) {
            Uri.Builder builder = Uri.parse(ServerAPI.BASE_URL+"shops/"+marketId+"/comments/").buildUpon();
            return builder.build().toString();
        }

        /*
        发布评论
        评论对象object_id：对象id
        评论类型ctype：1：景区，2：攻略，3：路线，4：评论
        */
        public static String buildWriteCommentUrl(int object_id,int ctype) {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_COMMENT_URL).buildUpon();
            builder.appendQueryParameter(PARAM_OBJECT_ID,""+object_id);
            builder.appendQueryParameter(PARAM_CTYPE,""+ctype);
            return builder.build().toString();
        }
        public static String buildWriteCommentUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_PUBLISH_COMMENT_URL).buildUpon();
            return builder.build().toString();
        }
        public static String buildUpdateUserInfoUrl(int userId) {
            Uri.Builder builder = Uri.parse(BASE_URL+ "users/"+userId+"/").buildUpon();
            return builder.build().toString();
        }
        public static String buildMessageListUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.BASE_MY_MESSAGE_URL).buildUpon();
            return builder.build().toString();
        }

        public enum VerifyCodeType {
            USER_REGISTER("1"),
            RESET_PASSWORD("2");

            private String value;

            public String value() {
                return value;
            }

            private VerifyCodeType(String typeString) {
                this.value = typeString;
            }
        }

        public static String buildSelfUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_SELF).buildUpon();
            return builder.build().toString();
        }

        public static String buildActiviteUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_ACTIVITE).buildUpon();
            return builder.build().toString();
        }
        public static String test(String str) {
            Uri.Builder builder = Uri.parse(str).buildUpon();
            return builder.build().toString();
        }

        public static Map<String, String> buildActiviteParams(String phoneNumber,
                VerifyCodeType type) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_PHONE_NUMBER, phoneNumber);
            result.put(PARAM_VERIFY_CODE_TYPE, type.value());
            return result;
        }

        public static String buildLoginUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_ACTIVITE_LOGIN).buildUpon();
            return builder.build().toString();
        }

        public static Map<String, String> buildLoginParams(Context context, String phoneNumber,
                String activationCode) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_PHONE_NUMBER, phoneNumber);
            result.put(PARAM_ACTIVATION_CODE, activationCode);
            String uuid = AppUtils.uuid(context);
            if (!TextUtils.isEmpty(uuid)) {
                result.put(PARAM_CLIENT_ID, AppUtils.uuid(context));
//                result.put("Content-Type","application/json");
            }
            result.put(PARAM_CLIENT_INFO, AppUtils.buildClientInfo(context));
            return result;
        }

        public static String buildVerifyUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_RESET_PASSWORD_VERIFY)
                    .buildUpon();
            return builder.build().toString();
        }

        public static Map<String, String> buildVerifyParams(String phoneNumber,
                String activitionCode) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_PHONE_NUMBER, phoneNumber);
            result.put(PARAM_VERIFY_CODE, activitionCode);
            return result;
        }

        public static String buildUpdatePwdUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_RESET_PASSWORD_UPDATE)
                    .buildUpon();
            return builder.build().toString();
        }

        public static Map<String, String> buildUpdatePwdParams(String phoneNumber,
                String password, String token) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_PHONE_NUMBER, phoneNumber);
            result.put(PARAM_PASSWORD, password);
            result.put(PARAM_TOKEN, token);
            return result;
        }

        public static String buildCommentsUrl(int limit, int offset) {
            return appendListParams(URL_USER_COMMENTS, limit, offset);
        }

        public static String buildTripDeleteUrl() {
            Uri.Builder builder = Uri.parse(ServerAPI.User.URL_USER_TRIP_DELETE)
                    .buildUpon();
            return builder.build().toString();
        }

        public static Map<String, String> buildTripDeleteParams(long id) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_ID, "" + id);
            return result;
        }
    }

    public static class ScenicList implements ListParams, LocationParams {
        public static String URL = getServerBase() + "/scenics";
        public static final String PARAM_TYPE = "type";

        public enum Type {
            RECOMM("1"),
            NEARBY("2"),
            HOT("3");

            private String typeValue;

            private Type(String typeString) {
                this.typeValue = typeString;
            }
        }

        public static String buildUrl(Type type, Location location, int limit, int offset) {
            return appendListParams(buildUrl(type, location), limit, offset);
        }

        public static String buildUrl(Type type,String cityName, int limit, int offset) {
            return appendListParams(buildCityUrl(type, cityName), limit, offset);
        }
        public static String buildCityUrl(Type type, String cityName) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_TYPE,
                    type.typeValue);
            if (null!=cityName&&!"".equals(cityName)) {
                builder.appendQueryParameter(Splash.PARAM_CITY, cityName);
            }
            return builder.build().toString();
        }
        public static String buildUrl(Type type, Location location) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_TYPE,
                    type.typeValue);
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }
    }

    public static class ScenicImages implements ListParams {
        public static String URL = getServerBase() + "/scenic_images";
        public static final String PARAM_ID = "scenic_id";

        public static String buildUrl(int scenicId) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_ID,
                    String.valueOf(scenicId));
            return builder.build().toString();
        }
    }

    public static class Weather {
        public static String URL = getServerBase() + "/weather";
        public static final String PARAM_CITY = "city";

        public static String buildUrl(String city) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }
    }
    public static class Home {
        public static String BANNERURL = getServerBase() + "/bannerslides";
        public static String RESTAURANTURL = getServerBase() + "/city_foods";
        public static String RESTDETAILURL = getServerBase() + "/foods_details";
        public static String SPECIALURL = getServerBase() + "/speciality";
        public static String HOTELURL = getServerBase() + "/hotels";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_LAT = "latitude";
        public static final String PARAM_LON = "longitude";
        public static final String BUSSINESSID = "business_id";
        public static final String PARAM_TYPE = "type";
        public static final String PARAM_OFFSET = "offset";


        public static String buildUrl(String city) {
            Uri.Builder builder = Uri.parse(BANNERURL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }
        public static String buildRestuarantUrl(String city) {
            Uri.Builder builder = Uri.parse(RESTAURANTURL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }
        public static String buildRestuarantUrl(String city,String lat,String lon) {
            Uri.Builder builder = Uri.parse(RESTAURANTURL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            builder.appendQueryParameter(PARAM_LAT, ""+lat);
            builder.appendQueryParameter(PARAM_LON, ""+lon);
            return builder.build().toString();
        }
        public static String buildRestDetailUrl(String business_id) {
            Uri.Builder builder = Uri.parse(RESTDETAILURL).buildUpon();
            builder.appendQueryParameter(BUSSINESSID, business_id);
            return builder.build().toString();
        }
        public static String buildSpecialUrl(String city) {
            Uri.Builder builder = Uri.parse(SPECIALURL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }
        public static String buildHotelUrl(String city) {
            Uri.Builder builder = Uri.parse(HOTELURL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }
    }

    public static class ScenicDetails {
        public static String URL = getServerBase() + "/scenic_details";
        public static String NEARBY_URL = getServerBase() + "/nearbyPeople";
        public static final String PARAM_ID = "id";
        public static final String PARAM_SCENIC_ID = "scenic_id";
        public static final String PARAM_LATITUDE = "latitude";
        public static final String PARAM_LONGITUDE = "longitude";
        public static final String PARAM_RANDOM = "random";

        public static String buildUrl(int scenicId, Location location, boolean random) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();

            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_ID, String
                        .valueOf(scenicId));
            }

            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }

            if (random) {
                builder.appendQueryParameter(PARAM_RANDOM, String.valueOf(1));
            }

            return builder.build().toString();
        }

        public static String buildNearbyPeopleUrl(int scenicId) {
            Uri.Builder builder = Uri.parse(NEARBY_URL).buildUpon();

            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_SCENIC_ID, String
                        .valueOf(scenicId));
            }
            return builder.build().toString();
        }
    }

    public static class ScenicSpots implements ListParams {
        public static String URL = getServerBase() + "/scenic_spots";
        public static String DETAILS_URL = getServerBase() + "/scenic_spot_details";
        public static final String PARAM_ID = "scenic_id";
        public static final String PARAM_SPOT_ID = "id";

        public static String buildUrl(int scenicId, int limit, int offset) {
            return appendListParams(buildUrl(scenicId), limit, offset);
        }

        public static String buildUrl(int scenicId) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(ServerAPI.ScenicSpots.PARAM_ID, String
                    .valueOf(scenicId));
            return builder.build().toString();
        }

        public static String buildDetailsUrl(int scenicSpotId) {
            Uri.Builder builder = Uri.parse(DETAILS_URL).buildUpon();
            builder.appendQueryParameter(ServerAPI.ScenicSpots.PARAM_SPOT_ID, String
                    .valueOf(scenicSpotId));
            return builder.build().toString();
        }
    }

    public static class ScenicRoutes {
        public static String URL = getServerBase() + "/routes";
        public static final String PARAM_ID = "scenic_id";
        public static final String PARAM_LATITUDE = "latitude";
        public static final String PARAM_LONGITUDE = "longitude";

        public static String buildUrl(int scenicId) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_ID, String
                    .valueOf(scenicId));
            return builder.build().toString();
        }
    }

    public static class ScenicVideos implements ListParams {
        public static String URL = getServerBase() + "/scenic_videos";
        public static String BANNER_URL = getServerBase() + "/videos/bannerSlides";
        public static final String PARAM_ID = "scenic_id";
        public static final String PARAM_TYPE = "type";
        public static final String PARAM_LATITUDE = "latitude";
        public static final String PARAM_LONGITUDE = "longitude";

        public enum Type {
            SCENIC("1"),
            SHARED("2"),
            RECOMMENDED("3"),
            HOT("4"),
            LIVE("5");

            private String typeValue;

            private Type(String typeString) {
                this.typeValue = typeString;
            }
        }

        public static String buildUrl(int scenicId, Type type, Location location, int limit,
                int offset) {
            return appendListParams(buildUrl(scenicId, type, location), limit, offset);
        }

        public static String buildUrl(int scenicId, Type type, Location location) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_ID, String
                        .valueOf(scenicId));
            }
            builder.appendQueryParameter(PARAM_TYPE, type.typeValue);
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }

        public static String buildBannerUrl(Location location) {
            Uri.Builder builder = Uri.parse(BANNER_URL).buildUpon();
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }
    }

    public static class LiveVideos implements ListParams {
        public static String LIVE_URL = getServerBase() + "/scenic_videos/live";
        public static String LIVE_SERVER_URL = getServerBase() + "/scenic_videos/live/server";
        public static final String PARAM_ID = "scenic_id";
        public static final String PARAM_TYPE = "type";
        public static final String PARAM_LATITUDE = "latitude";
        public static final String PARAM_LONGITUDE = "longitude";

        public static final String NAMESPACE = "http://www.yeshine.sh.cn/ns1VideoServices/";
        public static final String METHOD_GET_PLAY_URL = "getPlayUrlReq";
        public static final String METHOD_GET_PIC_LIST = "getPicListReq";

        public static String buildPlayUrl(int scenicId, Location location) {
            Uri.Builder builder = Uri.parse(LIVE_URL).buildUpon();
            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_ID, String
                        .valueOf(scenicId));
            }
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }

        public static String buildLiveUrl(int scenicId, Location location) {
            Uri.Builder builder = Uri.parse(LIVE_URL).buildUpon();
            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_ID, String
                        .valueOf(scenicId));
            }
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }
    }

    public static class ScenicShops implements ListParams {
        public static String URL = getServerBase() + "/shops";
        public static final String PARAM_ID = "scenic_id";
        public static final String PARAM_TYPE = "type";

        public enum Type {
            FOOD("1"),
            SHOPPING("2"),
            REST_ROOM("3"),
            HOTEL("4");

            private String typeValue;

            private Type(String typeString) {
                this.typeValue = typeString;
            }

            public boolean hasRate() {
                return false;
            }

            public boolean hasPrice() {
                return false;
            }
        }

        public static String buildUrl(int scenicId, Type type, int limit,
                int offset) {
            return appendListParams(buildUrl(scenicId, type), limit, offset);
        }

        public static String buildUrl(int scenicId, Type type) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            if (scenicId > 0) {
                builder.appendQueryParameter(PARAM_ID, String
                        .valueOf(scenicId));
            }
            builder.appendQueryParameter(PARAM_TYPE, type.typeValue);
            return builder.build().toString();
        }
    }

    public static class Comments implements ListParams {
        public static final int TYPE_SCENIC = 1;
        public static final int TYPE_SCENIC_SPOT = 2;
        public static final int TYPE_TRIP = 3;
        public static final int TYPE_COMMENT = 4;

        public static String URL = getServerBase() + "/comments";
        public static final String PARAM_OBJECT_ID = "obj_id";
        public static final String PARAM_OBJECT_TYPE = "obj_type";
        public static String VOTE_URL = getServerBase() + "/vote";


        public static String GUIDER_VOTE_URL = BASE_URL + "votes/";
        public static String GUIDER_COLLECT_URL = BASE_URL + "collects/";//添加收藏

        public enum Type {
            SCENIC("1"),
            RAIDERS("2"),
            ROUTE("3"),
            COMMENT("4"),
            TRIP("5"),
            ENTERTAINMENTS("6");

            private String value;

            public String value() {
                return value;
            }

            private Type(String typeString) {
                this.value = typeString;
            }
        }

        public static String buildUrl(Context context, int objectId, Type type, int limit,
                int offset) {
            return appendListParams(buildUrl(context, objectId, type), limit, offset);
        }

        public static String buildUrl(Context context, int objectId, Type type) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_OBJECT_ID, String
                    .valueOf(objectId));
            builder.appendQueryParameter(PARAM_OBJECT_TYPE, type.value());
            builder.appendQueryParameter(PARAM_ANONYMOUS, AppUtils.getVistorId(context));

            return builder.build().toString();
        }

        public static Map<String, String> buildVoteParams(Context context, int objectId, Type type) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(PARAM_OBJECT_ID, String .valueOf(objectId));
            params.put(PARAM_OBJECT_TYPE, type.value());
//            params.put(PARAM_ANONYMOUS, AppUtils.getVistorId(context));
            return params;
        }

        public static class New {
            public static final String URL = Comments.URL + "/new";
        }
    }

    public static class Search implements ListParams {
        public static String SEARCH_SCENIC_URL = getServerBase() + "/search/scenic";
        public static String SEARCH_TRIP_URL = getServerBase() + "/search/trip";
        public static String SEARCH_VIDEO_URL = getServerBase() + "/search/videos";
        public static final String PARAM_KEYWORD = "q";
        public static final String PARAM_CITY = "city";

        public enum SearchType {
            SCENIC,
            TRIP,
            VIDEO
        }

        public static String buildSearchUrl(SearchType type, String keyword, String city,
                int limit,
                int offset) {
            String base;
            if (type == SearchType.SCENIC) {
                base = SEARCH_SCENIC_URL;
            } else if (type == SearchType.TRIP) {
                base = SEARCH_TRIP_URL;
            } else {
                base = SEARCH_VIDEO_URL;
            }

            Uri.Builder builder = Uri.parse(base).buildUpon();
            if (!TextUtils.isEmpty(keyword)) {
                builder.appendQueryParameter(PARAM_KEYWORD,
                        keyword);
            }
            if (!TextUtils.isEmpty(city)) {
                builder.appendQueryParameter(PARAM_CITY, city);
            }
            builder.appendQueryParameter(PARAM_LIMIT, String
                    .valueOf(limit));
            builder.appendQueryParameter(PARAM_OFFSET, String
                    .valueOf(offset));

            return builder.build().toString();
        }

        public static String buildSearchUrl(SearchType type, String keyword, String city) {
            String base;
            if (type == SearchType.SCENIC) {
                base = SEARCH_SCENIC_URL;
            } else if (type == SearchType.TRIP) {
                base = SEARCH_TRIP_URL;
            } else {
                base = SEARCH_VIDEO_URL;
            }

            Uri.Builder builder = Uri.parse(base).buildUpon();
            if (!TextUtils.isEmpty(keyword)) {
                builder.appendQueryParameter(PARAM_KEYWORD,
                        keyword);
            }
            if (!TextUtils.isEmpty(city)) {
                builder.appendQueryParameter(PARAM_CITY, city);
            }

            return builder.build().toString();
        }

    }

    public static class Upload {
        public static class Token {
            public static String URL = getServerBase() + "/upload/token";
            public static final String PARAM_SCOPE = "scope";
            public static final String PARAM_DEADLINE = "deadline";
            public static final String PARAM_END_USER = "end_user";
            public static final String PARAM_INSERT_ONLY = "insert_only";
            public static final String PARAM_RETURN_BODY = "return_body";
            public static final String PARAM_PERSISTANT_OPS = "persistant_ops";

            private static final String TOKEN_PREF_PREFIX = "uploadtoken-";

            private static String buildUrl(Bucket scope, String endUser,
                    boolean insertOnly, String returnBody, String persistantOps) {
                Uri.Builder builder = Uri.parse(URL).buildUpon();
                builder.appendQueryParameter(PARAM_SCOPE, scope.value);
                if (insertOnly) {
                    builder.appendQueryParameter(PARAM_DEADLINE, "1");
                }
                builder.appendQueryParameter(PARAM_RETURN_BODY, returnBody);
                builder.appendQueryParameter(PARAM_PERSISTANT_OPS, persistantOps);

                return builder.build().toString();
            }

            private static boolean isTokenExpired(String token) {
                if (TextUtils.isEmpty(token)) {
                    return true;
                }

                try {
                    String str = token.split(":")[2];
                    String jsonStr = new String(
                            Base64.decode(str, Base64.URL_SAFE | Base64.NO_WRAP),
                            "utf-8");
                    JSONObject json = new JSONObject(jsonStr);
                    long deadline = json.optLong(PARAM_DEADLINE);

                    // TODO: not exactly accurate
                    return System.currentTimeMillis() / 1000 >= deadline;
                } catch (Exception e) {
                    return true;
                }
            }

            public static class UploadParams {
                public Bucket bucket;
                public String endUser = "";
                public boolean insertOnly = false;
                public String returnBody = "";
                public String persistantOps = "";
            }

            public static Request<UploadToken> getUploadToken(final Context context,
                    UploadParams params,
                    String requestTag,
                    final UploadTokenCallback cb) {
                if (cb == null || params == null) {
                    return null;
                }

                final String prefKey = TOKEN_PREF_PREFIX + params.bucket.value();
                String token = PreferencesUtils.getEncryptString(context, prefKey);
                if (!isTokenExpired(token)) {
                    Log.d("Upload token for %s is valid, use it", params.bucket);
                    cb.onGetToken(token);
                    return null;
                } else {
                    Log.d("Upload token for %s is expired, refreshing it", params.bucket);
                    final String url = buildUrl(params.bucket, params.endUser, params.insertOnly,
                            params.returnBody, params.persistantOps);
                    Log.d("Getting upload token from %s", url);
                    return RequestManager.getInstance().sendGsonRequest(url, UploadToken.class,
                            new Response.Listener<UploadToken>() {
                                @Override
                                public void onResponse(UploadToken token) {
                                    Log.d("Successfully get upload token: %s", token.token);
                                    if (!TextUtils.isEmpty(token.token)) {
                                        PreferencesUtils.putEncryptString(context, prefKey,
                                                token.token);
                                        cb.onGetToken(token.token);
                                    } else {
                                        cb.onTokenError();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(error, "Error getting upload token");
                                    cb.onTokenError();
                                }
                            }, requestTag);
                }
            }

            public interface UploadTokenCallback {
                void onTokenError();

                void onGetToken(String token);
            }
        }

        public static class Bucket {
            // TODO
            public static final Bucket SELF_TRAVEL_IMAGE = new Bucket("selftravel-image");
            public static final Bucket SELF_TRAVEL_AUDIO = new Bucket("selftravel-audio");
            public static final Bucket SELF_TRAVEL_VIDEO = new Bucket("selftravel-video");
            public static final Bucket SELF_TRAVEL = new Bucket("selftravel");

            public static final Bucket MAOYOU_IMAGE = new Bucket("maoyou-image");
            public static final Bucket MAOYOU_AUDIO = new Bucket("maoyou-audio");
            public static final Bucket MAOYOU_VIDEO = new Bucket("maoyou-video");
            public static final Bucket MAOYOU = new Bucket("maoyou");

            public static Bucket bucketImage = SELF_TRAVEL_IMAGE;
            public static Bucket bucketAudio = SELF_TRAVEL_AUDIO;
            public static Bucket bucketVideo = SELF_TRAVEL_VIDEO;
            public static Bucket bucket = SELF_TRAVEL;

            public static void switchServer(boolean debug) {
                if (debug) {
                    bucketImage = MAOYOU_IMAGE;
                    bucketAudio = MAOYOU_AUDIO;
                    bucketVideo = MAOYOU_VIDEO;
                    bucket = MAOYOU;
                } else {
                    bucketImage = SELF_TRAVEL_IMAGE;
                    bucketAudio = SELF_TRAVEL_AUDIO;
                    bucketVideo = SELF_TRAVEL_VIDEO;
                    bucket = SELF_TRAVEL;
                }
            }

            private String value;

            public String value() {
                return value;
            }

            public Bucket(String typeString) {
                this.value = typeString;
            }
        }

        public static String buildThumbnailPath(String imageUrl) {
            StringBuilder sb = new StringBuilder(imageUrl);
            sb.append("?imageView2/0/w/300");
            return sb.toString();
        }
    }

    public static class Trips implements ListParams, LocationParams {
        public static String TRIPS_ADD_URL = getServerBase() + "/trips/add/";
        public static String TRIPS_ADD_DAY_URL = getServerBase() + "/trips/days/add/";
        public static String TRIPS_LIST_URL = getServerBase() + "/trips/list/";
        public static String TRIPS_DETAIL_URL = getServerBase() + "/trips/details/";
        public static String TRIPS_BANNER_URL = getServerBase() + "/trips/bannerSlides";
        public static final String PARAM_ID = "id";
        public static final String PARAM_CITY = "city";
        public static final String PARAM_TITLE = "title";
        public static final String PARAM_CREATE_TIME = "start_date";

        public static Map<String, String> buildAddTripParams(Context context, String title,
                String createTime) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_TITLE, title);
            result.put(PARAM_CREATE_TIME, createTime);
            return result;
        }

        public static String buildTripBannerSlidesUrl(String city) {

            Uri.Builder builder = Uri.parse(TRIPS_BANNER_URL).buildUpon();
            builder.appendQueryParameter(PARAM_CITY, city);
            return builder.build().toString();
        }

        public static String buildGetTripsUrl(Location location) {

            // FIXME Just for test
            // location.latitude = 18.296388;
            // location.longitude = 109.208867;

            Uri.Builder builder = Uri.parse(TRIPS_LIST_URL).buildUpon();
            if (location != null && location.isValid()) {
                builder.appendQueryParameter(PARAM_LATITUDE, String
                        .valueOf(location.latitude));
                builder.appendQueryParameter(PARAM_LONGITUDE, String
                        .valueOf(location.longitude));
            }
            return builder.build().toString();
        }

        public static String buildGetTripDetailUrl(Context context, int id) {

            Uri.Builder builder = Uri.parse(TRIPS_DETAIL_URL).buildUpon();
            builder.appendQueryParameter(PARAM_ID, String
                    .valueOf(id));
            builder.appendQueryParameter(PARAM_ANONYMOUS, AppUtils.getVistorId(context));
            return builder.build().toString();
        }
    }

    public static class Itinerary implements ListParams {
        public static String TRIPS_NEW_URL = getServerBase() + "/trips/itinerary/new";
        public static String TRIPS_GET_URL = getServerBase() + "/trips/itinerary";
        public static String TRIPS_LIST_URL = getServerBase() + "/trips/itinerary/list";
        public static String TRIPS_DELETE_URL = getServerBase() + "/trips/itinerary/delete";
        public static final String PARAM_NAME = "name";
        public static final String PARAM_CREATE_TIME = "create_time";
        public static final String PARAM_END_TIME = "end_time";
        public static final String PARAM_START_TIME = "start_time";
        public static final String PARAM_DAYS = "days";
        public static final String PARAM_ID = "id";

        public static String buildGetItinerariesUrl(int limit, int offset) {

            Uri.Builder builder = Uri.parse(TRIPS_LIST_URL).buildUpon();
            builder.appendQueryParameter(PARAM_LIMIT, String.valueOf(limit));
            builder.appendQueryParameter(PARAM_OFFSET, String.valueOf(offset));
            return builder.build().toString();
        }
    }

    public static class Favorites implements ListParams {
        public static String FAVOR_LIST_URL = getServerBase() + "/user/favorites";
        public static String FAVOR_DELETE_URL = getServerBase() + "/user/favorites/delete";
        public static String FAVOR_ADD_URL = getServerBase() + "/favorites/add";
        public static final String PARAM_OBJECT_ID = "object_id";
        public static final String PARAM_OBJECT_TYPE = "object_type";

        public enum Type {
            SCENIC("1"),
            TRIP("2");

            private String value;

            public String value() {
                return value;
            }

            private Type(String typeString) {
                this.value = typeString;
            }
        }

        public static String buildGetFavoritesUrl(int limit, int offset) {
            Uri.Builder builder = Uri.parse(FAVOR_LIST_URL).buildUpon();
            builder.appendQueryParameter(PARAM_LIMIT, String.valueOf(limit));
            builder.appendQueryParameter(PARAM_OFFSET, String.valueOf(offset));
            return builder.build().toString();
        }

        public static Map<String, String> buildAddFavoritesParams(int objectId, Type type) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(PARAM_OBJECT_ID, String
                    .valueOf(objectId));
            params.put(PARAM_OBJECT_TYPE, type.value());
            return params;
        }

        public static String buildDeleteFavoriteUrl(int id, int type) {
            Uri.Builder builder = Uri.parse(FAVOR_DELETE_URL).buildUpon();
            builder.appendQueryParameter(PARAM_OBJECT_ID, String.valueOf(id));
            builder.appendQueryParameter(PARAM_OBJECT_TYPE, String.valueOf(type));
            return builder.build().toString();
        }
    }

    public static class VideoUpload {
        public static String URL = getServerBase() + "/media/video_upload";
    }

    public static class ImageUpload {
        public static String URL = getServerBase() + "/media/image_upload";
    }

    public static class CityList {
        public static String URL = getServerBase() + "/city_list";

    }

    public static class Feedback {
        public static String URL = getServerBase() + "/feedback";
        public static final String PARAM_CONTENT = "content";
        public static final String PARAM_CONTACT = "contact";

        public static Map<String, String> buildFeedbackParams(String content,
                String contact) {
            Map<String, String> result = new HashMap<String, String>();
            result.put(PARAM_CONTENT, content);
            result.put(PARAM_CONTACT, contact);
            // result.put("contact_type", "1");
            return result;
        }
    }

    public static class Version {
        public static String URL = getServerBase() + "/version/android";
        public static final String PARAM_VERSION = "version";

        public static String buildUrl(int versionCode) {
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            builder.appendQueryParameter(PARAM_VERSION, String.valueOf(versionCode));
            return builder.build().toString();
        }
    }

    public static class OfflinePackages {
        public static String LIST_INFO_URL = getServerBase() + "/offline_packages";
        public static final String PARAM_ID = "scenic_id";

        public static String buildUrl(int... ids) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ids.length; i++) {
                sb.append(ids[i]);
                if (i != ids.length - 1) {
                    sb.append(",");
                }
            }
            return buildUrl(sb.toString());
        }

        public static String buildUrl(String ids) {
            Uri.Builder builder = Uri.parse(LIST_INFO_URL).buildUpon();
            builder.appendQueryParameter(PARAM_ID, ids);
            return builder.build().toString();
        }
    }

    public static class Tracks implements ListParams {
        public static String LIST_URL = getServerBase() + "/user/footprint/lists";
        public static String DETAIL_URL = getServerBase() + "/user/footprint/details";
        public static final String PARAM_ID = "id";

        // public static final String PARAM_START_TIME = "start_time";
        // public static final String PARAM_END_TIME = "end_time";

        public static String buildGetFootprintsUrl(int limit, int offset) {
            Uri.Builder builder = Uri.parse(LIST_URL).buildUpon();
            builder.appendQueryParameter(PARAM_LIMIT, String.valueOf(limit));
            builder.appendQueryParameter(PARAM_OFFSET, String.valueOf(offset));
            return builder.build().toString();
        }

        public static String buildGetFootprintDetailUrl(int id) {
            Uri.Builder builder = Uri.parse(DETAIL_URL).buildUpon();
            builder.appendQueryParameter(PARAM_ID, String.valueOf(id));
            return builder.build().toString();
        }
    }

    public static class Qiniu {
        public static final String SERVER_BASE = "http://api.qiniu.com";

        public static class PFOP {
            public static final String URL = SERVER_BASE + "/prop/";
            public static final String PARAM_BUCKET = "bucket";
            public static final String PARAM_KEY = "key";
            public static final String PARAM_FOPS = "fops";
            public static final String PARAM_NOTIFY_URL = "notifyURL";

        }
    }

    public static String appendListParams(String baseUrl, int limit, int offset) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendQueryParameter(ListParams.PARAM_LIMIT, String.valueOf(limit));
        builder.appendQueryParameter(ListParams.PARAM_OFFSET, String.valueOf(offset));
        return builder.build().toString();
    }

    public static class ErrorCode {
        public static final int ERROR_NONE = 0;

        // Common
        public static final int ERROR_PARSE = -700;
        public static final int ERROR_REQUEST = -32600;
        public static final int ERROR_SERVER_NAME = -32601;
        public static final int ERROR_PARAMS = -32602;
        public static final int ERROR_INNER = -32500;
        public static final int ERROR_SERVER = -32400;

        // User
        public static final int ERROR_NOT_LOGIN = 20305;
        public static final int ERROR_USER_EXIST = 20330;
        public static final int ERROR_USER_NOT_EXIST = 20331;
        public static final int ERROR_INVALID_PHONE = 20332;
        public static final int ERROR_INVALID_CODE = 20333;
        public static final int ERROR_CODE_EXPIRE = 20334;
        public static final int ERROR_INVALID_PASSWORD = 20335;
        public static final int ERROR_INVALID_TOKEN = 22030;

        // comment
        public static final int ERROR_ALREADY_VOTED = 20231;

    }
}
