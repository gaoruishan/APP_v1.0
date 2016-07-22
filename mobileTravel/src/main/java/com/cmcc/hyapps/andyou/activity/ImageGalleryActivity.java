
package com.cmcc.hyapps.andyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.ImageGalleryAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.DataLoader.DataLoaderCallback;
import com.cmcc.hyapps.andyou.data.OfflinePackageManager;
import com.cmcc.hyapps.andyou.data.UrlListLoader;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImage;
import com.cmcc.hyapps.andyou.model.CompoundImage.TextImageList;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.util.ExcessiveClickBlocker;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;
import com.cmcc.hyapps.andyou.widget.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends BaseActivity implements OnClickListener,
        DataLoaderCallback<TextImageList> {
    private static final String TAG = ImageGalleryActivity.class.getSimpleName();
    public static final int TYPE_SCENIC = 1;
    public static final int TYPE_COMMENT = 2;

    private RecyclerView mRecyclerView;
    private ImageGalleryAdapter mAdapter;
    private View mLoadingProgress;
    private View mEmptyHintView;
    private PullToRefreshRecyclerView mPullToRefreshView;

    private int mId = -1;

    private static final int GET_COMMENTS_PARAM_LIMIT = 3 * 6;
    private static final int REQUEST_CODE_LOGIN_POST_IMAGES = 1;
    private static final int REQUEST_CODE_POST_IMAGES = 2;
    private UrlListLoader<TextImageList> mLoader;
    private List<TextImage> mImageList = new ArrayList<TextImage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getIntExtra(Const.EXTRA_ID, -1);
        if (mId < 0) {
            Log.e("Invalid scenic id %s", mId);
            finish();
            return;
        }
        setContentView(R.layout.activity_list);
        initViews();

        mPullToRefreshView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);

        mLoader = new UrlListLoader<TextImageList>(requestTag, TextImageList.class);
        mLoader.setUrl(ServerAPI.ScenicImages.buildUrl(mId));
        mLoader.setPageLimit(GET_COMMENTS_PARAM_LIMIT);

        TextImageList offlineList = OfflinePackageManager.getInstance().getOfflineData(mId,
                TextImageList.class);
        if (offlineList != null) {
            Log.d("Loading offline image list data for scenic %d", mId);
            onLoadFinished(offlineList, DataLoader.MODE_LOAD_MORE);
        } else {
            mLoader.loadMoreData(this, DataLoader.MODE_LOAD_MORE);
        }
    }

    private void initViews() {
        initActionBar();
        initListView();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(getIntent().getStringExtra(Const.EXTRA_NAME));
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(this);
        if (getIntent().getBooleanExtra(Const.EXTRA_SHOW_POST_IMAGE_BUTTON, false)) {
            actionBar.getRightView().setImageResource(R.drawable.ic_action_bar_camera_selecter);
            actionBar.getRightView().setOnClickListener(this);
        }
    }

    private void initListView() {
        mPullToRefreshView = (PullToRefreshRecyclerView) findViewById(R.id.pulltorefresh_twowayview);
        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                int mode = refreshView.getCurrentMode() ==
                        Mode.PULL_FROM_START ? DataLoader.MODE_REFRESH
                                : DataLoader.MODE_LOAD_MORE;
                mLoader.loadMoreData(ImageGalleryActivity.this, mode);
            }
        });

        mRecyclerView = mPullToRefreshView.getRefreshableView();
        mLoadingProgress = findViewById(R.id.loading_progress);
        mEmptyHintView = findViewById(R.id.empty_hint_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        int p = ScreenUtils.dpToPxInt(activity, 0.5f);
        mRecyclerView.setPadding(-p, 0, -p, 0);
        int scap = ScreenUtils.dpToPxInt(activity, 4f);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                List<TextImage> items = mAdapter.getDataItems();
                ArrayList<Image> imageList = new ArrayList<Image>();
                for (TextImage textImage : items) {
                    if (textImage != null && textImage.image != null) {
                        Image image = new Image();
                        image.infoTime = textImage.createTime;
                        image.imagePath = Uri.parse(textImage.image.largeImage);
                        image.thumbnailPath = Uri.parse(textImage.image.smallImage);
                        imageList.add(image);
                    }
                }

                Intent intent = new Intent(getApplicationContext(), PhotoPreviewActivity.class);
                intent.putExtra(Const.EXTRA_IMAGE_DATA, imageList);
                intent.putExtra(Const.EXTRA_IMAGE_PREVIEW_START_INDEX, position);
                startActivity(intent);
            }
        });

        mAdapter = new ImageGalleryAdapter(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
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
            case R.id.action_bar_right: {
                // if (!UserManager.makeSureLogin(this,
                // REQUEST_CODE_LOGIN_POST_IMAGES)) {
                postImages();
                // }
                break;
            }
            default:
                break;
        }
    }

    private void postImages() {
        Intent intent = new Intent(this, PostImagesActivity.class);
        intent.putExtra(Const.EXTRA_ID, mId);
        startActivityForResult(intent, REQUEST_CODE_POST_IMAGES);
    }

    @Override
    public void onLoadFinished(TextImageList textImageList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);
        mPullToRefreshView.onRefreshComplete();

        List<TextImage> list = null;
        if (textImageList != null) {
            list = textImageList.list;
        }

        if (list == null || list.isEmpty()) {
            if (mImageList.isEmpty()) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            mPullToRefreshView.setVisibility(View.VISIBLE);
            Log.d(TAG, "list:"+list);
            if (mode == DataLoader.MODE_REFRESH) {
                // There are refreshed items on server, replace local items,
                // otherwise do nothing
                if (mImageList.isEmpty() || !list.get(0).equals(mImageList.get(0))) {
                    mImageList.clear();
                    mImageList.addAll(list);
                    mAdapter.setDataItems(list);
                }
            } else {
                mImageList.addAll(list);
                mAdapter.appendDataItems(list);
            }
        }
    }

    @Override
    public void onLoadError(int mode) {
        // TODO
        mLoadingProgress.setVisibility(View.GONE);
        Toast.makeText(this, R.string.loading_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LOGIN_POST_IMAGES:
                    postImages();
                    break;
                case REQUEST_CODE_POST_IMAGES:
                    if (mLoader != null) {
                        mLoader.getPaginator().onNewDataAdded();
                        mLoader.loadMoreData(ImageGalleryActivity.this, DataLoader.MODE_REFRESH);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
