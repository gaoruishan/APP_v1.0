package com.cmcc.hyapps.andyou.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.activity.GuiderMessageListActivity;
import com.cmcc.hyapps.andyou.activity.GuiderRecommandActivity;
import com.cmcc.hyapps.andyou.activity.UserInformationActivity;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AESResponse;
import com.cmcc.hyapps.andyou.model.QHResultState;
import com.cmcc.hyapps.andyou.model.QHToken;
import com.cmcc.hyapps.andyou.model.QHTokenId;
import com.cmcc.hyapps.andyou.model.QHUserDetails;
import com.cmcc.hyapps.andyou.util.AESEncrpt;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.google.gson.Gson;
import com.littlec.sdk.entity.CMGroup;
import com.littlec.sdk.manager.CMIMHelper;
import com.littlec.sdk.utils.CMChatListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.util.model.QHAttention;
import test.grs.com.ims.util.model.QHBlackList;
import test.grs.com.ims.util.model.QHMailList;
import test.grs.com.ims.util.model.QHRecommends;
import test.grs.com.ims.util.model.QHUserInfoLists;

public class MobileTravelToIMService extends Service {
    public static final String USER_ID = "userId";
    private final int REQUEST_CODE_PUBLISH = 1;
    private final int REQUEST_CODE_FAVORITES = 2;
    private final int REQUEST_CODE_MESSAGE = 3;
    private final int REQUEST_CODE_SETTINGS = 4;
    private final int REQUEST_CODE_COLLECT = 5;
    private static MobileTravelToIMService mInstance;
    private Intent serviceIntent;
    private static Context mContext;
    private int offset = 1;
    private String mRecommendedUsers = "";
    public static List<CMGroup> allContacts;
    private QHBlackList blackList;

    public MobileTravelToIMService() {
    }

    public static void init(Context context) {
        mContext = context;
        Intent serviceIntent = new Intent(context, MobileTravelToIMService.class);
        context.startService(serviceIntent);
    }

    public static void destory() {
        mContext.stopService(new Intent(mContext, MobileTravelToIMService.class));
    }

    public interface BindBackDatas {
        QHBlackList onBlackListSuceess();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启监听登录的广播
        IntentFilter filter = new IntentFilter(IMConst.ACTION_IM);
        filter.addAction(IMConst.ACTION_RECOMMEND);
        filter.addAction(IMConst.ACTION_RECOMMEND_AGAIN);
        filter.addAction(IMConst.ACTION_MYATTENTION);
        filter.addAction(IMConst.ACTION_PAYATTENTIONTOME);
        filter.addAction(IMConst.ACTION_BLACKLIST);
        filter.addAction(IMConst.ACTION_ADD_BLACKLIST);
        filter.addAction(IMConst.ACTION_ADD_ATTENTION);
        filter.addAction(IMConst.ACTION_REMOVE_ATTENTION);
        filter.addAction(IMConst.ACTION_REMOVE_BLACKLIST);
        filter.addAction(IMConst.ACTION_USER_DETAIL);
        filter.addAction(IMConst.ACTION_MAILLISTUSER);
        filter.addAction(IMConst.ACTION_STARTACTIVITY);
        filter.addAction(IMConst.ACTION_STARTACTIVITY1);
        filter.addAction(IMConst.ACTION_GET_USERDETAIL);
        filter.addAction(IMConst.ACTION_GROUP);
        filter.addAction(IMConst.ACTION_FRIEND_TOKEN);
        filter.addAction(IMConst.ACTION_START_GUDERMESSAGE);
        filter.addAction(IMConst.ACTION_SAVE_ATTENTION);
        filter.addAction(IMConst.ACTION_PAYATTENTIONTOME_TA);
        filter.addAction(IMConst.ACTION_MYATTENTION_TA);
        filter.addAction(IMConst.ACTION_GET_USERLIST);
        filter.addAction(IMConst.ACTION_ONLY_GROUP);
        registerReceiver(receiverService, filter);
    }

    BroadcastReceiver receiverService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IMConst.ACTION_RECOMMEND.equals(action)) {
                //加载推荐用户
                getRecommendUsers();
                offset = 1;
            } else if (IMConst.ACTION_RECOMMEND_AGAIN.equals(action)) {
//                if (mRecommendedUsers.contains(userId)){
//                    if (mRecommendedUsers.contains(userId+",")){
//                        mRecommendedUsers.replace(userId+",","");
//                    }else {
//                        mRecommendedUsers.replace(userId,"");
//                    }
//                }
                //再次加载推荐用户
                getRecommends(mRecommendedUsers, offset);
                offset++;
            } else if (IMConst.ACTION_MYATTENTION.equals(action)) {
                //加载我关注的
                getMyAttentionUsers();
            } else if (IMConst.ACTION_PAYATTENTIONTOME.equals(action)) {
                //加载关注我的
                getPayAttentionToMeUsers();
            } else if (IMConst.ACTION_BLACKLIST.equals(action)) {
                //加载黑名单列表
                getBlackLists();
            } else if (IMConst.ACTION_ADD_BLACKLIST.equals(action)) {
                //添加黑名单
                String userId = intent.getStringExtra(USER_ID);
                addBlackList(userId);
            } else if (IMConst.ACTION_ADD_ATTENTION.equals(action)) {
                //添加关注的
                String userId = intent.getStringExtra(USER_ID);
                addAttention(userId);
            } else if (IMConst.ACTION_REMOVE_ATTENTION.equals(action)) {
                //取消关注的
                String userId = intent.getStringExtra(USER_ID);
                removeAttention(userId);
            } else if (IMConst.ACTION_MYATTENTION_TA.equals(action)) {
                //获取TA关注的人
                String userId = intent.getStringExtra(USER_ID);
                getMyAttentionUsersTA(userId);
            } else if (IMConst.ACTION_PAYATTENTIONTOME_TA.equals(action)) {
                //获取关注TA的人
                String userId = intent.getStringExtra(USER_ID);
                getPayAttentionToMeUsersTA(userId);
            } else if (IMConst.ACTION_GET_USERDETAIL.equals(action)) {
                //获取用户详情
                String userId = intent.getStringExtra(USER_ID);
                getUserDetail(userId);
            } else if (IMConst.ACTION_REMOVE_BLACKLIST.equals(action)) {
                //移除黑名单
                String userId = intent.getStringExtra(USER_ID);
                removeBlackList(userId);
            } else if (IMConst.ACTION_STARTACTIVITY1.equals(action)) {
                //跳转推荐好友
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(getApplicationContext(), GuiderRecommandActivity.class);
                getApplicationContext().startActivity(i);
            } else if (IMConst.ACTION_STARTACTIVITY.equals(action)) {
                //跳转用户详情
                Intent i = new Intent();
                i.putExtra("user_ID", intent.getStringExtra(USER_ID));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(getApplicationContext(), UserInformationActivity.class);
                getApplicationContext().startActivity(i);
            } else if (IMConst.ACTION_START_GUDERMESSAGE.equals(action)) {
                //跳转评论回复提醒
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(getApplicationContext(), GuiderMessageListActivity.class);
                getApplicationContext().startActivity(i);
            } else if (IMConst.ACTION_MAILLISTUSER.equals(action)) {
                //加载通讯录好友
                ArrayList<String> list = intent.getStringArrayListExtra("list");
                final StringBuilder phoneNums = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i == list.size() - 1) {
                        phoneNums.append(list.get(i));
                    } else {
                        phoneNums.append(list.get(i) + ",");
                    }
                }
                //开启异步任务 更新
                new NetWorkTask(getApplicationContext(), ServerAPI.getMailListUsersList.buildString(), phoneNums.toString()).execute();

            } else if (IMConst.ACTION_GROUP.equals(action)) {
                //加载群组
                getGroupDatas();
            } else if (IMConst.ACTION_ONLY_GROUP.equals(action)) {
                getOnlyGroupDatas();
            } else if (IMConst.ACTION_FRIEND_TOKEN.equals(action)) {
                //加载friendToken
                getFriendToken();
            } else if (IMConst.ACTION_SAVE_ATTENTION.equals(action)) {
                //只加载保存我关注的人
                getMyAttentionUsersToSave();
            } else if (IMConst.ACTION_GET_USERLIST.equals(action)) {
                //获取多个用户信息
                String userIds = intent.getStringExtra(USER_ID);
                getUserLists(userIds);
            }
        }
    };

    /**
     * 获取多个用户信息
     * @param userIds
     */
    private void getUserLists(String userIds) {
        RequestManager.getInstance().sendGsonRequest(ServerAPI.getUserInfoList.buildString(userIds), QHUserInfoLists.class,
                new Response.Listener<QHUserInfoLists>() {

                    @Override
                    public void onResponse(QHUserInfoLists user) {
                        Log.e("====获取多个用户信息: " + user.toString());
                       MessageHandle.getInstance().doUserInfoList(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "=onErrorResponse获取多个用户信息" + error);
                    }
                }, "");
    }

    /**
     * 获取关注TA的人
     *
     * @param userId
     */
    private void getPayAttentionToMeUsersTA(String userId) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("userId", userId);
        String body = getRequestParams(maps,ServerAPI.getPayAttentionListTa.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getPayAttentionListTa.buildString(), QHAttention.class, body,
                new Response.Listener<QHAttention>() {

                    @Override
                    public void onResponse(QHAttention user) {
                        Log.e("====关注TA的人QHAttention: " + user.toString());
                        // 刷新
                        MessageHandle.getInstance().setFreshAttentionDatas(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "getPayAttentionToMeUsersTA",AppUtils.dynamicKey);
    }

    /**
     * 获取TA关注的人
     *
     * @param userId
     */
    private void getMyAttentionUsersTA(String userId) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("userId", userId);
        String body = getRequestParams(maps,ServerAPI.getMyAttentionListTa.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getMyAttentionListTa.buildString(), QHAttention.class, body,
                new Response.Listener<QHAttention>() {

                    @Override
                    public void onResponse(QHAttention user) {
                        Log.e("====TA关注的人QHAttention: " + user.toString());
                        // 刷新
                        MessageHandle.getInstance().setFreshAttentionDatas(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "getMyAttentionUsersTA",AppUtils.dynamicKey);
    }

    /**
     * 取消关注
     *
     * @param userId
     */
    private void removeAttention(String userId) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("cancle_uid", userId);
        String body = getRequestParams(maps,ServerAPI.getRemovePayAttentionList.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getRemovePayAttentionList.buildString(), QHResultState.class, body,
                new Response.Listener<QHResultState>() {

                    @Override
                    public void onResponse(QHResultState user) {
                        if (user.getSuccessful().equals("true")) {
                            Toast.makeText(getApplicationContext(), "取消关注成功", Toast.LENGTH_LONG).show();
                            //更新我关注的人
                            getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_SAVE_ATTENTION));
//                            UserInformationActivity.refreshHasAttention(1);
                        } else {
                            Toast.makeText(getApplicationContext(), "取消关注失败", Toast.LENGTH_LONG).show();
//                            UserInformationActivity.refreshHasAttention(0);
                        }
                        Log.e("====取消关注QHAttention: " + user.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "取消关注user=onErrorResponse" + error);
                    }
                }, "", AppUtils.dynamicKey);
    }

    /**
     * 获取圈子验证后 加载默认
     */
    private void getFriendToken() {

        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getFriendToken.buildAuthToken(), QHTokenId.class,
                new Response.Listener<QHTokenId>() {

                    @Override
                    public void onResponse(QHTokenId user) {
//                        Log.e("==QHTokenId, User: " + user.jsessionid);
                        //保存验证
                        AppUtils.saveFriendToken(getApplicationContext(), new Gson().toJson(user), user.jsessionid);
                        AppUtils.saveDynamicKey(user.getDynamicKey());
                        //默认加载--我关注的人和群组

//                        getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));
//                        getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_GROUP));

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "getFriendToken",ServerAPI.AESE_KEY);
    }

    /**
     * 用户详情
     */
    private void getUserDetail(String id) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("user_id",id);
        String body = getRequestParams(maps,ServerAPI.getUserDetailList.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getUserDetailList.buildString(), QHUserDetails.class, body,
                new Response.Listener<QHUserDetails>() {

                    @Override
                    public void onResponse(QHUserDetails user) {
                        Log.e("====用户详情QHAttention: " + user.toString());
                        MessageHandle.getInstance().doAddUnContact(user.getAvatarUrl(), user.getNickname());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                        Toast.makeText(getApplicationContext(), "获取用户详情失败" + error, Toast.LENGTH_SHORT).show();

                    }
                }, "getUserDetail",AppUtils.dynamicKey);
    }

    /**
     * 添加黑名单
     *
     * @param id
     */
    private void addBlackList(String id) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("user_uid",id);
        String body = getRequestParams(maps,ServerAPI.getAddBlackList.buildString());
        //添加黑名单
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getAddBlackList.buildString(), QHResultState.class, body,
                new Response.Listener<QHResultState>() {

                    @Override
                    public void onResponse(QHResultState user) {
                        Log.e("====QHAttention: " + user.toString());
                        if (user.getSuccessful().equals("true")) {
                            Toast.makeText(getApplicationContext(), "添加黑名单成功", Toast.LENGTH_LONG).show();
                            //更新黑名单
                            getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
                        } else {
                            Toast.makeText(getApplicationContext(), "添加黑名单失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "addBlackList",AppUtils.dynamicKey);
    }

    /**
     * 移除黑名单
     */
    private void removeBlackList(final String id) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("user_uid",id);
        String body = getRequestParams(maps,ServerAPI.getRemoveBlackList.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getRemoveBlackList.buildString(), QHResultState.class, body,
                new Response.Listener<QHResultState>() {

                    @Override
                    public void onResponse(QHResultState user) {
                        if (user.getSuccessful().equals("true")) {
                            Toast.makeText(getApplicationContext(), "移除黑名单成功", Toast.LENGTH_LONG).show();
                            MessageHandle.getInstance().updateToIMContact(id);
                            //更新黑名单
                            getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_BLACKLIST));
                        } else {
                            Toast.makeText(getApplicationContext(), "移除黑名单失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "removeBlackList", AppUtils.dynamicKey);
    }

    /**
     * 获取黑名单
     */
    private void getBlackLists() {
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getBlackList.buildString(), QHBlackList.class,
                new Response.Listener<QHBlackList>() {

                    @Override
                    public void onResponse(QHBlackList user) {
                        Log.e("====获取黑名单QHAttention: " + user.toString());
//                        blackList = user;//绑定回调
                        MessageHandle.getInstance().saveTOIMBlackLists(user);
                        AppUtils.getInstance().setQHBlackLists(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "getBlackLists",AppUtils.dynamicKey);
    }

    /**
     * 添加关注
     */
    private void addAttention(final String id) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("attention_uid", id);
        String body = getRequestParams(maps,ServerAPI.getAddPayAttentionList.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getAddPayAttentionList.buildString(), QHResultState.class, body,
                new Response.Listener<QHResultState>() {

                    @Override
                    public void onResponse(QHResultState user) {
                        if (user.getSuccessful().equals("true")) {
                            Toast.makeText(getApplicationContext(), "添加关注成功", Toast.LENGTH_LONG).show();
                            //更新我关注的人
                            getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_SAVE_ATTENTION));
                            //刷新
//                            Log.e("==刷新==添加关注QHAttention: " + user.toString());
//                            getApplicationContext().sendBroadcast(new Intent(IMConst.ACTION_PAYATTENTIONTOME));
                        } else {
                            Toast.makeText(getApplicationContext(), "已添加关注", Toast.LENGTH_LONG).show();
                        }
                        Log.e("====添加关注QHAttention: " + user.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "添加关注user=onErrorResponse" + error);
                    }
                }, "", AppUtils.dynamicKey);
    }

    /**
     * 群组
     */
    private void getGroupDatas() {
        CMIMHelper.getCmGroupManager().getGroupListFromServer(new CMChatListener.OnGroupListener() {
            @Override
            public void onSuccess(List<CMGroup> groups) {
                Log.e("====群组onSuccess: " + groups.toString() + groups.size());
                //获取群列表成功的处理,groups 为拥有的群列表,包含有群 id,群名称,群的免打扰状态
                MessageHandle.getInstance().saveToIMGroupList(groups);
                allContacts = groups;
                if (allContacts == null) {
                    allContacts = new ArrayList<CMGroup>();
                }
                //更新我的群组
                AppUtils.getInstance().setGroupLists(groups);
            }

            @Override
            public void onFailed(String failedMsg) {
                // 获取群列表失败的处理,failedMsg 为失败原因
                Log.e("====群组failedMsg: " + failedMsg);
//                IMApp.getInstance().RedoLogin();//重新登录
//                allContacts = new ArrayList<CMGroup>();
//                MessageHandle.getInstance().saveToIMGroupList(allContacts);
            }
        });
    }
    private void getOnlyGroupDatas() {
        CMIMHelper.getCmGroupManager().getGroupListFromServer(new CMChatListener.OnGroupListener() {
            @Override
            public void onSuccess(List<CMGroup> groups) {
                Log.e("====群组onSuccess: " + groups.toString() + groups.size());
                allContacts = groups;
                if (allContacts == null) {
                    allContacts = new ArrayList<CMGroup>();
                }
                AppUtils.getInstance().setGroupLists(groups);
            }

            @Override
            public void onFailed(String failedMsg) {
                // 获取群列表失败的处理,failedMsg 为失败原因
                Log.e("====    private void getOnlyGroupDatas() 群组failedMsg: " + failedMsg);
//                IMApp.getInstance().RedoLogin();//重新登录
            }
        });
    }

    /**
     * 关注我的
     */
    private void getPayAttentionToMeUsers() {
        //   关注我的==ok
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getPayAttentionList.buildString(), QHAttention.class,
                new Response.Listener<QHAttention>() {

                    @Override
                    public void onResponse(QHAttention user) {
                        Log.e("====关注我的QHPayAttention: " + user.toString());
                        // 刷新
                        MessageHandle.getInstance().setFreshAttentionDatas(user);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "getPayAttentionToMeUsers",AppUtils.dynamicKey);
    }

    /**
     * 我关注的
     */
    private synchronized void getMyAttentionUsers() {
        //我关注的==ok
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getAttentionList.buildString(), QHAttention.class,
                new Response.Listener<QHAttention>() {

                    @Override
                    public void onResponse(QHAttention user) {
                        Log.e("====我关注的QHAttention: " + user.toString());
                        // 保存
                        MessageHandle.getInstance().saveToAttentionContacts(user.getResults());
                        // 刷新
                        MessageHandle.getInstance().setFreshAttentionDatas(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "==我关注的onErrorResponse" + error);
                    }
                }, "getMyAttentionUsers",AppUtils.dynamicKey);
    }

    /**
     * 我关注的 - 只保存
     */
    private synchronized void getMyAttentionUsersToSave() {
        //我关注的==ok
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getAttentionList.buildString(), QHAttention.class,
                new Response.Listener<QHAttention>() {

                    @Override
                    public void onResponse(QHAttention user) {
                        // 保存
                        MessageHandle.getInstance().saveToAttentionContacts(user.getResults());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "==我关注的onErrorResponse" + error);
                    }
                }, "getMyAttentionUsersToSave",AppUtils.dynamicKey);
    }

    /**
     * 加载推荐用户
     */
    private void getRecommendUsers() {
        RequestManager.getInstance().sendGsonRequestAESforGET(ServerAPI.getRecommendUsers.buildString(), QHRecommends.class,
                new Response.Listener<QHRecommends>() {

                    @Override
                    public void onResponse(QHRecommends user) {
                        Log.e("====推荐用户QHRecommend: " + user.toString());
                        if (user != null)
                            mRecommendedUsers = MessageHandle.getInstance().setFreshQHRecommendDatas(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "", AppUtils.dynamicKey);
    }

    /**
     * 获取请求加密参数
     * @return
     */
    public String getRequestParams(Map<String,Object> maps,String url){
        String data = "";
        try {
            data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,String> params = new HashMap<String, String>();
        params.put("data",data);
        String body = RequestManager.getInstance().appendParameter(url,params);
        return  body;
    }
    private void getRecommends(String recommendedUsers, int i) {
//        String url = ServerAPI.getRecommendUsers.buildString() + "?limit=10&offset=" + 10 * (i+1) + "&recommendedUsers=" + recommendedUsers;
//        String url = ServerAPI.getRecommendUsers.buildString() + "?limit=" + 10 * (i + 1) + "&offset=" + 10 * (i + 1) + "&recommendedUsers=" + recommendedUsers;
        String url = ServerAPI.getRecommendUsers.buildString() + "?limit=" + 10 * (i + 1) + "&offset=" + 10 * (i + 1);
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("recommendedUsers", recommendedUsers);
        String body = getRequestParams(maps,url);
        RequestManager.getInstance().sendGsonRequestAESforPOST(url, QHRecommends.class, body,
                new Response.Listener<QHRecommends>() {

                    @Override
                    public void onResponse(QHRecommends user) {
                        Log.e("====第" + offset + "次请求推荐用户QHRecommend: " + user.toString());
                        //再次请求
                        if (user != null)
                            mRecommendedUsers = MessageHandle.getInstance().setFreshQHRecommendDatas(user);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "", AppUtils.dynamicKey);
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        return serviceBinder;
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private ServiceBinder serviceBinder = new ServiceBinder();
//
//    public class ServiceBinder extends Binder implements BindBackDatas {
//
//        public ServiceBinder getService() {
//            return ServiceBinder.this;
//        }
//
//        @Override
//        public QHBlackList onBlackListSuceess() {
//            if (blackList == null) {
//                blackList = new QHBlackList();
//            }
//            return blackList;
//        }
//    }

    /**
     * 通讯录列表
     *
     * @param
     * @param
     */
    class NetWorkTask extends AsyncTask<Object, String, String> {

        private Context mContext;
        private String url;
        private String args;

        public NetWorkTask(Context mContext, String url, String args) {
            this.mContext = mContext;
            this.url = url;
            this.args = args;
        }

        @Override
        protected String doInBackground(Object... objects) {
            if (args.contains("-")) {
                args = args.replaceAll("-", "");
            }
            if (args.contains(" ")) {
                args = args.replaceAll(" ", "");
            }

            String result = "";
            HttpPost httpRequst = new HttpPost(url);//创建HttpPost对象
            QHToken tokenInfo = AppUtils.getQHToken(mContext);
            if (tokenInfo != null && !TextUtils.isEmpty(tokenInfo.token)) {
                httpRequst.addHeader("Authorization", tokenInfo.token);
                if (AppUtils.mjsessionid != null) {
                    httpRequst.addHeader("Cookie", "JSESSIONID=" + AppUtils.mjsessionid);
                }
            }
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            Map<String,Object> maps = new HashMap<String, Object>();
            maps.put("phoneNums", args);
            String data = "";
            try {
                data = AESEncrpt.Encrypt(new Gson().toJson(maps), AppUtils.dynamicKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            params.add(new BasicNameValuePair("phoneNums", args));
            params.add(new BasicNameValuePair("data", data));

            try {
                httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    result = EntityUtils.toString(httpEntity);//取出应答字符串
                    Log.e("==result", result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            android.util.Log.e("===result", result);
            JSONObject obj = null;
            //解析json
            QHMailList qhMailList = new QHMailList();
            ArrayList<QHMailList.ResultsEntity> lists = null;
            try {
                obj = new JSONObject(result);
                JSONArray jsonArray = obj.getJSONArray("results");
                lists = new ArrayList<QHMailList.ResultsEntity>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    QHMailList.ResultsEntity m = new QHMailList.ResultsEntity();
                    JSONObject user = (JSONObject) jsonArray.get(i);
                    m.setUserId(user.getString("userId") + "");
                    m.setNickname(user.getString("nickname") + "");
                    m.setAvatarUrl(user.getString("avatarUrl") + "");
                    m.setPhoneNum(user.getString("phoneNum") + "");
                    lists.add(m);
                }
                qhMailList.setResults(lists);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (lists != null) {
                MessageHandle.getInstance().setFreshMailLists(qhMailList);
            }
        }
    }
}
