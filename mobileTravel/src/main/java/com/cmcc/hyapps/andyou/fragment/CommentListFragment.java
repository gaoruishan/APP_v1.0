/**
 *
 */

package com.cmcc.hyapps.andyou.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.adapter.CommentAdapter;
import com.cmcc.hyapps.andyou.app.ServerAPI;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.util.Log;

import java.util.ArrayList;
import com.cmcc.hyapps.andyou.model.QHComment.QHCommentList;
/**
 * @author kuloud
 */
public class CommentListFragment extends BaseFragment {
    private ArrayList<Trip> mTripList = new ArrayList<Trip>();

    private View mLoadingProgress;
    private View mEmptyHintView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private int mId = -1;

    private static final int GET_COMMENTS_PARAM_LIMIT = 10;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        getCommentList();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        initListView(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void initListView(View root) {
        mEmptyHintView = root.findViewById(R.id.empty_hint_view);
        mEmptyHintView.setVisibility(View.VISIBLE);
        mLoadingProgress = root.findViewById(R.id.loading_progress);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new CommentAdapter();
        ItemClickSupport clickSupport = ItemClickSupport.addTo(mRecyclerView);
        clickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getCommentList() {
        final String url = ServerAPI.User.buildCommentsUrl(GET_COMMENTS_PARAM_LIMIT, 0);
        Log.d("Loading comment list from %s", url);

        RequestManager.getInstance().sendGsonRequest(url, QHCommentList.class,
                new Response.Listener<QHCommentList>() {
                    @Override
                    public void onResponse(QHCommentList response) {
                        Log.d("onResponse, CommentList=%s", response);
                        onCommentListLoaded(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(error, "Error loading scenic details from %s", url);
                        // TODO handle it
                    }
                }, "my_comment");
    }

    public void onCommentListLoaded(QHCommentList data) {
        mLoadingProgress.setVisibility(View.GONE);
        mAdapter.setDataItems(data.results);
        if(data.results != null && data.results.size() > 0)
            mEmptyHintView.setVisibility(View.GONE);
        else mEmptyHintView.setVisibility(View.VISIBLE);
    }

}
