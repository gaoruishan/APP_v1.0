/**
 *
 */

package com.cmcc.hyapps.andyou.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.app.Const;
import com.cmcc.hyapps.andyou.model.ScenicDetails.Transport;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuloud
 */
public class TrafficActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Transport> mTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTransport = getIntent().getParcelableArrayListExtra(Const.EXTRA_TRANSPORT_DATA);
        if (mTransport == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_traffic);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int scap = ScreenUtils.dpToPxInt(activity, 13);
        DividerItemDecoration decor = new DividerItemDecoration(scap);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        TrafficAdapter adapter = new TrafficAdapter(mTransport);
        mRecyclerView.setAdapter(adapter);

        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle(R.string.action_bar_title_traffic);
        actionBar.getLeftView().setImageResource(R.drawable.ic_action_bar_back_selecter);
        actionBar.getLeftView().setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                finish();
            }
        });
    }

    class TrafficAdapter extends RecyclerView.Adapter<TrafficAdapter.ViewHolder> {

        private List<Transport> mDataItems;

        public TrafficAdapter() {
        }

        public TrafficAdapter(List<Transport> items) {
            this.mDataItems = items;
        }

        public void setDownloadList(List<Transport> items) {
            this.mDataItems = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_traffic,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Transport item = mDataItems.get(position);
            holder.itemView.setTag(item);
            holder.titleView.setText(item.title);
            holder.descView.setText(item.desc);
        }

        @Override
        public int getItemCount() {
            return mDataItems == null ? 0 : mDataItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleView;
            TextView descView;

            public ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.transport_title);
                descView = (TextView) itemView.findViewById(R.id.transport_desc);
            }
        }
    }

}
