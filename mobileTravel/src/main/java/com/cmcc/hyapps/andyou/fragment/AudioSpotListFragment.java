/**
 *
 */

package com.cmcc.hyapps.andyou.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cmcc.hyapps.andyou.util.ImageUtil;
import com.kuloud.android.widget.recyclerview.BaseHeaderAdapter;
import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.ListenActivity;
import com.cmcc.hyapps.andyou.activity.ListenActivity.ActionDelegate;
import com.cmcc.hyapps.andyou.data.RequestManager;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.FormatUtils;

/**
 * PAGE 1: Spot list
 * 
 * @author kuloud
 */
public class AudioSpotListFragment extends BaseFragment {
    private final long DURATION = 1000;

    private RecyclerView mRecyclerView;
    private AudioIntroSpotListAdapter mAdapter;
    private ActionDelegate mActionDelegate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        rootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        rootView.setBackgroundColor(getResources().getColor(R.color.black_transprent));
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration decor = new DividerItemDecoration(getResources().getColor(R.color.base_grey_line), 1, 0);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mAdapter = new AudioIntroSpotListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public void invalidViews(final List<ScenicAudio> sceniAudios) {
        getView().postDelayed(new Runnable() {

            @Override
            public void run() {
                List<AudioIntro> currentPlayingAlbum = new ArrayList<AudioIntro>();
                if (sceniAudios != null) {
                    for (ScenicAudio scenicAudio : sceniAudios) {
                        if (scenicAudio.audio == null) {
                            continue;
                        }
                        for (AudioIntro audio : scenicAudio.audio) {
                            if (audio == null) {
                                continue;
                            }
                            if (audio.type == type) {
                                currentPlayingAlbum.add(audio);
                            }
                        }
                        // currentPlayingAlbum.addAll(scenicAudio.audio);
                    }
                }

                mAdapter.setDataItems(currentPlayingAlbum);
            }
        }, DURATION);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActionDelegate = ((ListenActivity) activity).actionDelegate;
    }

    @Override
    public void onDetach() {
        mActionDelegate = null;
        super.onDetach();
    }

    private class AudioIntroSpotListAdapter extends BaseHeaderAdapter<Object, AudioIntro> {

        public AudioIntroSpotListAdapter() {
            setHeader(new Object());
        }

        public void setData(List<AudioIntro> dataItems) {
            this.mDataItems = dataItems;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView spotImageView;
            public TextView spotNameTextView;
            public TextView audioCounTextView;
            public View playIndicator;

            public ViewHolder(View itemView) {
                super(itemView);
                playIndicator = itemView.findViewById(R.id.play_indicator);
                spotImageView = (ImageView) itemView.findViewById(R.id.audio_intro_spot_image);
                spotNameTextView = (TextView) itemView.findViewById(R.id.audio_intro_spot_name);
                audioCounTextView = (TextView) itemView.findViewById(R.id.audio_intro_count);
            }

        }

        @Override
        public int getItemCount() {
            return mDataItems == null ? 0 : mDataItems.size();
        }

        @Override
        public android.support.v7.widget.RecyclerView.ViewHolder onCreateHeaderViewHolder(
                ViewGroup parent) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_audio_intro_spot_list,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBinderHeaderViewHolder(
                android.support.v7.widget.RecyclerView.ViewHolder h) {
            ViewHolder holder = (ViewHolder) h;
            if (type == AudioIntro.TYPE_ALLUSIONS) {
                holder.spotImageView.setImageResource(R.drawable.ic_allusion);
                holder.spotNameTextView.setText(R.string.allusion);
            } else {
                holder.spotImageView.setImageResource(R.drawable.ic_listen_by_selecter);
                holder.spotNameTextView.setText(R.string.auto_guide);
                holder.playIndicator.setVisibility(View.INVISIBLE);
                holder.audioCounTextView.setVisibility(View.INVISIBLE);
                holder.itemView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onValidClick(View v) {
                        if (mActionDelegate != null) {
                            mActionDelegate.switchAutoGuide();
                        }
                    }
                });
            }
        }

        @Override
        public android.support.v7.widget.RecyclerView.ViewHolder onCreateItemViewHolder(
                ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_audio_intro_spot_list,
                    parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBinderItemViewHolder(
                android.support.v7.widget.RecyclerView.ViewHolder h, int position) {
            ViewHolder holder = (ViewHolder) h;
            final AudioIntro item = mDataItems.get(position);
            holder.itemView.setTag(item);
            String url = item.scenicImage;
            if (TextUtils.isEmpty(url)) {
                url = item.imageUrl;
            }
            if (!TextUtils.isEmpty(url)) {
                ImageUtil.DisplayImage(url, holder.spotImageView,
                        R.drawable.bg_image_hint,
                        R.drawable.bg_image_hint);
//                RequestManager.getInstance().getImageLoader().get(url,
//                        ImageLoader.getImageListener(holder.spotImageView,
//                                R.drawable.bg_image_hint,
//                                R.drawable.bg_image_hint));
            }

            if (item.highlight) {
                holder.itemView.setSelected(true);
                holder.playIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setSelected(false);
                holder.playIndicator.setVisibility(View.INVISIBLE);
            }

            if (type == AudioIntro.TYPE_ALLUSIONS) {
                holder.spotNameTextView.setText(item.title);
            } else {
                if (TextUtils.isEmpty(item.scenicName)) {
                    holder.spotNameTextView.setText(item.title);
                } else {
                    holder.spotNameTextView.setText(item.scenicName);
                }
            }
            holder.audioCounTextView.setText(FormatUtils.makeTimeString(
                    holder.itemView.getContext(),
                    item.duration / 1000));
            holder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onValidClick(View v) {
                    if (mActionDelegate != null) {
                        final AudioIntro item = (AudioIntro) v.getTag();
                        mActionDelegate.play(item);
                    }
                }
            });
            // int audioCount = item.audio != null ? item.audio.size() : 0;
            // holder.audioCounTextView.setText(String.valueOf(audioCount));
            // holder.itemView.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onValidClick(View v) {
            // if (mActionDelegate != null) {
            // final ScenicAudio item = (ScenicAudio) v.getTag();
            // // mActionDelegate.play(item);
            // }
            // }
            // });
        }

    }
}
