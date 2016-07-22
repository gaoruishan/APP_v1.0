/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.MultiSelectImageAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.Image;
import com.cmcc.hyapps.andyou.model.ImageBucket;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.task.LoadImageBucketTask;
import com.cmcc.hyapps.andyou.task.TaskListener;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class PhotoPickActivity extends BaseActivity {
    private RecyclerView mRecyclerView = null;
    private LoadImageBucketTask mLoadImageBucketTask;

    private MultiSelectImageAdapter mAdapter;
    private int mMaxSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);

        initViews();
        loadImageData();
    }

    /**
     * TODO Load images by folder
     * 
     * @return
     */
    private void loadImageData() {
        mLoadImageBucketTask = new LoadImageBucketTask(getApplicationContext());
        mLoadImageBucketTask.exe(new TaskListener<List<ImageBucket>>() {
            @Override
            public void onResult(List<ImageBucket> result) {
                List<Image> dataList = new ArrayList<Image>();
                for (int i = 0; i < result.size(); i++) {
                    dataList.addAll(result.get(i).imageList);
                }
                ImageBucket bucket = new ImageBucket();
                bucket.imageList = dataList;
                bucket.count = bucket.imageList.size();

                ArrayList<Image> selectedImages = getIntent()
                        .getParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA);
                ((MultiSelectImageAdapter) mRecyclerView.getAdapter()).setImageBucket(
                        bucket, selectedImages);
            }

            @Override
            public void onCancel(List<ImageBucket> result) {

            }
        }, true);

    }

    private void initViews() {
        mMaxSelection = getIntent().getIntExtra(Const.EXTRA_PICK_IMAGE_COUNT, 9);

        initActionBar();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                mAdapter.toggleSelection(view, position);
                if (mMaxSelection == 1) {
                    ArrayList<Image> selectionsImages = mAdapter.getSelection();
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA, selectionsImages);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        int scap = ScreenUtils.getDimenPx(getApplicationContext(), R.dimen.gallery_image_spacing);
        DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        int padding = ScreenUtils.getDimenPx(getBaseContext(), R.dimen.gallery_image_spacing);
        mRecyclerView.setPadding(padding, padding >> 1, 0, 0);

        mAdapter = new MultiSelectImageAdapter(mMaxSelection);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_title_choose_image);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
        if (mMaxSelection > 1) {
            actionBar.setRightMode(true);
            actionBar.getRightTextView().setText(R.string.finish);
            actionBar.getRightTextView().setOnClickListener(new OnClickListener() {

                @Override
                public void onValidClick(View v) {
                    ArrayList<Image> selectionsImages = mAdapter.getSelection();
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(Const.EXTRA_IMAGE_DATA, selectionsImages);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoadImageBucketTask != null) {
            mLoadImageBucketTask.cancel(true);
        }

        super.onDestroy();
    }

}
