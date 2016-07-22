/**
 *
 */

package com.cmcc.hyapps.andyou.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.FavoritesActivity;
import com.cmcc.hyapps.andyou.activity.FeedbackActivity;
import com.cmcc.hyapps.andyou.activity.GuiderCollectionActivity;
import com.cmcc.hyapps.andyou.activity.GuiderMessageListActivity;
import com.cmcc.hyapps.andyou.activity.GuiderPublishActivity;
import com.cmcc.hyapps.andyou.activity.GuiderRecommandActivity;
import com.cmcc.hyapps.andyou.activity.LoginActivity;
import com.cmcc.hyapps.andyou.activity.QHAboutActivity;
import com.cmcc.hyapps.andyou.activity.SettingsActivity;
import com.cmcc.hyapps.andyou.activity.UserProfileActivity;
import com.cmcc.hyapps.andyou.app.MobConst;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.helper.CacheClearHelper;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.CheckUpdateUtil;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;

/**
 * @author kuloud
 */
public class MeFragment extends BaseFragment implements OnClickListener {
    private final String TAG = "MeFragment";

    private final int REQUEST_CODE_PUBLISH = 1;
    private final int REQUEST_CODE_FAVORITES = 2;
    private final int REQUEST_CODE_MESSAGE = 3;
    private final int REQUEST_CODE_SETTINGS = 4;
    private final int REQUEST_CODE_COLLECT = 5;

    private NetworkImageView mAvatar = null;
    private TextView mName = null;
    private View me_header;
    private TextView desc;
    private long cachesize;

    public MeFragment() {
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        initActionBar(view);
        me_header = view.findViewById(R.id.me_header);
        mAvatar = (NetworkImageView) view.findViewById(R.id.iv_me_avata);
        mName = (TextView) view.findViewById(R.id.tv_me_name);
        me_header.setOnClickListener(this);
        initListItems(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        updateCacheSize();
        bindUserInfo(AppUtils.getQHUser(getActivity()));
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    private void initListItems(View container) {
        setItem(container, R.id.item_publish, R.drawable.publish, R.string.me_item_publish);
        setItem(container, R.id.item_collect, R.drawable.collect, R.string.me_item_collect);
        setItem(container, R.id.item_message, R.drawable.message, R.string.me_item_message);
        setItem(container, R.id.item_share, R.drawable.share, R.string.me_item_share);
        setItem(container, R.id.item_renovate, R.drawable.renovate, R.string.me_item_renovate);
        setItem(container, R.id.item_clear, R.drawable.delete, R.string.me_item_clear,true);
        setItem(container, R.id.item_feedback, R.drawable.feedback, R.string.settings_item_feedback);
        setItem(container, R.id.item_about, R.drawable.about, R.string.me_item_about);
    }

    private void setItem(View container, int id, int iconId, int textId) {
        View item = container.findViewById(id);
        item.setOnClickListener(this);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        icon.setImageResource(iconId);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        text.setText(textId);
    }
    private void setItem(View container, int id, int iconId, int textId,boolean bool) {
        View item = container.findViewById(id);
        item.setOnClickListener(this);
        ImageView icon = (ImageView) item.findViewById(R.id.item_icon);
        icon.setImageResource(iconId);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        ImageView next = (ImageView)item.findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        desc = (TextView)item.findViewById(R.id.desc);
//        desc.setText(getActivity().getCacheDir().getTotalSpace() + "");
        text.setText(textId);
    }

    private void initActionBar(View view) {
        ActionBar actionBar = (ActionBar) view.findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_me);
        actionBar.setBackgroundResource(R.color.title_bg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                case REQUEST_CODE_SETTINGS:
//                    mAvatar.setImageResource(R.drawable.bg_avata_hint);
//                    mName.setText("");
//                    break;
                case REQUEST_CODE_PUBLISH:
                    Intent publish = new Intent(getActivity(), GuiderPublishActivity.class);
                    startActivity(publish);
                    break;
                case REQUEST_CODE_FAVORITES:
                    Intent favorites = new Intent(getActivity(), FavoritesActivity.class);
                    startActivity(favorites);
                    break;
                case REQUEST_CODE_MESSAGE:
                    Intent message = new Intent(getActivity(), GuiderMessageListActivity.class);
                    startActivity(message);
                    break;

                case REQUEST_CODE_COLLECT:
                    Intent collect = new Intent(getActivity(), GuiderCollectionActivity.class);
                    startActivity(collect);
                    break;

                default:
                    break;
            }
        }
    }

    private void bindUserInfo(QHUser user) {
        if (user == null) {
            mAvatar.setImageResource(R.drawable.bg_avata_hint);
            mName.setText(R.string.login_quickly);
            return;
        }
        boolean localLoaded = false;
        if(null!=user.user_info.avatar_url) {
            if (FileUtils.fileCached(getActivity(), user.user_info.avatar_url)) {
                String url = FileUtils
                        .getCachePath(getActivity(), user.user_info.avatar_url);
                Bitmap bm = FileUtils.getLocalBitmap(url);
                if (bm != null) {
                    mAvatar.setImageBitmap(bm);
                    localLoaded = true;
                }
            }
        }
        if (!localLoaded) {
            // If not find the target avatar, it means that the avatar has been updated. so we should delete the old one.
            String oldAvatarUrl = AppUtils.getOldAvatarUrl(getActivity());
            if (!TextUtils.isEmpty(oldAvatarUrl)) {
                FileUtils.delFile(FileUtils.getCachePath(getActivity(), oldAvatarUrl));
            }
            ImageUtil.DisplayImage(user.user_info.avatar_url, mAvatar, R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
//            mAvatar.setDefaultImageResId(R.drawable.bg_avata_hint);
//            mAvatar.setErrorImageResId(R.drawable.bg_avata_hint);
//            mAvatar.setImageUrl(user.user_info.avatar_url, RequestManager.getInstance().getImageLoader());

        }
        mName.setText(user.username);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        Intent intent = null;
        QHUser user = AppUtils.getQHUser(getActivity());
        switch (v.getId()) {
            case R.id.me_header:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_AVATA);
                if (user == null) {
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    startActivity(login);
                } else {
                    Intent info = new Intent(getActivity(), UserProfileActivity.class);
                    info.putExtra("user", user);
                    startActivity(info);
                }
                break;
            case R.id.item_publish:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_TRIP);
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_PUBLISH)) {
                    intent = new Intent(getActivity(), GuiderPublishActivity.class);
                }
                break;
            case R.id.item_feedback:
                    intent = new Intent(getActivity(), FeedbackActivity.class);
               // new FeedbackAgent(getActivity()).startFeedbackActivity();
                break;
            case R.id.action_bar_right:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_SETTINGS);
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(settings, REQUEST_CODE_SETTINGS);
                break;
            case R.id.item_collect:
                if (!UserManager.makeSureLogin(getActivity(), REQUEST_CODE_COLLECT)) {
                    intent = new Intent(getActivity(), GuiderCollectionActivity.class);
                }
                break;
            case R.id.item_share:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_SETTINGS);
                Intent recommend = new Intent(getActivity(), GuiderRecommandActivity.class);
                startActivity(recommend);
                break;
            case R.id.item_message:
                MobclickAgent.onEvent(getActivity(), MobConst.ID_INDEX_ME_SETTINGS);
                if (!UserManager.makeSureLogin(getActivity(),REQUEST_CODE_MESSAGE)){
                    intent = new Intent(getActivity(), GuiderMessageListActivity.class);
                }
                break;
            case R.id.item_clear:
                CacheClearHelper.clearCache(getActivity());
                updateCacheSize();
                break;
            case R.id.item_about:
                intent = new Intent(getActivity(), QHAboutActivity.class);
                break;
            case R.id.item_renovate:
                try {
                    CheckUpdateUtil.getInstance(getActivity()).getUpdataInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void updateCacheSize() {
        try {
            queryPacakgeSize(getActivity().getPackageName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void  queryPacakgeSize(String pkgName) throws Exception{
        if ( pkgName != null){
            //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager pm = getActivity().getPackageManager();  //得到pm对象
            try {
                //通过反射机制获得该隐藏函数
                Method getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
                //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
                getPackageSizeInfo.invoke(pm, pkgName,new PkgSizeObserver());
            }
            catch(Exception ex){
                ex.printStackTrace() ;
                throw ex ;  // 抛出异常
            }
        }
    }

    public class PkgSizeObserver extends IPackageStatsObserver.Stub{
        /*** 回调函数，
         * @param pStats ,返回数据封装在PackageStats对象中
         * @param succeeded  代表回调成功
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            // TODO Auto-generated method stub
            cachesize = pStats.cacheSize  ; //缓存大小
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    desc.setText(formateFileSize(cachesize));
                }
            });

        }
    }

    private String formateFileSize(long size){
        return Formatter.formatFileSize(getActivity(), size);
    }


}
