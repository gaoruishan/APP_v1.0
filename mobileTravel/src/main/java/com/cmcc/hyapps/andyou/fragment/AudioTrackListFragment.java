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
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.ListenActivity;
import com.cmcc.hyapps.andyou.activity.ListenActivity.ActionDelegate;
import com.cmcc.hyapps.andyou.model.AudioIntro;
import com.cmcc.hyapps.andyou.model.ScenicAudio;
import com.cmcc.hyapps.andyou.support.OnClickListener;
import com.cmcc.hyapps.andyou.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * PAGE 3: Session list
 * 
 * @author kuloud
 */
public class AudioTrackListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private AudioIntroListAdapter mAdapter;
    private ActionDelegate mActionDelegate;

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_recycler_list, container, false);
        rootView.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        rootView.setBackgroundColor(getResources().getColor(R.color.black_transprent));
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()
                .getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration decor = new DividerItemDecoration(getResources().getColor(
                R.color.base_grey_line), 1, 0);
        decor.initWithRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(decor);
        mAdapter = new AudioIntroListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
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

    public void invalidViews(final List<ScenicAudio> sceniAudios) {
        getView().post(new Runnable() {

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
        });
    }

    private class AudioIntroListAdapter extends
            RecyclerView.Adapter<AudioIntroListAdapter.ViewHolder> {
        private List<AudioIntro> mDataItems;

        public AudioIntroListAdapter() {
        }

        public void setDataItems(List<AudioIntro> dataItems) {
            this.mDataItems = dataItems;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            public TextView audioNameTextView;
            public TextView audioDurationTextView;
            public View playIndicator;

            public ViewHolder(View itemView) {
                super(itemView);
                playIndicator = itemView.findViewById(R.id.play_indicator);
                audioNameTextView = (TextView) itemView.findViewById(R.id.audio_intro_name);
                audioDurationTextView = (TextView) itemView
                        .findViewById(R.id.audio_intro_duration);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_audio_intro_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final AudioIntro item = mDataItems.get(position);
            holder.itemView.setTag(item);
            holder.itemView.setSelected(item.highlight);
            holder.audioNameTextView.setText(item.title);
            holder.audioDurationTextView.setText(FormatUtils.makeTimeString(
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

            if (item.highlight) {
                holder.playIndicator.setVisibility(View.VISIBLE);
                holder.itemView.setSelected(true);
            } else {
                holder.playIndicator.setVisibility(View.INVISIBLE);
                holder.itemView.setSelected(false);
            }
        }

        @Override
        public int getItemCount() {
            return mDataItems == null ? 0 : mDataItems.size();
        }
    }
}
