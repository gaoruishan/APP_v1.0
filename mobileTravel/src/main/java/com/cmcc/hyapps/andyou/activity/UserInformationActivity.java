package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.QHUserDetails;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;

import java.util.HashMap;
import java.util.Map;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.contact.ContactsTaActivity;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.MessageActivity;

/**
 * 用户详细信息页面
 * Created by bingbing on 2015/10/28.
 */
public class UserInformationActivity extends BaseActivity implements View.OnClickListener {

    private RoundedImageView mRoundedImageView;
    private TextView nameTextView, addressTextView, introduceTextView;
    private TextView tv_isAttention;
    private RelativeLayout addForcus, start_chat;
    private TextView his_forcus, his_fans;
    private QHUserDetails mQHUserDetails;
    private String userID;
    private String nickName;
    private String avatarUrl;
    private View his_trends, his_publish, his_collected;
    private int hasAttentioned;

    private View his_forcus_view, his_fans_view;
    private boolean isChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_user_information);
        initActionBar();
        initView();
        initListItems();
        userID = getIntent().getStringExtra("user_ID");
        getUserInformation(userID);
    }

    private void getUserInformation(final String user_ID) {
        Map<String,Object> maps = new HashMap<String, Object>();
        maps.put("user_id",user_ID);
        String body = getRequestParams(maps,ServerAPI.getUserDetailList.buildString());
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getUserDetailList.buildString(), QHUserDetails.class,body,
                new Response.Listener<QHUserDetails>() {

                    @Override
                    public void onResponse(QHUserDetails user) {
                        Log.e("====刷新用户详细QHPayAttention: " + user.toString());
                        if (user == null) return;
                        mQHUserDetails = user;
                        avatarUrl = user.getAvatarUrl();
                        ImageUtil.DisplayImage(user.getAvatarUrl(), mRoundedImageView,
                                R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
                        his_forcus.setText(user.getPayAttentionNum() + "");
                        his_fans.setText(user.getIsAttentionedNum() + "");
                        hasAttentioned = user.getHasAttentioned();
                        if (user.getHasAttentioned() == 1) {
                            tv_isAttention.setText("取消关注");
                        } else {
                            tv_isAttention.setText("加关注");
                            Drawable drawable = getResources().getDrawable(R.drawable.user_information_add_forcus);
                            tv_isAttention.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                        }
                        if (!TextUtils.isEmpty(user.getNickname()))
                            nickName = user.getNickname();
                        nameTextView.setText(user.getNickname());
                        if (!TextUtils.isEmpty(user.getLocationArea()))
                            addressTextView.setText(user.getLocationArea());
                        if (!TextUtils.isEmpty(user.getIntroduction()))
                            introduceTextView.setText(user.getIntroduction());
                        setDrawableRight(nameTextView, user.getGender(), UserInformationActivity.this);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
                    }
                }, "", AppUtils.dynamicKey);
    }

    //    public static void refreshHasAttention(int hasAtte){
//        if (hasAtte==1){
//            tv_isAttention.setText("添加关注");
//            hasAttentioned = 0;
//        }else {
//            tv_isAttention.setText("取消关注");
//        }
//    }
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.action_bar_left:
                if (isChange){
                    setResult(RESULT_OK,new Intent().putExtra("isChange",isChange));
                }
                finish();
                break;
            case R.id.tv_isAttention:
                isChange = true;
                if (!userID.equals(IMApp.currentUserName)) {
                    if (hasAttentioned == 1) {
                        //取消关注
                        sendBroadcast(new Intent(IMConst.ACTION_REMOVE_ATTENTION).putExtra("userId", userID));
                    } else {
                        //添加关注
                        sendBroadcast(new Intent(IMConst.ACTION_ADD_ATTENTION).putExtra("userId", userID));
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //刷新
                            getUserInformation(userID);
                        }
                    }, 1000);

                }
                break;
            case R.id.user_information_his_trends:
                if (mQHUserDetails != null) {
                    intent = new Intent(this, FriendsCircleMyTrendsActivity.class);
                    intent.putExtra("user_id", mQHUserDetails.getUserId());
                }
                break;
            case R.id.user_information_his_travel:
                if (mQHUserDetails != null) {
                    intent = new Intent(this, GuiderPublishActivity.class);
                    intent.putExtra("user_id", mQHUserDetails.getUserId());
                }
                break;
//            case R.id.user_information_his_argument:
//                break;
            case R.id.user_information_his_collect:
                if (mQHUserDetails != null) {
                    intent = new Intent(this, GuiderCollectionActivity.class);
                    intent.putExtra("user_id", mQHUserDetails.getUserId());
                }
                break;
            case R.id.user_information_start_chat:
                //跳转聊天界面
                Intent i = new Intent(UserInformationActivity.this, MessageActivity.class);
                i.putExtra(IMConst.CHATTYPE, false);//单聊
                i.putExtra(IMConst.USERNAME, userID + "");//用户名
                i.putExtra(IMConst.AVATARURL, avatarUrl + "");
                i.putExtra(IMConst.NICKNAME, nickName + "");
                if (userID != null && nickName != null) {
                    startActivity(i);
                } else {
                    Toast.makeText(this, "网络数据加载中,暂不能跳转", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.user_information_his_forcus_view:
                //跳转到TA的关注人列表
                startActivity(new Intent(UserInformationActivity.this, ContactsTaActivity.class).putExtra(IMConst.NAVIGATE_DESTINATION, 2).putExtra(IMConst.USER_ID, userID));
                break;
            case R.id.user_information_his_fans_view:
                //跳转到关注TA的人列表
                startActivity(new Intent(UserInformationActivity.this, ContactsTaActivity.class).putExtra(IMConst.NAVIGATE_DESTINATION, 1).putExtra(IMConst.USER_ID, userID));
                break;

        }
        if (intent != null) {
            startActivity(intent);
        }

    }

    private void initView() {
        mRoundedImageView = (RoundedImageView) this.findViewById(R.id.user_information_avator);
        nameTextView = (TextView) this.findViewById(R.id.user_information_name);
        addressTextView = (TextView) this.findViewById(R.id.user_information_address);
        introduceTextView = (TextView) this.findViewById(R.id.user_information_introduce);
        tv_isAttention = (TextView) this.findViewById(R.id.tv_isAttention);
        his_forcus = (TextView) this.findViewById(R.id.user_information_his_forcus);
        his_fans = (TextView) this.findViewById(R.id.user_information_his_fans);
        his_forcus_view = this.findViewById(R.id.user_information_his_forcus_view);
        his_forcus_view.setOnClickListener(this);
        his_fans_view = this.findViewById(R.id.user_information_his_fans_view);
        his_fans_view.setOnClickListener(this);
        addForcus = (RelativeLayout) this.findViewById(R.id.user_information_add_forcus);
        start_chat = (RelativeLayout) this.findViewById(R.id.user_information_start_chat);
        addForcus.setOnClickListener(this);
        start_chat.setOnClickListener(this);
        his_forcus.setOnClickListener(this);
        his_fans.setOnClickListener(this);
        his_trends = this.findViewById(R.id.user_information_his_trends);
        his_trends.setOnClickListener(this);
        his_publish = this.findViewById(R.id.user_information_his_travel);
        his_publish.setOnClickListener(this);
        his_collected = this.findViewById(R.id.user_information_his_collect);
        his_collected.setOnClickListener(this);
        tv_isAttention.setOnClickListener(this);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) this.findViewById(R.id.action_bar);
        actionBar.setTitle("用户详情");
        actionBar.setBackgroundResource(R.color.title_bg);
        actionBar.getLeftView().setImageResource(R.drawable.return_back);
        actionBar.getLeftView().setOnClickListener(this);
    }

    private void initListItems() {
        setItem(R.id.user_information_his_trends, R.drawable.user_information_his_trends, R.string.user_information_his_trends);
        setItem(R.id.user_information_his_travel, R.drawable.user_information_his_travel, R.string.user_information_his_publish);
        //暂时先用我的发布来代替我的点评和我的游记
        //   setItem(R.id.user_information_his_argument, R.drawable.user_information_his_argument, R.string.user_information_his_argument);
        setItem(R.id.user_information_his_collect, R.drawable.user_information_his_collect, R.string.user_information_his_collect);
    }

    private void setItem(int id, int iconId, int textId) {
        View item = this.findViewById(id);
        item.setOnClickListener(this);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        icon.setImageResource(iconId);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        text.setText(textId);
    }

    public void setDrawableRight(TextView mTextView, int sexy, Context context) {
        Drawable drawable = null;
        if (sexy == 1)
            drawable = context.getResources().getDrawable(R.drawable.girl);
        else
            drawable = context.getResources().getDrawable(R.drawable.boy);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTextView.setCompoundDrawables(null, null, drawable, null);
    }

}
