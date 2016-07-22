package com.cmcc.hyapps.andyou.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.SelectedImageAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.QHFriendInfo;
import com.cmcc.hyapps.andyou.support.ExEditText;
import com.cmcc.hyapps.andyou.upyun.UploadTask;
import com.cmcc.hyapps.andyou.util.FileUtils;
import com.cmcc.hyapps.andyou.util.ImageHelper;
import com.cmcc.hyapps.andyou.util.NetUtils;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.util.ToastUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.BadgeView;
import com.cmcc.hyapps.andyou.widget.CommonDialog;
import com.cmcc.hyapps.andyou.widget.FriendsRecycleView;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2015/10/26.
 */
public class FriendsCircleSendTrendActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, UploadTask.UploadCallBack {
    private ExEditText mEditText;
    private RecyclerView mRecyclerView;
    private View locationView, mRootView;
    private ToggleButton location_toggleButton, private_toggleButton;
    private ActionBar mActionBar;
    private SelectedImageAdapter mSelectedImageAdapter;
    private static final int MAX_IMAGE_COUNT = 9;
    private ArrayList<Image> mSelectedImages = new ArrayList<Image>();
    private Context mContext;
    private PopupWindow mPopupWindow;
    private Uri mTakePhotoUri;
    private LinearLayout.LayoutParams mLayoutParams;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_PHOTO = 2;
    private CommonDialog mInProgressDialog;
    private boolean isLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_friends_circle_send_trends);
        initView();
        initActionBar();
        initPopupWindow();
        initRecyclerView();
    }

    private void initDialog() {
        mInProgressDialog = new CommonDialog(this);
        mInProgressDialog.getLeftBtn().setVisibility(View.GONE);
        mInProgressDialog.setTitleText(R.string.dialog_title_publishing_comments);
        mInProgressDialog.setContentText(R.string.dialog_content_publishing_comments);
        mInProgressDialog.getRightBtn().setText(R.string.dialog_cancel_sending);
        mInProgressDialog.getDialog().setCancelable(false);
        mInProgressDialog.getDialog().setCanceledOnTouchOutside(false);
        mInProgressDialog.setOnDialogViewClickListener(new CommonDialog.OnDialogViewClickListener() {

            @Override
            public void onRightButtonClick() {
                cancelRequests();
            }

            @Override
            public void onLeftButtonClick() {
            }
        });
    }

    private void cancelRequests() {
        if (mInProgressDialog != null && mInProgressDialog.getDialog().isShowing()) {
            mInProgressDialog.dismissDialog();
        }
    }

    private String mText;
    private void initView() {
        mRootView = this.findViewById(R.id.friends_circle_send_trends_main);
        mEditText = (ExEditText) this.findViewById(R.id.friends_circle_send_trends_edittext);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mText = s.toString().trim();
                if (mText.length() >= 500) {
                    ToastUtils.show(FriendsCircleSendTrendActivity.this, R.string.count_limited);
                    mEditText.setText(mText.substring(0, 499));
                    mEditText.setSelection(mEditText.getText().length());
                }
            }
        });
        locationView = this.findViewById(R.id.friends_circle_send_trends_location);
        locationView.setOnClickListener(this);
        location_toggleButton = (ToggleButton) this.findViewById(R.id.friends_circle_send_trends_location_toggleButton);
        private_toggleButton = (ToggleButton) this.findViewById(R.id.friends_circle_send_trends_private_toggleButton);

        location_toggleButton.setOnCheckedChangeListener(this);
        private_toggleButton.setOnCheckedChangeListener(this);
        location_toggleButton.setChecked(false);
        private_toggleButton.setChecked(true);
        initDialog();
    }

    private void initActionBar() {
        mActionBar = (ActionBar) findViewById(R.id.action_bar);
        mActionBar.setTitle("动态发布");
        mActionBar.getTitleView().setTextColor(Color.WHITE);
        mActionBar.setBackgroundResource(R.color.title_bg);
        mActionBar.getLeftView().setImageResource(R.drawable.return_back);
        mActionBar.getLeftView().setOnClickListener(this);
        mActionBar.getRightTextView().setText("发送");
        mActionBar.getRightTextView().setVisibility(View.VISIBLE);
        mActionBar.getRightTextView().setOnClickListener(this);

    }

    private int heigh;

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.friends_circle_send_trends_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        int gap = ScreenUtils.dpToPxInt(getApplicationContext(), 13);
        mRecyclerView.setPadding(gap / 2, gap, gap / 2, 0);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(gap, gap);
        dividerItemDecoration.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mLayoutParams = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
        heigh = ScreenUtils.dpToPxInt(getApplicationContext(), 100);
        mLayoutParams.height = heigh;
        mRecyclerView.setLayoutParams(mLayoutParams);
        mSelectedImageAdapter = new SelectedImageAdapter(this);
        //  mSelectedImages = (ArrayList<Image>) mSelectedImageAdapter.getDataItems();

        mSelectedImageAdapter.setMaxImageLimit(MAX_IMAGE_COUNT);
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemSubViewClickListener(new ItemClickSupport.OnItemSubViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view instanceof BadgeView) {
                    if (mSelectedImageAdapter.getDataItems() != null && mSelectedImageAdapter.getDataItems().size() > position) {
                        if (mSelectedImages.size() == MAX_IMAGE_COUNT) {
                            mSelectedImageAdapter.addEndImage(FriendsCircleSendTrendActivity.this);
                        }
                        mSelectedImageAdapter.getDataItems().remove(position);
                        mSelectedImages.remove(position);
                        mSelectedImageAdapter.notifyDataSetChanged();
                        setRecyclerViewHeigh();
                    }
                } else if (view instanceof ImageView) {
                    if (mSelectedImages.size() < MAX_IMAGE_COUNT
                            && position == mSelectedImageAdapter.getItemCount() - 1) {
                        ScreenUtils.dissmissKeyboard(mContext, mEditText);
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
        mRecyclerView.setAdapter(mSelectedImageAdapter);
    }

    private void initPopupWindow() {
        View menu = View.inflate(activity, R.layout.menu_popup_select_image, null);
        menu.findViewById(R.id.item_popupwindow_camera).setOnClickListener(this);
        menu.findViewById(R.id.item_popupwindow_photo).setOnClickListener(this);
        menu.findViewById(R.id.item_popupwindow_cancel).setOnClickListener(this);
        mPopupWindow = new PopupWindow(menu, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setContentView(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_left:
                finish();
                break;
            case R.id.action_bar_right_text:
                postTrends();
                break;
            case R.id.friends_circle_send_trends_location:

                break;
            case R.id.item_popupwindow_camera:
                mPopupWindow.dismiss();
                takePhoto();
                break;
            case R.id.item_popupwindow_photo:
                mPopupWindow.dismiss();
                Intent intent = new Intent(activity, PhotoPickActivity.class);
                intent.putExtra(Const.EXTRA_PICK_IMAGE_COUNT, MAX_IMAGE_COUNT - mSelectedImages.size());
                intent.putExtra(Const.EXTRA_IMAGE_DATA, mSelectedImages);
                startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
                break;
            case R.id.item_popupwindow_cancel:
                mPopupWindow.dismiss();
                break;
        }
    }

    private List<File> imgFiles = new ArrayList<File>();
    private void postTrends() {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            ToastUtils.AvoidRepeatToastShow(this,R.string.network_unavailable,Toast.LENGTH_SHORT);
            return;
        }
        if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
            ToastUtils.show(this, R.string.error_no_comment);
            return;
        }
        if (mEditText.getText().toString().trim().length() < 5){
            ToastUtils.show(this, "不得少于5个字");
            return;
        }
        QHFriendInfo qhFriendInfo = new QHFriendInfo();
        qhFriendInfo.setInfoText(mEditText.getText().toString().trim());
        if (private_toggleButton.isChecked())
            qhFriendInfo.setIsPublic(1);
        else
            qhFriendInfo.setIsPublic(0);
        for (int i = 0; i < mSelectedImages.size(); i++) {
            String path = mSelectedImages.get(i).imagePath.getPath();
            String compressUrl = null;
            try {
                compressUrl = ImageHelper.compressImageFile(path);
            } catch (Exception e) {
                Toast.makeText(this, "第" + (i + 1) + "张图片加载失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(compressUrl)) {
                File file = new File(compressUrl);
                imgFiles.add(file);
            }
        }
        mInProgressDialog.showDialog();
        new UploadTask(this, imgFiles, 3, qhFriendInfo, isLocation, this).execute("", "");
    }

    private void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTakePhotoUri = ImageHelper.getOutputImageUri();
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
        startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void postTrendsSuccess() {
        mInProgressDialog.dismissDialog();
        Toast.makeText(this, R.string.comment_post_sucess, Toast.LENGTH_SHORT).show();
        Intent result = new Intent();
        setResult(RESULT_OK, result);
        finish();
    }

    private void postTrendFaild() {
        mInProgressDialog.dismissDialog();
        // TODO
        Toast.makeText(this, R.string.comment_post_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.friends_circle_send_trends_location_toggleButton:
                if (isChecked){
                    isLocation = true;
                    ToastUtils.show(this,"位置已开启");
                }
                else
                    isLocation = false;
                break;
            case R.id.friends_circle_send_trends_private_toggleButton:
                if (!isChecked)
                    ToastUtils.show(this,"私有动态只有互相关注的好友才可以查看");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (mTakePhotoUri != null) {
                        ImageHelper.compressImageFile(mTakePhotoUri.getPath());
                        Image takenPhoto = new Image();
                        takenPhoto.imagePath = mTakePhotoUri;
                        mSelectedImages.add(takenPhoto);
                        ((SelectedImageAdapter) mRecyclerView.getAdapter()).addImage(takenPhoto);
                        MediaScannerConnection.scanFile(mContext, new String[]{FileUtils.getExternalImageDir()}, null, null);
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
        setRecyclerViewHeigh();
    }

    private void setRecyclerViewHeigh() {
        if (mSelectedImages.size() < 4) {
            mLayoutParams.height = heigh;
            mRecyclerView.setLayoutParams(mLayoutParams);
        }
        if (mSelectedImages.size() < 8 && mSelectedImages.size() > 3) {
            mLayoutParams.height = heigh * 2 - 50;
            mRecyclerView.setLayoutParams(mLayoutParams);
        }
        if (mSelectedImages.size() == 9) {
            mLayoutParams.height = heigh * 3 - 100;
            mRecyclerView.setLayoutParams(mLayoutParams);
        }
    }

    @Override
    public void onSuccess(String result) {
        postTrendsSuccess();
    }

    @Override
    public void onFailed() {
        postTrendFaild();
    }
}
