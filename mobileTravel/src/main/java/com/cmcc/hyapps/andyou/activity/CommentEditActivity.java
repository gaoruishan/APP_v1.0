/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemSubViewClickListener;
import com.qiniu.rs.UploadTaskExecutor;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SelectedImageAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.app.UserManager;
import com.cmcc.hyapps.andyou.data.StringConverter;
import com.cmcc.hyapps.andyou.model.Comment;
import com.cmcc.hyapps.andyou.model.Comment.NewCommentResult;
import com.cmcc.hyapps.andyou.model.CommentImage;
import com.cmcc.hyapps.andyou.model.CompoundImage;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.UploadToken;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.ImageHelper;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.BadgeView;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.CommonDialog.OnDialogViewClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class CommentEditActivity extends BaseActivity implements OnClickListener ,UploadTask.UploadCallBack {
    private View mRootView;
    private PopupWindow mPopupWindow;
    private RecyclerView mRecyclerView;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private static final int MAX_IMAGE_COUNT = 9;

    private Context mContext;
    private List<Image> mPendingUploadImages = new ArrayList<Image>();
    private List<File> imgFiles = new ArrayList<File>();
    private ArrayList<Image> mSelectedImages = new ArrayList<Image>();
    private RatingBar mRatingBar;
    private EditText mCommentText;
    private Request<UploadToken> mGetTokenRequest;
    private Request<NewCommentResult> mPostCommentRequest;
    private UploadTaskExecutor mImageUploadTask;
    private CommonDialog mInProgressDialog;

    private int mScenicId = -1;
    private Comment mComment;
    private Uri mTakePhotoUri;
    private int ctype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mScenicId = getIntent().getIntExtra(Const.EXTRA_ID, -1);

        ctype = getIntent().getIntExtra(Const.MARKET_HOTEL,0);
        if (mScenicId == -1) {
            finish();
            return;
        }
        setContentView(R.layout.activity_comment_edit);
        mRootView = findViewById(R.id.root_container);
        initViews();
    }

    private void initViews() {
        initActionBar();
        initPopupWindow();

        mRatingBar = (RatingBar) findViewById(R.id.comment_rating);
        mCommentText = (EditText) findViewById(R.id.comment_text);

        mInProgressDialog = new CommonDialog(this);
        mInProgressDialog.getLeftBtn().setVisibility(View.GONE);
        mInProgressDialog.setTitleText(R.string.dialog_title_publishing_comments);
        mInProgressDialog.setContentText(R.string.dialog_content_publishing_comments);
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
        int gap = ScreenUtils.dpToPxInt(getApplicationContext(), 13);
        mRecyclerView.setPadding(gap / 2, gap, gap / 2, 0);
        DividerItemDecoration decor = new DividerItemDecoration(gap, gap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setLayoutManager(layoutManager);

        final SelectedImageAdapter adpater = new SelectedImageAdapter(this);
        adpater.setMaxImageLimit(MAX_IMAGE_COUNT);
        ItemClickSupport clickListener = ItemClickSupport.addTo(mRecyclerView);
        clickListener.setOnItemSubViewClickListener(new OnItemSubViewClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (view instanceof BadgeView) {
                    if (adpater.getDataItems() != null && adpater.getDataItems().size() > position) {
                        adpater.getDataItems().remove(position);
                        mSelectedImages.remove(position);

                        adpater.notifyDataSetChanged();
                    }
                } else if (view instanceof ImageView) {
                    if (mSelectedImages.size() < MAX_IMAGE_COUNT
                            && position == adpater.getItemCount() - 1) {
                        ScreenUtils.dissmissKeyboard(mContext, mCommentText);
                        mPopupWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
                    } else {
                        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
                        intent.putParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA, mSelectedImages);
                        intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                        startActivity(intent);
                    }
                }
            }
        });
        mRecyclerView.setAdapter(adpater);
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

        if (mPostCommentRequest != null && !mPostCommentRequest.isCanceled()) {
            mPostCommentRequest.cancel();
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
        actionBar.setTitle(R.string.action_bar_title_comment_edit);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
     //   actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_send_selecter);
        actionBar.getRightTextView().setText("发送");
        actionBar.getRightTextView().setVisibility(View.VISIBLE);
        actionBar.getLeftView().setOnClickListener(this);
        actionBar.getRightTextView().setOnClickListener(this);
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
            case R.id.action_bar_right_text:
                if (!NetUtils.isNetworkAvailable(this)) {
                    ToastUtils.AvoidRepeatToastShow(this, R.string.network_unavailable, Toast.LENGTH_LONG);
                    return;
                }
                if (TextUtils.isEmpty(mCommentText.getText().toString().trim())) {
                    Toast.makeText(this, R.string.comment_edit_input_content, Toast.LENGTH_SHORT)
                            .show();
                } else {

                    if (!UserManager.makeSureLogin(CommentEditActivity.this, REQUEST_CODE_LOGIN_NEW_COMMENT)) {
                        postcomment();
                    }

//                    if (mPendingUploadImages.isEmpty()) {
//                        postComment();
//                    } else {
//                        getUploadToken();
//                    }
                }
                break;
            case R.id.item_popupwindow_camera:
                mPopupWindow.dismiss();
                takePhoto();
                break;
            case R.id.item_popupwindow_photo:
                mPopupWindow.dismiss();
                Intent intent = new Intent(activity, PhotoPickActivity.class);
                intent.putExtra(Const.EXTRA_PICK_IMAGE_COUNT,MAX_IMAGE_COUNT - mSelectedImages.size());
                intent.putExtra(Const.EXTRA_IMAGE_DATA, mSelectedImages);
                startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
                break;
            case R.id.item_popupwindow_cancel:
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
    }


    private void postcomment(){

        mComment = new Comment();
        mComment.objectId = mScenicId;
        //market argument
        if (ctype == 5){
            mComment.type = ServerAPI.Comments.Type.TRIP.value();
            mComment.ctype = Integer.parseInt(ServerAPI.Comments.Type.TRIP.value());
        }else {
            mComment.type = ServerAPI.Comments.Type.SCENIC.value();
            mComment.ctype = Integer.parseInt(ServerAPI.Comments.Type.SCENIC.value());
        }
        // 1. trim white space 2. replace multiple line breaks into
        mComment.content = mCommentText.getText().toString().trim().replaceAll("\n+", "\n");
        mComment.rating = mRatingBar.getRating();
        mComment.allowReply = true;
        mComment.allowVote = true;
        mComment.images = new ArrayList<CompoundImage>();
        mComment.comment_images = new ArrayList<CommentImage>();
        mPendingUploadImages.clear();
        mPendingUploadImages.addAll(mSelectedImages);
        for(int i = 0;i<mSelectedImages.size();i++) {
            String path = mSelectedImages.get(i).imagePath.getPath();
            String compressUrl = null;
            try {
                compressUrl = ImageHelper.compressImageFile(path);
            } catch (Exception e) {
                Toast.makeText(CommentEditActivity.this, "第" + (i + 1) + "张图片加载失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(compressUrl)){
                File file = new File(compressUrl);
                imgFiles.add(file);
            }
        }
        mInProgressDialog.showDialog();

        new UploadTask(CommentEditActivity.this, imgFiles , mComment.content, mComment.objectId, mComment.ctype ,this).execute("", "");
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
//                        takenPhoto.bitmap = BitmapFactory.decodeFile(mTakePhotoUri.getPath());
                        mSelectedImages.add(takenPhoto);
                        ((SelectedImageAdapter) mRecyclerView.getAdapter()).addImage(takenPhoto);
                        MediaScannerConnection.scanFile(mContext, new String[] {FileUtils.getExternalImageDir()}, null, null);
                    }
                }

            case REQUEST_CODE_PICK_PHOTO: {
                if (resultCode == RESULT_OK && intent != null) {
                    ArrayList<Image> images = intent
                            .getParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA);
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

  /*  private void getUploadToken() {
        UploadParams params = new UploadParams();
        params.bucket = Bucket.bucketImage;

        mGetTokenRequest = ServerAPI.Upload.Token.getUploadToken(getApplicationContext(), params,
                requestTag,
                new ServerAPI.Upload.Token.UploadTokenCallback() {

                    @Override
                    public void onTokenError() {
                        commentPostError();
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
            postComment();
            return;
        }

        Authorizer auth = new Authorizer();
        auth.setUploadToken(uploadToken);

        String key = IO.UNDEFINED_KEY;
        PutExtra extra = new PutExtra();
        extra.params = new HashMap<String, String>();

        String path = mPendingUploadImages.remove(0).imagePath.getPath();
        ImageHelper.compressImageFile(path);

        final File file = new File(path);
//        imgFiles.add(file);
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
                            commentPostError();
                        } else {
                            String key = ret.getKey();
                            String redirect = "http://" + ServerAPI.Upload.Bucket.bucketImage.value() + ".qiniudn.com/" + key;
                            // String redirect2 = "http://" + bucketName + // ".u.qiniudn.com/" + key;
                            CompoundImage compoundImage = new CompoundImage();
                            compoundImage.largeImage = redirect;
                            compoundImage.smallImage = ServerAPI.Upload.buildThumbnailPath(redirect);
                            mComment.images.add(compoundImage);

                            CommentImage comment_images = new CommentImage();
                            comment_images.image_url = redirect;
                            comment_images.large_url = redirect;
                            comment_images.small_url = ServerAPI.Upload.buildThumbnailPath(redirect);
                            mComment.comment_images.add(comment_images);

                            uploadImages(uploadToken);
                            Log.d("Image %s uploaded to %s", file, compoundImage);
                        }
                    }

                    @Override
                    public void onFailure(CallRet ret) {
                        Log.e("Error uploading image:%s", ret.getResponse());
                        commentPostError();
                    }
                });
    }

    private void postComment() {
        String url =ServerAPI.User.buildWriteCommentUrl()*//*buildWriteCommentUrl(mScenicId,1)*//*;
        Gson gson = new Gson();
        String jsonBody = gson.toJson(mComment);
        Log.d("posting comment, mComment=%s", jsonBody);
        mInProgressDialog.setContentText(R.string.dialog_content_publishing_comments);
        mPostCommentRequest = RequestManager.getInstance().sendGsonRequest(Method.POST, url,
                jsonBody,
                NewCommentResult.class, new Response.Listener<NewCommentResult>() {
                    @Override
                    public void onResponse(NewCommentResult result) {
                        Log.d("New comment posted, id=: %d", result.commentId);
                        if (result.commentId > 0) {
                            mComment.id = result.commentId;
                            mComment.author = Author.fromUser(AppUtils
                                    .getUser(getApplicationContext()));
                            // TODO this is not that accurate
                            mComment.createTime = TimeUtils.formatTime(System
                                    .currentTimeMillis(), TimeUtils.DATE_TIME_FORMAT);
                            commentPostSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error post new comment, error=%s", error);
                        commentPostError();
                    }
                }, requestTag);
    }*/

    private void commentPostSuccess() {
        mInProgressDialog.dismissDialog();
        Toast.makeText(this, R.string.comment_post_sucess, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra(Const.EXTRA_COMMENT_DATA, mComment);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void commentPostError() {
        mInProgressDialog.dismissDialog();
        // TODO
        Toast.makeText(this, R.string.comment_post_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        cancelRequests();
        super.onDestroy();
    }



    @Override
    public void onSuccess(String result) {
        Gson mGson = new Gson();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        mGson = gb.create();
        mComment =  mGson.fromJson(result.toString(), Comment.class);
        commentPostSuccess();
    }
    @Override
    public void onFailed() {

    }
}
