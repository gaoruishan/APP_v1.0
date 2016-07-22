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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.FavouriteAdapter;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.DataLoader;
import com.cmcc.hyapps.andyou.data.GsonRequest;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.FavoriteItem;
import com.cmcc.hyapps.andyou.model.FavoriteItem.FavoriteItemList;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.Log;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.List;

/**
 * @author kuloud
 */
public class FavoritesActivity extends BaseActivity implements DataLoader<FavoriteItemList>,
        DataLoader.DataLoaderCallback<FavoriteItemList> {
    private RecyclerView mRecyclerView;
    private View mEmptyHintView;
    private View mLoadingProgress;
    private FavouriteAdapter mAdapter;
    private GsonRequest<FavoriteItemList> mRequestInFly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initViews();
        loadMoreData(this, DataLoader.MODE_LOAD_MORE);
    }

    @Override
    protected void onDestroy() {
        onLoaderDestory();
        super.onDestroy();
    }

    @Override
    public void loadMoreData(final DataLoaderCallback<FavoriteItemList> cb, final int mode) {
        final String url = ServerAPI.Favorites.buildGetFavoritesUrl(5, 0);
        Log.d("Loading ItineraryInfo list from %s", url);
        mRequestInFly = RequestManager.getInstance().sendGsonRequest(url, FavoriteItemList.class,
                new Response.Listener<FavoriteItemList>() {
                    @Override
                    public void onResponse(FavoriteItemList response) {
                        Log.d("onResponse, ScenicSpotList=%s", response);
                        if (cb != null) {
                            cb.onLoadFinished(response, mode);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic spots from %s", url);
                        if (cb != null) {
                            cb.onLoadError(mode);
                        }
                    }
                }, requestTag);
    }

    private void initViews() {
        initActionBar();
        initListView();
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_favorites);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
        actionBar.setRightMode(true);
        actionBar.getRightTextView().setText(R.string.edit);
        actionBar.getRightTextView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
            }
        });
        actionBar.getRightTextView().setVisibility(View.GONE);
    }

    private void initListView() {
        mEmptyHintView = findViewById(R.id.empty_hint_view);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        int padding = ScreenUtils.dpToPxInt(activity, 13);
        mRecyclerView.setPadding(padding, padding / 2, padding, 0);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decor = new DividerItemDecoration(padding, padding);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FavouriteAdapter();
        mRecyclerView.setAdapter(mAdapter);
        ItemClickSupport clickListener = ItemClickSupport.addTo(mRecyclerView);
        clickListener.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(activity, ScenicDetailActivity.class);
                FavoriteItem item = (FavoriteItem) view.getTag();
                intent.putExtra(Const.EXTRA_ID, item.id);
                intent.putExtra(Const.EXTRA_NAME, item.name);
            }
        });
    }

    @Override
    public void onLoadError(int mode) {
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFinished(FavoriteItemList favoriteItemList, int mode) {
        mLoadingProgress.setVisibility(View.GONE);

        List<FavoriteItem> list = null;
        if (favoriteItemList != null) {
            list = favoriteItemList.list;
        }

        if (list == null || list.isEmpty()) {
            if (mAdapter.getItemCount() <= 0) {
                mEmptyHintView.setVisibility(View.VISIBLE);
            }
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.appendDataItems(list);
        }
    }

    @Override
    public void onLoaderDestory() {
        if (mRequestInFly != null) {
            mRequestInFly.cancel();
        }
    }
}
