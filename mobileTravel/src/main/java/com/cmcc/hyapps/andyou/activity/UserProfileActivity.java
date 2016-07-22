/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.cmcc.hyapps.andyou.model.CityModel;
import com.cmcc.hyapps.andyou.model.DistrictModel;
import com.cmcc.hyapps.andyou.model.ProvinceModel;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.cmcc.hyapps.andyou.widget.wheel.OnWheelChangedListener;
import com.cmcc.hyapps.andyou.widget.wheel.WheelView;
import com.cmcc.hyapps.andyou.widget.wheeladapter.ArrayWheelAdapter;
import com.cmcc.hyapps.andyou.widget.wheeladapter.XmlParserHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.littlec.sdk.utils.CommonUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.auth.Authorizer;
import com.qiniu.io.IO;
import com.qiniu.rs.CallBack;
import com.qiniu.rs.CallRet;
import com.qiniu.rs.PutExtra;
import com.qiniu.rs.UploadCallRet;
import com.qiniu.rs.UploadTaskExecutor;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Bucket;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Token.UploadParams;
import com.cmcc.hyapps.andyou.data.ImageLoaderManager;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.data.StringConverter;
import com.cmcc.hyapps.andyou.model.EmptyResponse;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.QHUser;
import com.cmcc.hyapps.andyou.model.UploadToken;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ConstTools;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import test.grs.com.ims.message.IMConst;

/**
 * @author kuloud
 */
public class UserProfileActivity extends BaseActivity implements OnClickListener, UploadTask.UploadCallBack {

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";
    private final int REQUEST_CODE_NAME = 1;
    private final int REQUEST_CODE_TAKE_PHOTO = 2;
    private final int REQUEST_CODE_PICK_PHOTO = 3;
    private final int REQUEST_CODE_CUT_PHOTO = 4;
    private final int REQUEST_CODE_INTRODUCE = 5;

    // Cache avata in temp dir, when avata updated, make it effect avata
    public static final String PATH_NAME_AVATAR_TEMP = "avatar_temp";

    private Context mContext;
    private QHUser mUser;
    // Store the previous avatar url.
    private String mPreviousAvatarUrl;

    private ViewGroup mRootContainer;
    private PopupWindow mPickPhotoWindow, pickSexyWindow, pickAddressWindows;
    private NetworkImageView mAvatar;
    private TextView mName;
    private TextView sexy, address, introduce;
    private View introduceView;
    private UploadTaskExecutor mImageUploadTask;
    private Request<UploadToken> mGetTokenRequest;

    private String[] sexyStrings = {"男", "女"};
    private String currentSexy = "男";
    // TextView tv_regist;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_user_profile);
        initProvinceDatas();
        mRootContainer = (ViewGroup) findViewById(R.id.root_container);
        initActionBar();
        initListItems();
        mUser = getIntent().getParcelableExtra("user");
        if (mUser == null) {
            finish();
        } else {
            url = ServerAPI.User.buildUpdateUserInfoUrl(mUser.id);
            bindUserInfo(mUser);
        }
    }

    private void bindUserInfo(QHUser user) {
        boolean localLoaded = false;
        if (user.user_info == null) {
            return;
        }
        if (user.user_info.avatar_url != null && FileUtils.fileCached(activity, user.user_info.avatar_url)) {
            String url = FileUtils.getCachePath(activity, user.user_info.avatar_url);
            Bitmap bm = FileUtils.getLocalBitmap(url);
            if (bm != null) {
                mAvatar.setImageBitmap(bm);
                localLoaded = true;
            }
        }

        if (!localLoaded) {
            ImageUtil.DisplayImage(user.user_info.avatar_url, mAvatar,
                    R.drawable.bg_avata_hint, R.drawable.bg_avata_hint);
        }

        mName.setText(user.user_info.nickname);
        introduce.setText(user.user_info.getIntroduction());
        if (user.user_info.gender == 0){
            sexy.setText("男");
        }else
            sexy.setText("女");
        address.setText(user.user_info.getLocation_provnice() + " " + user.user_info.getLocation_area());
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_user_profile);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO){
                if (cameraFile != null) {
                    //file:///mnt/sdcard/image.png
//                            onAvataUrlLoaded("file:///mnt/sdcard/selftravel/avatar_temp.JEPG");
                    startPhotoZoom(BitmapFactory.decodeFile(cameraFile.getPath()),150);
//                        Uri uri = data.getData();
//                        if (uri != null) {
//                            onAvataUrlLoaded(uri.getPath());
//                        } else
//                        if (data.getExtras() != null) {
//                            Bitmap bm = (Bitmap) data.getExtras().get("data");
//                            startPhotoZoom(bm, 200);
//                        }
                    MediaScannerConnection.scanFile(mContext, new String[]{
                            FileUtils.getExternalImageDir()
                    }, null, null);
                }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_NAME:
                    String name = data.getStringExtra(TextFieldEditActivity.KEY_TEXT);
                    /*Map<String, String> params = new HashMap<String, String>();
                    String name = data.getStringExtra(TextFieldEditActivity.KEY_TEXT);
                    params.put(ServerAPI.User.PARAM_NAME, name);
                    mUser.user_info.nickname = name;
                    updateUserInfo(params);*/

//                    List<File> imgFiles = new ArrayList<File>();
//                    QHUser usr = AppUtils.getQHUser(getApplicationContext());
//                    String url = ServerAPI.User.buildUpdateUserInfoUrl(usr.id);
                    mUser.user_info.nickname = name;
                    new UploadTask(getApplicationContext(), url, 1, null, mUser, this).execute("", "");
                    break;

                case REQUEST_CODE_PICK_PHOTO: {
                    if (resultCode == RESULT_OK && data != null) {
                        ArrayList<Image> images = data
                                .getParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA);
                        if (images != null && !images.isEmpty()) {
                            if (images.get(0).bitmap != null) {
                                startPhotoZoom(images.get(0).bitmap, 200);
                            } else {
                                String path = images.get(0).imagePath.toString();
                                onAvataUrlLoaded(path);
                            }
                        }
                    }
                    break;
                }
                case REQUEST_CODE_CUT_PHOTO: {
                    if (data != null && data.getExtras() != null) {
                        Bitmap photo = data.getExtras().getParcelable("data");
                        onAvataLoaded(photo);
                    }
                    break;
                }
                case REQUEST_CODE_INTRODUCE: {
                    if (data == null)
                        return;
                    String introduce = data.getStringExtra(TextFieldEditActivity.KEY_TEXT);
                    mUser.user_info.setIntroduction(introduce);
                    new UploadTask(getApplicationContext(), url, 1, null, mUser, this).execute("", "");
                    break;
                }
                default:
                    break;
            }
        }
    }
    File cameraFile;
    public void doTakePicture() {
        if (!CommonUtils.isExitsSdcard()) {
            Toast.makeText(getApplicationContext(), "SD卡不存在，不能拍照", Toast.LENGTH_LONG).show();
            return;
        }

        cameraFile = new File(Environment.getExternalStorageDirectory().getPath() + "/selftravel/"+System.currentTimeMillis()+".JEPG");
        android.util.Log.e("PATH = " ,cameraFile.getPath());
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_TAKE_PHOTO);
    }

    private void onAvataUrlLoaded(String url) {
        ImageLoaderManager.getInstance().getLoader()
                .loadImage(url, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        onUploadError();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        startPhotoZoom(loadedImage, 150);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        onUploadError();
                    }
                });
    }

    private void onAvataLoaded(Bitmap bp) {
        try {
            if (bp != null) {
                FileUtils.cacheBitmap(mContext, bp, PATH_NAME_AVATAR_TEMP);
                List<File> imgFiles = new ArrayList<File>();
                File file = ConstTools.saveBitmap(bp, "user_icon", getApplicationContext());

               /* String path = bp.getp();
                ImageHelper.compressImageFile(path);
                File file = new File(path);*/
                imgFiles.add(file);

                QHUser usr = AppUtils.getQHUser(getApplicationContext());
                String url = ServerAPI.User.buildUpdateUserInfoUrl(usr.id);
//                new UploadTask(getApplicationContext(), url, 1, imgFiles, usr.user_info.nickname, 1, this).execute("", "");
                new UploadTask(getApplicationContext(), url, 1, imgFiles, mUser,this).execute("", "");

//             getUploadToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
            onUploadError();
        }
    }

    private void updateUserInfo(final Map<String, String> params) {
        RequestManager.getInstance().sendGsonRequest(Method.POST, ServerAPI.User.buildSelfUrl(),
                EmptyResponse.class,
                null, new Response.Listener<EmptyResponse>() {
                    @Override
                    public void onResponse(EmptyResponse response) {
                        Log.e("=====onResponse");
                        String url = FileUtils.getCachePath(activity, PATH_NAME_AVATAR_TEMP);
                        Bitmap bm = FileUtils.getLocalBitmap(url);
                        FileUtils.cacheBitmap(mContext, bm, mUser.user_info.avatar_url);
                        // Delete temp avatar
                        FileUtils.delFile(url);
                        // Delete previous avatar
                        if (!TextUtils.isEmpty(mPreviousAvatarUrl)) {
                            FileUtils.delFile(FileUtils.getCachePath(activity, mPreviousAvatarUrl));
                        }
                        AppUtils.saveUser(activity, mUser);
                        bindUserInfo(mUser);
                        if (params.containsKey(ServerAPI.User.PARAM_NAME)) {
                            ToastUtils.show(mContext, R.string.msg_user_name_change_success);
                        } else if (params.containsKey(ServerAPI.User.PARAM_AVATAR_URL)) {
                            ToastUtils.show(mContext, R.string.msg_user_avata_change_success);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("=====onResponse" + error, "onErrorResponse");
                        if (params.containsKey(ServerAPI.User.PARAM_NAME)) {
                            ToastUtils.show(mContext, R.string.msg_user_name_change_failed);
                        } else if (params.containsKey(ServerAPI.User.PARAM_AVATAR_URL)) {
                            ToastUtils.show(mContext, R.string.msg_user_avata_change_failed);
                        }
                    }
                }, false, params, "user_self");
    }

    private void initListItems() {
        setItem(R.id.item_avatar, R.string.me_item_avatar, true);
        setItem(R.id.item_name, R.string.me_item_nickname, true);
        setItem(R.id.item_sexy, R.string.me_item_sexy, true);
        setItem(R.id.item_address, R.string.me_item_address, true);
       // setItem(R.id.item_introduce, R.string.me_item_introduce, true);
        mAvatar = (NetworkImageView) findViewById(R.id.item_avatar).findViewById(R.id.item_image);
        mAvatar.setVisibility(View.VISIBLE);
        mName = (TextView) findViewById(R.id.item_name).findViewById(R.id.item_summary);
        sexy = (TextView) findViewById(R.id.item_sexy).findViewById(R.id.item_summary);
        address = (TextView) findViewById(R.id.item_address).findViewById(R.id.item_summary);
        introduce = (TextView) findViewById(R.id.item_introduce_textView);
        introduceView = findViewById(R.id.item_introduce);
        introduceView.setOnClickListener(this);
    }

    private void setItem(int id, int textId, boolean enable) {
        View item = findViewById(id);
        if (enable)
            item.setOnClickListener(this);
        TextView text = (TextView) item.findViewById(R.id.item_text);
        text.setText(textId);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.item_avatar:
                popupPickPhotoMenu();
                break;
            case R.id.item_address:
                popupPickAddressMenu();
                break;
            case R.id.item_name:
                Intent name = new Intent(activity, TextFieldEditActivity.class);
                name.putExtra(TextFieldEditActivity.KEY_TITLE, getString(R.string.me_item_name));
                name.putExtra(TextFieldEditActivity.KEY_TEXT, mName.getText());
                startActivityForResult(name, REQUEST_CODE_NAME);
                break;
            case R.id.item_sexy:
                popupPickSexyMenu();
                break;
            case R.id.item_popupwindow_camera:
                mPickPhotoWindow.dismiss();
//                takePhoto();
                doTakePicture();
                break;
            case R.id.item_popupwindow_photo:
                mPickPhotoWindow.dismiss();
                Intent intent = new Intent(activity, PhotoPickActivity.class);
                intent.putExtra(Const.EXTRA_PICK_IMAGE_COUNT, 1);
                startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
                break;
            case R.id.item_popupwindow_cancel:
                mPickPhotoWindow.dismiss();
                break;
            case R.id.item_introduce:
                Intent introduceIntent = new Intent(activity, TextFieldIntroduceActivity.class);
                introduceIntent.putExtra(TextFieldEditActivity.KEY_TITLE, getString(R.string.me_item_introduce));
                introduceIntent.putExtra(TextFieldEditActivity.KEY_TEXT, introduce.getText());
                startActivityForResult(introduceIntent, REQUEST_CODE_INTRODUCE);
                break;
            default:
                break;
        }
    }

    private void popupPickPhotoMenu() {
        if (mPickPhotoWindow == null) {
            View menu = View.inflate(activity, R.layout.menu_popup_select_image, null);
            menu.findViewById(R.id.item_popupwindow_camera).setOnClickListener(this);
            menu.findViewById(R.id.item_popupwindow_photo).setOnClickListener(this);
            menu.findViewById(R.id.item_popupwindow_cancel).setOnClickListener(this);
            mPickPhotoWindow = new PopupWindow(menu, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mPickPhotoWindow.setContentView(menu);
        }
        mPickPhotoWindow.showAtLocation(mRootContainer, Gravity.BOTTOM, 0, 0);
    }

    private WheelView proviceWheelView, cityWheelView;

    private void popupPickAddressMenu() {
        if (pickAddressWindows == null) {
            View view = View.inflate(activity, R.layout.pick_address_windows, null);
            proviceWheelView = (WheelView) view.findViewById(R.id.pick_provice);
            proviceWheelView.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
            proviceWheelView.setVisibleItems(7);

            proviceWheelView.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    updateCities();
                }
            });

            cityWheelView = (WheelView) view.findViewById(R.id.pick_city);
          //  cityWheelView.setViewAdapter(new ArrayWheelAdapter<String>(this, sexyStrings));
            cityWheelView.setVisibleItems(7);
            cityWheelView.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
//                  mCurrentCityName = cities[wheel.getCurrentItem()];
                }
            });
            updateCities();
            Button okButton = (Button) view.findViewById(R.id.pick_address_confirm);
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setText(mCurrentProviceName + " " + cities[cityWheelView.getCurrentItem()]);
                    mUser.user_info.setLocation_provnice(mCurrentProviceName);
                    mUser.user_info.setLocation_area(cities[cityWheelView.getCurrentItem()]);
                    new UploadTask(UserProfileActivity.this.getApplicationContext(), url, 1, null, mUser, UserProfileActivity.this).execute("", "");

                    if (pickAddressWindows != null)
                        pickAddressWindows.dismiss();
                }
            });
            Button cancleButton = (Button) view.findViewById(R.id.pick_address_cancle);
            cancleButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pickAddressWindows != null)
                        pickAddressWindows.dismiss();
                }
            });
            pickAddressWindows = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        pickAddressWindows.showAtLocation(mRootContainer, Gravity.CENTER, 0, 0);
    }

    private void popupPickSexyMenu() {
        if (pickSexyWindow == null) {
            View view = View.inflate(activity, R.layout.pick_sexy_windows, null);
            WheelView wheelView = (WheelView) view.findViewById(R.id.pick_sexy_wheel);
            wheelView.setViewAdapter(new ArrayWheelAdapter<String>(this, sexyStrings));
            wheelView.setVisibleItems(2);
            wheelView.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    currentSexy = sexyStrings[wheel.getCurrentItem()];

                }
            });
            Button okButton = (Button) view.findViewById(R.id.pick_sexy_confirm);
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(currentSexy)) {
                        sexy.setText(currentSexy);
                        if (currentSexy.equals("男"))
                        mUser.user_info.gender = 0;
                        else
                            mUser.user_info.gender = 1;
                        new UploadTask(UserProfileActivity.this.getApplicationContext(), url, 1, null, mUser, UserProfileActivity.this).execute("", "");
                    }
                    if (pickSexyWindow != null)
                        pickSexyWindow.dismiss();
                }
            });
            Button cancleButton = (Button) view.findViewById(R.id.pick_sexy_cancle);
            cancleButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pickSexyWindow != null)
                        pickSexyWindow.dismiss();
                }
            });
            pickSexyWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
        }
        pickSexyWindow.showAtLocation(mRootContainer, Gravity.CENTER, 0, 0);
    }

    private void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void getUploadToken() {
        UploadParams params = new UploadParams();
        params.bucket = Bucket.bucketImage;

        mGetTokenRequest = ServerAPI.Upload.Token.getUploadToken(getApplicationContext(), params,
                requestTag,
                new ServerAPI.Upload.Token.UploadTokenCallback() {

                    @Override
                    public void onTokenError() {
                        onUploadError();
                    }

                    @Override
                    public void onGetToken(String token) {
                        mGetTokenRequest = null;
                        uploadImages(token);
                    }
                });
    }

    private void uploadImages(final String uploadToken) {
        Authorizer auth = new Authorizer();
        auth.setUploadToken(uploadToken);

        String key = IO.UNDEFINED_KEY;
        PutExtra extra = new PutExtra();
        extra.params = new HashMap<String, String>();

        final String imagePath = FileUtils.getCachePath(activity, PATH_NAME_AVATAR_TEMP);
        Log.d("Uploading image %s", imagePath);
        mImageUploadTask = IO.putFile(getApplicationContext(), auth, key,
                Uri.fromFile(new File(imagePath)), extra,
                new CallBack() {

                    @Override
                    public void onProcess(long current, long total) {
                        int percent = (int) (current * 100 / total);
                        Log.d("Upload percent %d", percent);
                    }

                    @Override
                    public void onSuccess(UploadCallRet ret) {
                        if (ret.getException() != null) {
                            Log.e(ret.getException(), "Upload error");
                            onUploadError();
                        } else {
                            String key = ret.getKey();
                            String redirect = "http://"
                                    + ServerAPI.Upload.Bucket.bucketImage.value()
                                    + ".qiniudn.com/" + key;
                            mPreviousAvatarUrl = mUser.user_info.avatar_url;
                            mUser.user_info.avatar_url = ServerAPI.Upload.buildThumbnailPath(redirect);
                            Log.d("Image %s uploaded to %s", imagePath, redirect);

                            Map<String, String> params = new HashMap<String, String>();
                            params.put(ServerAPI.User.PARAM_AVATAR_URL, mUser.user_info.avatar_url);
                            updateUserInfo(params);
                        }
                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        onUploadError();
                    }
                });
    }

    private void onUploadError() {
        ToastUtils.show(mContext, R.string.error_avata_update_failed);
    }

    private void startPhotoZoom(Bitmap bp, int size) {
        FileUtils.cacheBitmap(mContext, bp, PATH_NAME_AVATAR_TEMP);
        String url = FileUtils.getCachePath(activity, PATH_NAME_AVATAR_TEMP);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_CODE_CUT_PHOTO);
    }

    private void startPhotoZoomRotate(Bitmap bp, int size) {
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
         bp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight(), matrix, true);
        FileUtils.cacheBitmap(mContext, bp, PATH_NAME_AVATAR_TEMP);
        String url = FileUtils.getCachePath(activity, PATH_NAME_AVATAR_TEMP);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        intent.putExtra("crop", "true");

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_CODE_CUT_PHOTO);
    }

    @Override
    public void onSuccess(String result) {

        Gson mGson = new Gson();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        QHUser user = mGson.fromJson(result.toString(), QHUser.class);
        if (user.getResult() == 1){
            ToastUtils.show(mContext, R.string.name_again);
            return;
        }
        if (user.user_info != null) {
            if (TextUtils.isEmpty(user.user_info.avatar_url))
                user.user_info.avatar_url = mUser.user_info.avatar_url;
            else
                mUser.user_info.avatar_url = user.user_info.avatar_url;

//            if (!TextUtils.isEmpty(user.user_info.nickname))
//                mUser.user_info.nickname = user.user_info.nickname;
        }

        AppUtils.saveUser(activity, mUser);
        bindUserInfo(mUser);

        ToastUtils.show(mContext, R.string.change_user_information_success);
    }

    @Override
    public void onFailed() {
        ToastUtils.show(mContext, R.string.change_user_information_fail);
    }

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
    /**
     * 根据当前的市，更新区WheelView的信息,暂无用到
     */
//    private void updateAreas() {
//        int pCurrent = mViewCity.getCurrentItem();
//        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
//        String[] areas = mDistrictDatasMap.get(mCurrentCityName);
//
//        if (areas == null) {
//            areas = new String[] { "" };
//        }
//        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
//        mViewDistrict.setCurrentItem(0);
//    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private String[] cities;
    private void updateCities() {
        int pCurrent = proviceWheelView.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        cities = null;
        cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        cityWheelView.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        cityWheelView.setCurrentItem(0);
        // updateAreas();
    }

}
