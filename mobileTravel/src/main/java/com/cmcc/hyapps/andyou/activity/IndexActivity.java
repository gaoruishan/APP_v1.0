
package com.cmcc.hyapps.andyou.activity;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.fragment.FreshHomeFragment;
import com.cmcc.hyapps.andyou.fragment.FreshMeFragment;
import com.cmcc.hyapps.andyou.fragment.FriendsCircleFragment;
import com.cmcc.hyapps.andyou.service.FriendsCircleBroadcast;
import com.cmcc.hyapps.andyou.service.FriendsIsHasMessageServices;
import com.cmcc.hyapps.andyou.util.CheckUpdateUtil;
import com.cmcc.hyapps.andyou.widget.BadgeView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.ShareManager;
import com.cmcc.hyapps.andyou.fragment.HomeFragment;
import com.cmcc.hyapps.andyou.fragment.MarketFragment;
import com.cmcc.hyapps.andyou.media.PlaybackService;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.service.LocationService;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.LocationUtil;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.BottomTab;
import com.cmcc.hyapps.andyou.widget.BottomTab.OnTabSelected;

import test.grs.com.ims.message.IMConst;

public class IndexActivity extends BaseActivity implements OnClickListener, FriendsCircleBroadcast.OnFriendsCircleBroadcastListener {
    /**
     * Position of tab.
     */
    private static final int POS_HOME = 0;
    private static final int POS_MARKET = POS_HOME + 1;
    private static final int POS_FRIENDS_CIRCLE = POS_MARKET + 1;
    private static final int POS_ME = POS_FRIENDS_CIRCLE + 1;

    private static final int REQ_SELECT_LOCATION = 1;
    private static final int POS_REQUEST_CODE = 1005;

    private BottomTab mBottomTab;
    private ImageView red_point;
    private long mBackPressedTime;

    private Fragment mHomeFragment = null;
    private Fragment mMarketFragment = null;
    private Fragment mFriendsCircleFragment = null;
    private Fragment mMeFragment = null;
    private Fragment mCurrentFragment = null;

    private View mLocationSelectorView = null;

    private PopupWindow mPopupWindow;

    private boolean isHas = false;
    private FriendsCircleBroadcast mBroadcast;
    public static final String CANCLE_LOGIN_BROADCASE = "com.cancleLoginBroadcast";
    public static final String FRIENDS_CRICLE_BROADCAST = "com.friendsCircleBroadcast";

    private void registerFriendsCircleBroadcast() {
        mBroadcast = new FriendsCircleBroadcast();
        mBroadcast.setCircleBroadcastListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FRIENDS_CRICLE_BROADCAST);
        intentFilter.addAction(IMConst.ACTION_RGISTER);
        registerReceiver(mBroadcast, intentFilter);
    }

    private BroadcastReceiver cancleBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CANCLE_LOGIN_BROADCASE.equals(action)) {
                if (red_point != null) {
                    red_point.setVisibility(View.INVISIBLE);
                }
                getFragmentManager().beginTransaction().remove(mFriendsCircleFragment);
                mFriendsCircleFragment = null;
            }
        }
    };

    private void registerCancleBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CANCLE_LOGIN_BROADCASE);
        registerReceiver(cancleBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        //开启后台轮询是否有新的动态或者消息
        registerFriendsCircleBroadcast();
        registerCancleBroadcast();
        Intent intent = new Intent(this, FriendsIsHasMessageServices.class);
        startService(intent);

        LocationUtil.getInstance(this);

        try {
            CheckUpdateUtil.getInstance(this).getUpdataInfo(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBottomTab = (BottomTab) findViewById(R.id.bottom_tab);
        mBottomTab.setOnTabSelected(new OnTabSelected() {

            //点击时触发的点击事件，通过回调接口形式提供给IndexActivity
            @Override
            public void onTabSeledted(int index) {
                Fragment fragment = null;
                switch (index) {
                    //Home
                    case POS_HOME:
                        if (mHomeFragment == null) {
                            mHomeFragment = new FreshHomeFragment();
                        }
                        fragment = mHomeFragment;
                        MobclickAgent.onEvent(getBaseContext(), MobConst.ID_INDEX_TAB, MobConst.VALUE_INDEX_TAB_SCENIC);
                        break;
                    case POS_MARKET:
                        if (mMarketFragment == null) {
                            mMarketFragment = new MarketFragment();
                        }
                        fragment = mMarketFragment;
                        MobclickAgent.onEvent(getBaseContext(), MobConst.ID_INDEX_TAB, MobConst.VALUE_INDEX_TAB_DISCOVERY);
                        break;
                    case POS_FRIENDS_CIRCLE:
                        red_point.setVisibility(View.INVISIBLE);
                        if (!UserManager.isLogin(IndexActivity.this)) {
                            mBottomTab.selectTab(mBottomTab.getBeforeTabIndex());
                            goToLogin();
                            return;
                        }
                        if (mFriendsCircleFragment == null) {
                            mFriendsCircleFragment = new FriendsCircleFragment();
                        }
                        fragment = mFriendsCircleFragment;
                        MobclickAgent.onEvent(getBaseContext(), MobConst.ID_INDEX_TAB, MobConst.VALUE_INDEX_TAB_LIVE);
                        break;
                    case POS_ME:
                        if (mMeFragment == null) {
                            mMeFragment = new FreshMeFragment();
                        }
                        fragment = mMeFragment;
                        MobclickAgent.onEvent(getBaseContext(), MobConst.ID_INDEX_TAB, MobConst.VALUE_INDEX_TAB_ME);
                        break;

                    default:
                        Log.e("unknown position of SectionsPagerAdapter: " + index);
                        if (mHomeFragment == null) {
                            mHomeFragment = new HomeFragment /*ScenicDetailsFragment*/();
                        }
                        fragment = mHomeFragment;
                        break;
                }

                if (!fragment.isAdded()) {
                    Bundle args = fragment.getArguments();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putString(Const.ARGS_REQUEST_TAG, requestTag);
                    fragment.setArguments(args);
                }

                if (activity != null && !activity.isFinishing()) {
                    //当前fragment为空
                    if (mCurrentFragment == null) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.container, fragment).commitAllowingStateLoss();
                    } else if (fragment.isAdded()) {
                        //当前fragment不为空，但是要显示的fragment已经添加过了，隐藏当前currentFragment，直接显示fragment
                        // TODO: mCurrentFragment is still attached to view
                        // hierarchy
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .show(fragment)
                                .commitAllowingStateLoss();
                    } else {
                        //fragment还没有添加过，隐藏当前currentFragment，显示新的fragment
                        getFragmentManager().beginTransaction().hide(mCurrentFragment)
                                .add(R.id.container, fragment).commitAllowingStateLoss();
                    }
                } else {
                    Log.e("Activity is leaving");
                }
                mCurrentFragment = fragment;
            }
        });

        mLocationSelectorView = findViewById(R.id.scenic_select_location);
        mLocationSelectorView.setOnClickListener(this);
        red_point = (ImageView) findViewById(R.id.bottom_tab_red_point);
        red_point.setVisibility(View.INVISIBLE);
        UmengUpdateAgent.update(this);
        // Remove this according new requirement
//        if (!PreferencesUtils.getBoolean(getApplicationContext(),
//                PreferencesUtils.KEY_INDEX_FIRST_START)) {
//            PreferencesUtils.putBoolean(getApplicationContext(),
//                    PreferencesUtils.KEY_INDEX_FIRST_START, true);
//            Intent intent = new Intent(activity, ScenicDetailMaskActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在每个activity中都绑定
        // 用来保证获取正确的新增用户、活跃用户、启动次数、使用时长等基本数据
        MobclickAgent.onResume(activity);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(activity);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (!ShareManager.getInstance().hideBorad()) {
            if ((System.currentTimeMillis() - mBackPressedTime) > 2000) {
                mBackPressedTime = System.currentTimeMillis();
                ToastUtils.show(activity, R.string.press_back_to_exit);
            } else {
                super.onBackPressed();
                cleanUp();
                // Exit application if back pressed.
                finish();
            }
        }
    }

    private void cleanUp() {
        stopService(new Intent(this, PlaybackService.class));
        stopService(new Intent(this, LocationService.class));
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.scenic_select_location: {
                Intent intent = new Intent(this, CityChooseActivity.class);
                startActivityForResult(intent, REQ_SELECT_LOCATION);
                break;
            }

            default:
                break;
        }
    }

    public void showLocationSelector() {
        mLocationSelectorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_SELECT_LOCATION && data != null) {
            Location location = (Location) data.getParcelableExtra(Const.EXTRA_COORDINATES);
            if (location != null && location.isValid()) {
                mLocationSelectorView.setVisibility(View.GONE);
                Intent intent = new Intent(LocationService.ACTION_UPDATE_LOCATION);
                intent.putExtra(Const.EXTRA_COORDINATES, location);
                startService(intent);
                Log.d("Mannally set city to %s", location);
            } else {
                Log.e("Invalid location %s", location);
            }
        } else if (resultCode == RESULT_OK && requestCode == POS_REQUEST_CODE) {
            mBottomTab.selectTab(POS_FRIENDS_CIRCLE);
            if (mBottomTab.getOnTabSelected() != null)
                mBottomTab.getOnTabSelected().onTabSeledted(POS_FRIENDS_CIRCLE);
        } else if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcast);
        unregisterReceiver(cancleBroadcastReceiver);
        LocationUtil.getInstance(AppUtils.getContext()).destroyAMapLocationListener();
    }

    private void goToLogin() {
        View view = LayoutInflater.from(this).inflate(R.layout.friends_circle_goto_login, null);
        Button loginButton = (Button) view.findViewById(R.id.frieds_no_login_button);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                Intent login = new Intent(activity, FreshLoginActivity.class);
                startActivityForResult(login, POS_REQUEST_CODE);
            }
        });
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.frieds_no_login_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setContentView(view);
        mPopupWindow.showAtLocation(mBottomTab, Gravity.CENTER, 0, 0);
    }

    public void startAlarm(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, pendingIntent);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onFriendsCircleBroadcast(Intent intent) {
        Log.e("回调成功", "show red");
        if (intent != null) {
            isHas = intent.getBooleanExtra("isHas", false);
            if (isHas) {
                //将红点显示出来,如果正处在朋友圈页面，则不需要显示
                if (mBottomTab != null && mBottomTab.getCurrentTabIndex() != 2 && red_point != null)
                    red_point.setVisibility(View.VISIBLE);
            }
        }
        Intent i = new Intent(this, FriendsIsHasMessageServices.class);
        startAlarm(i);
    }

}
