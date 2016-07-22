
package com.cmcc.hyapps.andyou.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.support.OnClickListener;

import java.util.List;

/**
 * @author kuloud
 */
public class ScenicOverviewAdapter extends BaseHeaderAdapter<String, AudioIntro> {

    private Activity mActivity;

    public ScenicOverviewAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public ScenicOverviewAdapter(List<AudioIntro> items, Activity activity) {
        this(activity);
        this.mDataItems = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView arrow;
        private TextView title;
        private ImageView playState;
        private View contentContainer;
        private TextView content;
        private boolean expand;
        private View itemExpandable;

        public ViewHolder(View itemView) {
            super(itemView);
            arrow = (ImageView) itemView.findViewById(R.id.scenic_overview_expand_arrow);
            title = (TextView) itemView.findViewById(R.id.scenic_overview_group_title);
            playState = (ImageView) itemView.findViewById(R.id.scenic_overview_group_play);
            contentContainer = itemView.findViewById(R.id.scenic_overview_content_container);
            content = (TextView) contentContainer.findViewById(R.id.scenic_overview_content);
            itemExpandable = itemView.findViewById(R.id.item_expandable);
        }
    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateHeaderViewHolder(
            ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_scenic_overview_header,
                parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBinderHeaderViewHolder(android.support.v7.widget.RecyclerView.ViewHolder holder) {
        final HeaderViewHolder vholder = (HeaderViewHolder) holder;
        if (mHeader != null) {
//            RequestManager.getInstance().getImageLoader().get(mHeader,
//                    ImageLoader.getImageListener(vholder.headerView, R.drawable.bg_image_hint,
//                            R.drawable.bg_image_hint));

            ImageUtil.DisplayImage(mHeader, vholder.headerView, R.drawable.bg_image_hint,
                    R.drawable.bg_image_hint);
        }
    }

    @Override
    public android.support.v7.widget.RecyclerView.ViewHolder onCreateItemViewHolder(
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scenic_overview,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBinderItemViewHolder(
            final android.support.v7.widget.RecyclerView.ViewHolder vholder,
            int position) {
        final ViewHolder holder = (ViewHolder) vholder;
        final AudioIntro item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemExpandable.setOnClickListener(new OnClickListener() {

            @Override
            public void onValidClick(View v) {
                holder.expand = !holder.expand;
                holder.arrow.getDrawable().setLevel(holder.expand ? 1 : 2);
                holder.contentContainer.setVisibility(holder.expand ? View.VISIBLE : View.GONE);
                holder.arrow.setTag(holder.expand);
            }
        });
        holder.title.setText(item.title);
        holder.content.setText(item.content);
        holder.arrow.getDrawable().setLevel(holder.expand ? 1 : 2);
        holder.contentContainer.setVisibility(holder.expand ? View.VISIBLE : View.GONE);

        holder.playState.setTag(item);
        attachClickListener(holder, holder.playState, position);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView headerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = (ImageView) itemView.findViewById(R.id.scenic_overview_header);
        }
    }
}
