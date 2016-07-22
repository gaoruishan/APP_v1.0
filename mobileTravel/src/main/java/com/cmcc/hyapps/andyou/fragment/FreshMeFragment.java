package com.cmcc.hyapps.andyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.FreshLoginActivity;
import com.cmcc.hyapps.andyou.activity.FriendsCircleMyTrendsActivity;
import com.cmcc.hyapps.andyou.activity.GuiderCollectionActivity;
import com.cmcc.hyapps.andyou.activity.GuiderPublishActivity;
import com.cmcc.hyapps.andyou.activity.GuiderRecommandActivity;
import com.cmcc.hyapps.andyou.activity.IndexActivity;
import com.cmcc.hyapps.andyou.activity.MeSettingActivity;
import com.cmcc.hyapps.andyou.activity.UserProfileActivity;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.IsHasMessage;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.model.QHUserDetails;
import com.cmcc.hyapps.andyou.service.MobileTravelToIMService;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.roundimageview.RoundedImageView;
import com.littlec.sdk.entity.CMGroup;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.grs.com.ims.IMApp;
import test.grs.com.ims.contact.ContactsActivity;
import test.grs.com.ims.contact.GroupContactsActivity;
import test.grs.com.ims.message.IMConst;
import test.grs.com.ims.message.IMContact;
import test.grs.com.ims.message.IMGroup;
import test.grs.com.ims.message.MessageHandle;
import test.grs.com.ims.message.MessageListActivity;
import test.grs.com.ims.message.UNIMContact;
import test.grs.com.ims.util.model.QHBlackList;

/**
 * 新的我的界面
 * Created by bingbing on 2015/10/27.
 */
public class FreshMeFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = "FreshMeFragment";

    private final int REQUEST_CODE_PUBLISH = 1;
    private final int REQUEST_CODE_FAVORITES = 2;
    private final int REQUEST_CODE_MESSAGE = 3;
    private final int REQUEST_CODE_SETTINGS = 4;
    private final int REQUEST_CODE_COLLECT = 5;

    //header
    private Button loginButton;

    private RoundedImageView mRoundedImageView;
    private TextView name, my_forcus, forcus_me, my_circle;
    private View has_login_layout;

    private View my_forcus_view, forcus_me_view;
    private TextView my_circle_view;

    private ImageView message;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_me, container, false);
        initActionBar(mView);
        initHeader(mView);
        initListItems(mView);
        return mView;
    }

    private void initActionBar(View view) {
        message = (ImageView) view.findViewById(R.id.message_icon);
        message.setOnClickListener(this);
        red_point = (ImageView) view.findViewById(R.id.home_fragment_message_red_point);
    }

    private void initHeader(View view) {
        loginButton = (Button) view.findViewById(R.id.me_header_view_login_button);
        loginButton.setOnClickListener(this);

        mRoundedImageView = (RoundedImageView) view.findViewById(R.id.me_header_view_avator);
        mRoundedImageView.setOnClickListener(this);

        name = (TextView) view.findViewById(R.id.me_header_view_name);
        my_forcus = (TextView) view.findViewById(R.id.me_header_view_my_forcus);
        my_forcus_view = view.findViewById(R.id.me_header_view_my_forcus_view);
        my_forcus_view.setOnClickListener(this);
        forcus_me = (TextView) view.findViewById(R.id.me_header_view_forcus_me);
        forcus_me_view = view.findViewById(R.id.me_header_view_forcus_me_view);
        forcus_me_view.setOnClickListener(this);
        my_circle = (TextView) view.findViewById(R.id.me_header_view_my_circles);
        my_circle_view = (TextView) view.findViewById(R.id.me_header_view_my_circles_view);
        my_circle_view.setOnClickListener(this);
        has_login_layout = view.findViewById(R.id.me_header_view_has_login_layout);
        my_forcus.setOnClickListener(this);
        forcus_me.setOnClickListener(this);
        my_circle.setOnClickListener(this);
        //群组监听
        AppUtils.getInstance().setOnRecivedMessageListener(new AppUtils.OnSetBackSuceessListener() {
            @Override
            public void onBlackListSuceess(QHBlackList list) {

            }

            @Override
            public void onGroupListSuceess(List<CMGroup> groups) {
                setGroupSize(groups);
            }
        });
    }

    private void setGroupSize(List<CMGroup> groups) {
        List<IMGroup> imGroups = MessageHandle.getInstance().getIMGroups();
        int save = 0;
        for (int i = 0; i < groups.size(); i++) {
            String groupId = groups.get(i).getGroupId();
            for (int j = 0; j < imGroups.size(); j++) {
                if (imGroups.get(j).getGroupId().equals(groupId)) {
                    save++;
                    break;
                }
            }
        }
        int i = groups.size() - save;
        my_circle.setText((i <= 0 ? 0 : i) + "");
    }

    private void initListItems(View container) {
        setItem(container, R.id.item_my_trends, R.drawable.me_fragment_trends, R.string.me_item_trends);
        setItem(container, R.id.item_my_publish, R.drawable.me_fragment_publish, R.string.me_item_publish);
        setItem(container, R.id.item_my_collect, R.drawable.me_fragment_collect, R.string.me_item_collect);
        setItem(container, R.id.item_share, R.drawable.me_fragment_share, R.string.me_item_share);
        setItem(container, R.id.item_setting, R.drawable.me_fragment_setting, R.string.me_item_settings);
    }

    private void setItem(View container, int id, int iconId, int textId) {
        View item = container.findViewById(id);
        item.setOnClickListener(this);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        icon.setImageResource(iconId);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        text.setText(textId);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        if (UserManager.isLogin(this.getActivity())){
          isHasXiaoXiMessage();
        }else{
            if (red_point != null && red_point.isShown()){
                red_point.setVisibility(View.GONE);
            }
        }
        bindUserInfo();
    }

    private void bindUserInfo() {
        QHUser user = AppUtils.getQHUser(getActivity());
        if (user == null) {
            loginButton.setVisibility(View.VISIBLE);
            has_login_layout.setVisibility(View.GONE);
            return;
        }
        loginButton.setVisibility(View.GONE);
        has_login_layout.setVisibility(View.VISIBLE);
        //获得用户详情
        getUserDetail();

        boolean localLoaded = false;
        if (user.user_info == null) {
            return;
        }
//        if (!TextUtils.isEmpty(user.user_info.avatar_url)) {
//            if (FileUtils.fileCached(getActivity(), user.user_info.avatar_url)) {
//                String url = FileUtils
//                        .getCachePath(getActivity(), user.user_info.avatar_url);
//                Bitmap bm = FileUtils.getLocalBitmap(url);
//                if (bm != null) {
//                    mRoundedImageView.setImageBitmap(bm);
//                    localLoaded = true;
//                }
//            }
//        }
//        if (!localLoaded) {
//            // If not find the target avatar, it means that the avatar has been updated. so we should delete the old one.
//            String oldAvatarUrl = AppUtils.getOldAvatarUrl(getActivity());
//            if (!TextUtils.isEmpty(oldAvatarUrl)) {
//                FileUtils.delFile(FileUtils.getCachePath(getActivity(), oldAvatarUrl));
//            }
//            ImageUtil.DisplayImage(user.user_info.avatar_url, mRoundedImageView, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//            android.util.Log.e("===username" + user.username, "nick=" + user.user_info.nickname);
//        }
//         name.setText(user.username);
        if (!TextUtils.isEmpty(user.user_info.nickname))
            name.setText(user.user_info.nickname);
        else
            name.setText(user.username);
    }


    private void getUserDetail() {
        //发送广播获取我关注的人
//        getActivity().sendBroadcast(new Intent(IMConst.ACTION_MYATTENTION));

        QHUser qhUser = AppUtils.getQHUser(getActivity());
        if (qhUser == null) {
            return;
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("user_id", qhUser.id);
        String body = getRequestParams(maps, ServerAPI.getUserDetailList.buildString());
        //用户详情==ok\
        RequestManager.getInstance().sendGsonRequestAESforPOST(ServerAPI.getUserDetailList.buildString(), QHUserDetails.class, body,
                new Response.Listener<QHUserDetails>() {

                    @Override
                    public void onResponse(QHUserDetails user) {
                        //   Toast.makeText(getActivity(),"获取用户详情失败"+ user.toString(),Toast.LENGTH_SHORT).show();

                        Log.e("====用户详情: " + user.toString());
                        forcus_me.setText(user.getIsAttentionedNum() + "");
                        my_forcus.setText(user.getPayAttentionNum() + "");
                        name.setText(user.getNickname() + "");
                        ImageUtil.DisplayImage(user.getAvatarUrl(), mRoundedImageView, R.drawable.recommand_bgs, R.drawable.recommand_bg);
                        //传nick,avataUrl
                        IMApp.setCurrentAvataUrl(user.getAvatarUrl());
                        IMApp.setCurrentUserNick(user.getNickname());
                        List<CMGroup> allContacts = MobileTravelToIMService.allContacts;
                        if (allContacts == null) {
                            my_circle.setText(0 + "");
                        } else {
                            setGroupSize(allContacts);
//                            List<IMGroup> imGroups = MessageHandle.getInstance().getIMGroups();
//                            int i = allContacts.size() - imGroups.size();
//                            my_circle.setText((i<=0?0:i) + "");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "onErrorResponse" + error);
//                        Toast.makeText(getActivity(),"获取用户详情失败"+ error,Toast.LENGTH_SHORT).show();

                    }
                }, "", AppUtils.dynamicKey);
    }


    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        Intent intent = null;
        switch (v.getId()) {
            case R.id.me_header_view_login_button:
                QHUser user = AppUtils.getQHUser(getActivity());
                if (user == null) {
                    intent = new Intent(getActivity(), FreshLoginActivity.class);
                }
                break;
            case R.id.me_header_view_avator:
                QHUser user1 = AppUtils.getQHUser(getActivity());
                if (user1 != null) {
                    intent = new Intent(getActivity(), UserProfileActivity.class);
                    intent.putExtra("user", user1);
                }
                break;
            case R.id.item_my_trends:
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_COLLECT)) {
                    intent = new Intent(FreshMeFragment.this.getActivity(), FriendsCircleMyTrendsActivity.class);
                    intent.putExtra("user_id", AppUtils.getQHUser(FreshMeFragment.this.getActivity()).id);
                }
                break;
            case R.id.item_my_publish:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_TRIP);
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_PUBLISH)) {
                    intent = new Intent(getActivity(), GuiderPublishActivity.class);
                }
                break;
            case R.id.item_my_collect:
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_COLLECT)) {
                    intent = new Intent(getActivity(), GuiderCollectionActivity.class);
                }
                break;
            case R.id.item_share:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_SETTINGS);
                intent = new Intent(getActivity(), GuiderRecommandActivity.class);
                break;
            case R.id.item_setting:
                intent = new Intent(getActivity(), MeSettingActivity.class);
                break;
            case R.id.message_icon:
                //跳到消息列表
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_PUBLISH)) {
                    intent = new Intent(getActivity(), MessageListActivity.class);
                }
                break;
            case R.id.me_header_view_my_forcus_view:
            case R.id.me_header_view_my_forcus:
                //跳到我关注的
                startActivity(new Intent(getActivity(), ContactsActivity.class).putExtra(IMConst.NAVIGATE_DESTINATION, 2));
                break;
            case R.id.me_header_view_forcus_me_view:
            case R.id.me_header_view_forcus_me:
                //跳到关注我的
                startActivity(new Intent(getActivity(), ContactsActivity.class).putExtra(IMConst.NAVIGATE_DESTINATION, 1));
                break;
            case R.id.me_header_view_my_circles:
            case R.id.me_header_view_my_circles_view:
                //跳到群组
                startActivity(new Intent(getActivity(), GroupContactsActivity.class));
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}