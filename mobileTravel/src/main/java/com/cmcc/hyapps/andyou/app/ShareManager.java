/**
 *
 */

package com.cmcc.hyapps.andyou.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.PreferencesUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.UMShareBoardListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Location;
import com.cmcc.hyapps.andyou.model.TripDetail;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuloud
 */
public final class ShareManager {
    private Activity mActivity;
    private static final String HOME_PAGE = "http://ziyou01.com/";
    private static final String LISTEN_PAGE_FORMAT = "http://webapp.selftravel.com.cn/scenic_audio.html?title=%s&scenic_id=%d&spot_id=%d&lat=%f&lng=%f&url=%s";
    private static final String COMMENT_PAGE_FORMAT = "http://webapp.selftravel.com.cn/comment_detail.html?author=%s&id=%d&share=true";

    //TODO 青海移动项目组：在微信平台申请 WECHAT_APP_ID 和 WECHAT_APP_SECRET 替换如下变量值
    // https://open.weixin.qq.com/cgi-bin/index?t=home/index&lang=zh_CN
    private static final String WECHAT_APP_ID = "wx5f6784cbe24d3e66";
    private static final String WECHAT_APP_SECRET = "0c8e4da58b59cf62840ccf84b97df7cc";

    //TODO 青海移动项目组：在QQ平台申请 QQ_APP_ID 和 QQ_APP_KEY 替换如下变量值
    // http://connect.qq.com/
    private static final String QQ_APP_ID = "1105021272";
    private static final String QQ_APP_KEY = "fSmnj94ZyhBoMLdq";

    private static final String SHARE_DESCRIPTOR = "SelfTravel";
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(SHARE_DESCRIPTOR);
    private static ShareManager sShareManager;

    private boolean mBoardOpened = false;

    private ShareManager() {
    }

    synchronized public static ShareManager getInstance() {
        if (sShareManager == null) {
            sShareManager = new ShareManager();
        }
        return sShareManager;
    }

    public void share() {

    }

    public void onStart(Activity activity) {
        mActivity = activity;
        configPlatforms();
    }

    public void onEnd() {
        hideBorad();
        mActivity = null;
        mController.dismissShareBoard();
    }

    private void configPlatforms() {
        // Setup Sina SSO
        mController.getConfig().closeToast();
        //关闭位置分享
        mController.getConfig().setDefaultShareLocation(false);
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // add QQ、QZone
        addQQQZonePlatform();

        // add wechat
        addWXPlatform();

        addSMS();
    }

    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    private void addQQQZonePlatform() {
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity,
                QQ_APP_ID, QQ_APP_KEY);
        String apk_url = PreferencesUtils.getString(mActivity, "APK_URL");
        qqSsoHandler.setTargetUrl("http://111.44.243.117:81/indexDown.html"); // TODO
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity, QQ_APP_ID, QQ_APP_KEY);
        qZoneSsoHandler.addToSocialSDK();
    }

    private void addWXPlatform() {

        UMWXHandler wxHandler = new UMWXHandler(mActivity, WECHAT_APP_ID, WECHAT_APP_SECRET);
        String apk_url = PreferencesUtils.getString(mActivity, "APK_URL");
        wxHandler.setTargetUrl(apk_url);
        wxHandler.addToSocialSDK();
        wxHandler.showCompressToast(false);
        //支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, WECHAT_APP_ID, WECHAT_APP_SECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        //去掉sdk中的toast
        wxCircleHandler.showCompressToast(false);
    }

    public void openShare(Bitmap bitmap, String content) {
        mController.getConfig().enableSIMCheck(false);
        mController.setShareContent(content);
        UMImage imgBitmap = (bitmap == null) ? new UMImage(mActivity, R.drawable.ic_launcher)
                : new UMImage(mActivity, bitmap);
        mController.setShareMedia(imgBitmap);
        openShareBoard();
    }

    public void shareTripDetail(TripDetail tripDetail) {
        // TODO
    }

    public void shareAudio(int scenic_id, AudioIntro audioIntro, Location loc) {
        shareAudio(scenic_id, -1, audioIntro, loc);
    }

    public void shareAudio(int scenic_id, int spot_id, AudioIntro audioIntro, Location loc) {
        if (audioIntro == null) {
            return;
        }
        String appName = mActivity.getString(R.string.app_name);
        String audioUrl = audioIntro.url;
        String audioTitle = TextUtils.isEmpty(audioIntro.title) ? appName : audioIntro.title;
        Bitmap bitmap = audioIntro.imageBitmap;
        String content = audioIntro.content;

        // setup image
        UMImage resImage = null;
        if (bitmap == null) {
            resImage = new UMImage(mActivity, R.drawable.ic_launcher);
        } else {
            resImage = new UMImage(mActivity, bitmap);
        }

        // setup audio
        UMusic uMusic = null;
        if (!TextUtils.isEmpty(audioUrl)) {
            uMusic = new UMusic(audioUrl);
            uMusic.setAuthor(appName);
            uMusic.setTitle(audioTitle);
            uMusic.setThumb(resImage);
        }

        String targetUrl = getAudioTargetUrl(audioTitle, scenic_id, spot_id, loc, audioUrl);
        // Separate setup each platform, for customize according to the
        // situation diversification.
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        SinaShareContent sinaContent = new SinaShareContent();
        setupSinaShareContent(sinaContent, audioTitle, content, resImage, uMusic, targetUrl);

        WeiXinShareContent wechatContent = new WeiXinShareContent();
        setupWeChatShareContent(wechatContent, audioTitle, content, resImage, uMusic, targetUrl);
        CircleShareContent circleContent = new CircleShareContent();
        setupCircleShareContent(circleContent, audioTitle, content, resImage, uMusic, targetUrl);

        QQShareContent qqShareContent = new QQShareContent();
        setupQQShareContent(qqShareContent, audioTitle, content, resImage, uMusic, targetUrl);
        QZoneShareContent qzoneConent = new QZoneShareContent();
        setupQzoneShareContent(qzoneConent, audioTitle, content, resImage, uMusic, targetUrl);

        openShareBoard();
    }

    private void openShareBoard() {
        mController.getConfig().setPlatforms(
                SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.SMS);
        mController.openShare(mActivity, false);
        mController.setShareBoardListener(new UMShareBoardListener() {

            @Override
            public void onShow() {
                mBoardOpened = true;
            }

            @Override
            public void onDismiss() {
                mBoardOpened = false;
            }
        });
    }

    private String getAudioTargetUrl(String audioTitle, int scenic_id, int spot_id, Location loc,
                                     String audioUrl) {
        return String.format(Locale.CHINESE, LISTEN_PAGE_FORMAT, audioTitle, scenic_id, spot_id,
                loc.latitude,
                loc.longitude, audioUrl);
    }

    private void setupShareContent(BaseShareContent mediaContent, String title, String content,
                                   UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }
        if (!TextUtils.isEmpty(title)) {
            mediaContent.setTitle(title);
        }
        if (resImage != null) {
            mediaContent.setShareImage(resImage);
        }
        if (uMusic != null) {
            mediaContent.setShareMedia(uMusic);
        }
        if (!TextUtils.isEmpty(targetUrl))
            mediaContent.setTargetUrl(targetUrl);
        if (mController != null)
            mController.setShareMedia(mediaContent);

    }

    private void setupSinaShareContent(SinaShareContent mediaContent, String title, String content,
                                       UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }
        if (!TextUtils.isEmpty(title)) {
            mediaContent.setTitle(title);
        }
        if (resImage != null) {
            mediaContent.setShareImage(resImage);
        }
        if (uMusic != null) {
            mediaContent.setShareMedia(uMusic);
        }
        if (!TextUtils.isEmpty(targetUrl))
            mediaContent.setTargetUrl(targetUrl);
        if (mController != null)
            mController.setShareMedia(mediaContent);
    }

    private void setupWeChatShareContent(WeiXinShareContent mediaContent, String title,
                                         String content,
                                         UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }
        if (!TextUtils.isEmpty(title)) {
            mediaContent.setTitle(title);
        }
        if (resImage != null) {
            mediaContent.setShareImage(resImage);
        }
        if (uMusic != null) {
            mediaContent.setShareMedia(uMusic);
        }
        if (!TextUtils.isEmpty(targetUrl))
            mediaContent.setTargetUrl(targetUrl);
        if (mController != null)
            mController.setShareMedia(mediaContent);
    }

    private void setupWeChatShareContent(UMSocialService mController, WeiXinShareContent mediaContent, String title,
                                         String content,
                                         UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }

        mediaContent.setTitle(title);
        if (resImage != null) {
            mediaContent.setShareImage(resImage);
        }
        if (uMusic != null) {
            mediaContent.setShareMedia(uMusic);
        }

        mediaContent.setTargetUrl(targetUrl);

        mController.setShareMedia(mediaContent);
    }

    private void setupCircleShareContent(CircleShareContent mediaContent, String title,
                                         String content,
                                         UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }

        mediaContent.setTitle(title);
        mediaContent.setShareImage(resImage);

        mediaContent.setTargetUrl(targetUrl);

        mController.setShareMedia(mediaContent);
    }

    private void setupQQShareContent(QQShareContent mediaContent, String title, String content,
                                     UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }
        if (!TextUtils.isEmpty(title))
            mediaContent.setTitle(title);
        if (resImage != null)
            mediaContent.setShareImage(resImage);
        if (uMusic != null) {
            mediaContent.setShareMedia(uMusic);
        }
        if (!TextUtils.isEmpty(targetUrl))
            mediaContent.setTargetUrl(targetUrl);
        if (mController != null)
            mController.setShareMedia(mediaContent);
    }

    private void setSMSShareContent(SmsShareContent smsContent, String content) {
        smsContent.setShareContent(content);
        mController.setShareMedia(smsContent);
    }

    private void setupQzoneShareContent(QZoneShareContent mediaContent, String title,
                                        String content,
                                        UMImage resImage, UMusic uMusic, String targetUrl) {
        if (!TextUtils.isEmpty(content)) {
            mediaContent.setShareContent(content);
        }

        mediaContent.setTitle(title);
        mediaContent.setShareImage(resImage);

        mediaContent.setTargetUrl(targetUrl);

        mController.setShareMedia(mediaContent);
    }

    /**
     * @return
     */
    public boolean hideBorad() {
        if (mBoardOpened) {
            mController.dismissShareBoard();
            mBoardOpened = false;
            return true;
        }
        return false;
    }

    /**
     *
     */
    public void shareComment(Comment comment) {
        String authorName = comment.author.name;
        int id = comment.id;
        String content = comment.content;
        String targetUrl = getCommentTargetUrl(authorName, id);

        UMImage resImage = null;
        if (comment.images == null || comment.images.size() == 0) {
            resImage = new UMImage(mActivity, R.drawable.ic_launcher);
        } else {
            resImage = new UMImage(mActivity, comment.images.get(0).smallImage);
        }

        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        SinaShareContent sinaContent = new SinaShareContent();
        setupSinaShareContent(sinaContent, authorName, comment.content, resImage, null, targetUrl);

        WeiXinShareContent wechatContent = new WeiXinShareContent();
        setupWeChatShareContent(wechatContent, authorName, content, resImage, null, targetUrl);
        CircleShareContent circleContent = new CircleShareContent();
        setupCircleShareContent(circleContent, authorName, content, resImage, null, targetUrl);

        QQShareContent qqShareContent = new QQShareContent();
        setupQQShareContent(qqShareContent, authorName, content, resImage, null, targetUrl);
        QZoneShareContent qzoneConent = new QZoneShareContent();
        setupQzoneShareContent(qzoneConent, authorName, content, resImage, null, targetUrl);

        openShareBoard();
    }

    public void shareApp(String targetUrl, Bitmap bitmap) {
        String authorName = "和畅游";
        String content = "分享下载";

        UMImage resImage = new UMImage(mActivity, R.drawable.ic_launcher);

        WeiXinShareContent wechatContent = new WeiXinShareContent();
        setupWeChatShareContent(wechatContent, authorName, content, resImage, null, targetUrl);

        QQShareContent qqShareContent = new QQShareContent();
        setupQQShareContent(qqShareContent, authorName, content, resImage, null, targetUrl);

        SmsShareContent smsShareContent = new SmsShareContent();
        setSMSShareContent(smsShareContent, "看景点直播，用语音导航，搜吃喝玩乐，写攻略游记，尽在本土自驾游神器——和畅游，体验请点击下载http://111.44.243.117:81/indexDown.html");

        openShareBoard();
    }

    private String getCommentTargetUrl(String authorName, int id) {
        return String.format(Locale.CHINESE, COMMENT_PAGE_FORMAT, authorName, id);
    }

    private OnShareManagerOauthListener mOnShareManagerOauthListener;

    public void setOnShareManagerOauthListener(OnShareManagerOauthListener onShareManagerOauthListener) {
        mOnShareManagerOauthListener = onShareManagerOauthListener;
    }
    private OnShareManagerGetInfoListener mOnShareManagerGetInfoListener;

    public void setOnShareManagerGetInfoListener(OnShareManagerGetInfoListener onShareManagerGetInfoListener) {
        mOnShareManagerGetInfoListener = onShareManagerGetInfoListener;
    }

    public interface OnShareManagerOauthListener {
        void OnOauthSuccess(SHARE_MEDIA share_media);

        void OnOauthfaild(SHARE_MEDIA share_media);

        void OnOauthCancle(SHARE_MEDIA share_media);
    }

    public interface OnShareManagerGetInfoListener {
        void OnGetInfoSuccess(Map<String, Object> infos, SHARE_MEDIA share_media);

        void OnGetOnfifaild(SHARE_MEDIA share_media);
    }

    public void doOauthVerify(SHARE_MEDIA share_media) {

        mController.doOauthVerify(mActivity, share_media, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
//                Toast.makeText(mActivity, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA share_media) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
//                    Toast.makeText(mActivity, "授权成功.", Toast.LENGTH_SHORT).show();
                    mOnShareManagerOauthListener.OnOauthSuccess(share_media);
                    String uid = "";
                    if (share_media == SHARE_MEDIA.QQ){
                        uid = value.getString("uid");
                    }
                    getThirdInfo(share_media,uid);
                } else {
                    Toast.makeText(mActivity, "授权失败", Toast.LENGTH_SHORT).show();
                    mOnShareManagerOauthListener.OnOauthfaild(share_media);
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
//                Toast.makeText(mActivity, "授权失败", Toast.LENGTH_SHORT).show();
                android.util.Log.e("SINA", e.toString());
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
//                Toast.makeText(mActivity, "授权取消", Toast.LENGTH_SHORT).show();
                mOnShareManagerOauthListener.OnOauthCancle(share_media);
            }
        });
    }

    private void getThirdInfo(final SHARE_MEDIA share_media, final String uid) {

        mController.getPlatformInfo(mActivity, share_media, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
//                Toast.makeText(mActivity, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == 200 && info != null) {
                    StringBuilder sb = new StringBuilder();
                    Set<String> keys = info.keySet();
                    for (String key : keys) {
                        sb.append(key + "=" + info.get(key).toString() + "\r\n");
                    }
                    Log.d("TestData", sb.toString());
                    if (share_media == SHARE_MEDIA.QQ && !TextUtils.isEmpty(uid)){
                        info.put("uid",uid);
                    }
                    mOnShareManagerGetInfoListener.OnGetInfoSuccess(info,share_media);
                } else {
                    Log.d("TestData", "发生错误：" + status);
                    mOnShareManagerOauthListener.OnOauthfaild(share_media);
                }
            }
        });
    }

    /*
    取消授权
     */
    public void deleteOauth(final Context mContext,SHARE_MEDIA share_media){
        mController.deleteOauth(mContext, share_media,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        //注销登录用于清空已经获取的accesstoken信息，注销之后，下次用户需要重新输入密码完成登录过程。
                        if (status == 200) {
//                            Toast.makeText(mContext, "删除成功.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
//                            Toast.makeText(mContext, "取消授权失败",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    /**
     * 分享监听器
     */
    SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode,
                               SocializeEntity entity) {
            if (stCode == 200) {
                mOnShareManagerBackListener.shareSuccess(platform);
            } else {
                mOnShareManagerBackListener.shareFaild();
            }
        }
    };
    /**
     * 分享后的回调监听
     */
    private OnShareManagerBackListener mOnShareManagerBackListener;

    public UMSocialService getController() {
        return mController;
    }

    public interface OnShareManagerBackListener {
        void shareSuccess(SHARE_MEDIA share_media);

        void shareFaild();
    }

    public void setOnShareManagerBackListener(OnShareManagerBackListener onShareManagerBackListener) {
        this.mOnShareManagerBackListener = onShareManagerBackListener;
    }

    public void shareQQ(String title, String content, String imageUrl, String targetUrl) {

        UMImage resImage = new UMImage(mActivity, imageUrl);
        QQShareContent qqShareContent = new QQShareContent();
        setupShareContent(qqShareContent, title, content, resImage, null, targetUrl);
        mController.postShare(mActivity, SHARE_MEDIA.QQ, mShareListener);
    }

    public void shareSina(String title, String content, String imageUrl, String targetUrl) {

        UMImage resImage = new UMImage(mActivity, imageUrl);

        SinaShareContent sinaShareContent = new SinaShareContent();
//        content = content + "[url]" + targetUrl + "[/url]";
        content = content + targetUrl;
        setupShareContent(sinaShareContent, title, content, resImage, null, targetUrl);
        mController.postShare(mActivity, SHARE_MEDIA.SINA, mShareListener);
    }
    public void shareDirectSina(String title, String content, String imageUrl, String targetUrl) {

//        UMImage resImage = new UMImage(mActivity, imageUrl);

        SinaShareContent sinaShareContent = new SinaShareContent();
//        content = content + "[url]" + targetUrl + "[/url]";
        content = content + targetUrl;
        setupShareContent(sinaShareContent, title, content, null, null, targetUrl);
        mController.directShare(mActivity, SHARE_MEDIA.SINA, mShareListener);
    }

    public void shareWeChat(String title, String content, String imageUrl, String targetUrl) {

        UMImage resImage = new UMImage(mActivity, imageUrl);

        WeiXinShareContent wechatContent = new WeiXinShareContent();
        setupShareContent(wechatContent, title, content, resImage, null, targetUrl);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN, mShareListener);

    }

    public void shareWeChatCircle(String title, String content, String imageUrl, String targetUrl) {

        UMImage resImage = new UMImage(mActivity, imageUrl);

        CircleShareContent wechatContent = new CircleShareContent();
        setupShareContent(wechatContent, "和畅游推荐: " + title, content, resImage, null, targetUrl);
        mController.postShare(mActivity, SHARE_MEDIA.WEIXIN_CIRCLE, mShareListener);
    }
}
