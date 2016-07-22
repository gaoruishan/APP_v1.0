/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.qiniu.auth.Authorizer;
import com.qiniu.io.IO;
import com.qiniu.rs.CallBack;
import com.qiniu.rs.CallRet;
import com.qiniu.rs.PutExtra;
import com.qiniu.rs.UploadCallRet;
import com.qiniu.rs.UploadTaskExecutor;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SelectedImageAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Bucket;
import com.cmcc.hyapps.andyou.app.ServerAPI.Upload.Token.UploadParams;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.EmptyResponse;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.ImageList;
import com.cmcc.hyapps.andyou.model.UploadToken;
import com.cmcc.hyapps.andyou.util.AppUtils;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.ImageHelper;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostImagesActivity extends BaseActivity implements OnClickListener {
    private View mRootView;
    private PopupWindow mPopupWindow;
    private RecyclerView mRecyclerView;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static final int MAX_IMAGE_COUNT = 8;

    private Context mContext;
    private List<Image> mPendingUploadImages = new ArrayList<Image>();
    private ArrayList<Image> mSelectedImages = new ArrayList<Image>();
    private EditText mTitleText;
    private Request<UploadToken> mGetTokenRequest;
    private Request<EmptyResponse> mPostImageRequest;
    private UploadTaskExecutor mImageUploadTask;
    private CommonDialog mInProgressDialog;

    private int mScenicId = -1;
    private ImageList mImageList;
    private Uri mTakePhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);

        if (mScenicId == -1) {
            finish();
            return;
        }
        mRootView = getLayoutInflater().inflate(R.layout.activity_post_images, null);
        setContentView(mRootView);
        initViews();
    }

    private void initViews() {
        initActionBar();
        initPopupWindow();

        mTitleText = (EditText) findViewById(R.id.image_gallery_title);

        mInProgressDialog = new CommonDialog(this);
        mInProgressDialog.getLeftBtn().setVisibility(View.GONE);
        mInProgressDialog.setTitleText(R.string.dialog_title_publishing_comments);
        mInProgressDialog.setContentText(R.string.dialog_content_posting_images);
        mInProgressDialog.getRightBtn().setText(R.string.dialog_cancel_sending);
        mInProgressDialog.getDialog().setCancelable(false);
        mInProgressDialog.getDialog().setCanceledOnTouchOutside(false);
        mInProgressDialog.setOnDialogViewClickListener(new OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
                cancelRequests();
            }

            @Override
            public void onLeftButtonClick() {
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        int scap = ScreenUtils.dpToPxInt(activity, 13);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);

        mRecyclerView.setLayoutManager(layoutManager);
        SelectedImageAdapter adapter = new SelectedImageAdapter(this);
        adapter.setMaxImageLimit(MAX_IMAGE_COUNT);
        ItemClickSupport clickListener = ItemClickSupport.addTo(mRecyclerView);
        clickListener.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                if (ExcessiveClickBlocker.isExcessiveClick()) {
                    return;
                }
                SelectedImageAdapter adapter = (SelectedImageAdapter) parent.getAdapter();
                if (mSelectedImages.size() < MAX_IMAGE_COUNT
                        && position == adapter.getItemCount() - 1) {
                    ScreenUtils.dissmissKeyboard(mContext, mTitleText);
                    mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(activity, PhotoPreviewActivity.class);
                    intent.putParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA, mSelectedImages);
                    intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void cancelRequests() {
        if (mGetTokenRequest != null && !mGetTokenRequest.isCanceled()) {
            mGetTokenRequest.cancel();
        }

        mPendingUploadImages.clear();
        if (mImageUploadTask != null) {
            mImageUploadTask.cancel();
            mImageUploadTask = null;
        }

        if (mPostImageRequest != null && !mPostImageRequest.isCanceled()) {
            mPostImageRequest.cancel();
        }

        if (mInProgressDialog != null && mInProgressDialog.getDialog().isShowing()) {
            mInProgressDialog.dismissDialog();
        }
    }

    private void initPopupWindow() {
        View menu = View.inflate(activity, R.layout.menu_popup_select_image, null);
        menu.findViewById(R.id.item_popupwindow_camera).setOnClickListener(this);
        menu.findViewById(R.id.item_popupwindow_photo).setOnClickListener(this);
        menu.findViewById(R.id.item_popupwindow_cancel).setOnClickListener(this);
        mPopupWindow = new PopupWindow(menu, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPopupWindow.setContentView(menu);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_post_images);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_send_selecter);
        actionBar.getLeftView().setOnClickListener(this);
        actionBar.getRightView().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ExcessiveClickBlocker.isExcessiveClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.action_bar_right:
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    Toast.makeText(this, R.string.post_image_input_title, Toast.LENGTH_SHORT)
                            .show();
                } else if (mSelectedImages.isEmpty()) {
                    Toast.makeText(this, R.string.post_image_add_images, Toast.LENGTH_SHORT)
                            .show();
                } else if (AppUtils.getUser(activity) == null) {
                    Toast.makeText(this, R.string.error_not_login, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    mInProgressDialog.showDialog();
                    mPendingUploadImages.addAll(mSelectedImages);
                    mImageList = new ImageList();
                    mImageList.images = new ArrayList<CompoundImage>();
                    mImageList.scenicId = mScenicId;
                    mImageList.title = mTitleText.getText().toString();
                    getUploadToken();
                }
                break;
            case R.id.item_popupwindow_camera:
                mPopupWindow.dismiss();
                takePhoto();
                break;
            case R.id.item_popupwindow_photo:
                mPopupWindow.dismiss();
                Intent intent = new Intent(activity, PhotoPickActivity.class);
                intent.putExtra(Const.EXTRA_PICK_IMAGE_COUNT,
                        MAX_IMAGE_COUNT - mSelectedImages.size());
                startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
                break;
            case R.id.item_popupwindow_cancel:
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
    }

    private void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTakePhotoUri = ImageHelper.getOutputImageUri();
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
        startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK/* && (intent != null)*/) {
//                    String fileName = String.valueOf(System.currentTimeMillis());
//                    Bitmap bm = null;
//                    String path = null;
//                    Uri uri = intent.getData();
//                    if (uri != null) {
//                        if ("content".equals(uri.getScheme())) {
//                            path = getCapturePath(uri);
//                        } else {
//                            path = uri.getPath();
//                        }
//                        if (path != null) {
//                            bm = BitmapFactory.decodeFile(path);
//                        }
//
//                    } else if (intent.getExtras() != null) {
//                        bm = (Bitmap) intent.getExtras().get("data");
//                        path = FileUtils.saveBitmap(bm, fileName);
//                    }

                    if (mTakePhotoUri != null) {
                        ImageHelper.compressImageFile(mTakePhotoUri.getPath());
                        Image takenPhoto = new Image();
                        takenPhoto.imagePath = mTakePhotoUri;
                        takenPhoto.bitmap = BitmapFactory.decodeFile(mTakePhotoUri.getPath());;
                        mSelectedImages.add(takenPhoto);
                        ((SelectedImageAdapter) mRecyclerView.getAdapter()).addImage(takenPhoto);
                        MediaScannerConnection.scanFile(mContext, new String[] {
                                FileUtils.getExternalImageDir()
                            }, null, null);
                    }
                }

            case REQUEST_CODE_PICK_PHOTO: {
                if (resultCode == RESULT_OK && intent != null) {
                    ArrayList<Image> images = intent.getParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA);
                    if (images != null) {
                        mSelectedImages.addAll(images);
                        ((SelectedImageAdapter) mRecyclerView.getAdapter()).addImages(images);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private String getCapturePath(Uri uri) {
        String[] columns = {
                MediaStore.Images.Media.DATA, MediaStore.MediaColumns.DATE_ADDED
        };

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, columns, null, null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);

        cursor.close();

        return path;
    }

    private void getUploadToken() {
        UploadParams params = new UploadParams();
        params.bucket = Bucket.bucketImage;

        mGetTokenRequest = ServerAPI.Upload.Token.getUploadToken(getApplicationContext(), params,
                requestTag,
                new ServerAPI.Upload.Token.UploadTokenCallback() {

                    @Override
                    public void onTokenError() {
                        imagePostError();
                    }

                    @Override
                    public void onGetToken(String token) {
                        mGetTokenRequest = null;
                        uploadImages(token);
                    }
                });
    }

    private void uploadImages(final String uploadToken) {
        if (mPendingUploadImages.isEmpty()) {
            postImageList();
            return;
        }

        Authorizer auth = new Authorizer();
        auth.setUploadToken(uploadToken);

        String key = IO.UNDEFINED_KEY;
        PutExtra extra = new PutExtra();
        extra.params = new HashMap<String, String>();

        String path = mPendingUploadImages.remove(0).imagePath.getPath();
        String compressUrl = ImageHelper.compressImageFile(path);
         File file = null;
        if (!TextUtils.isEmpty(compressUrl)){
            file = new File(path);
        }
        Log.d("Uploading image %s", file);
        int index = mSelectedImages.size() - mPendingUploadImages.size();
        mInProgressDialog.setContentText(getString(R.string.dialog_content_posting_images_progress,
                index));
        if (!file.exists()) {
            Log.d("Image %s does not exists, ignore it", file);
            uploadImages(uploadToken);
            return;
        }

        mImageUploadTask = IO.putFile(getApplicationContext(), auth, key,
                Uri.fromFile(file), extra,
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
                            imagePostError();
                        } else {
                            String key = ret.getKey();
                            String redirect = "http://"
                                    + ServerAPI.Upload.Bucket.bucketImage.value()
                                    + ".qiniudn.com/" + key;
                            // String redirect2 = "http://" + bucketName +
                            // ".u.qiniudn.com/" + key;
                            CompoundImage compoundImage = new CompoundImage();
                            compoundImage.largeImage = redirect;
                            compoundImage.smallImage = ServerAPI.Upload
                                    .buildThumbnailPath(redirect);
                            mImageList.images.add(compoundImage);
                            uploadImages(uploadToken);
                          //  Log.d("Image %s uploaded to %s", file, compoundImage);
                        }
                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        imagePostError();
                    }
                });
    }

    private void postImageList() {
        Gson gson = new Gson();
        String jsonBody = gson.toJson(mImageList);
        Log.d("posting images, mImageList=%s", jsonBody);
        mInProgressDialog.setContentText(R.string.dialog_content_posting_images);

        mPostImageRequest = RequestManager.getInstance().sendGsonRequest(Method.POST,
                ServerAPI.ImageUpload.URL,
                jsonBody,
                EmptyResponse.class, new Response.Listener<EmptyResponse>() {
                    @Override
                    public void onResponse(EmptyResponse result) {
                        imagePostSuccess();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error post new image list, error=%s", error);
                        imagePostError();
                    }
                }, requestTag);
    }

    private void imagePostSuccess() {
        mInProgressDialog.dismissDialog();
        Toast.makeText(this, R.string.image_post_success, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void imagePostError() {
        mInProgressDialog.dismissDialog();
        Toast.makeText(this, R.string.image_post_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        cancelRequests();
        super.onDestroy();
    }
}
