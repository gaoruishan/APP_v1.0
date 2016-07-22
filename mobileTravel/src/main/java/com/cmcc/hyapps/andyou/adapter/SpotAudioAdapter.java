
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.AudioIntro;

import java.util.List;

/**
 * @author kuloud
 */
public class SpotAudioAdapter extends RecyclerView.Adapter<SpotAudioAdapter.ViewHolder> {

    private List<AudioIntro> mDataItems;

    public SpotAudioAdapter() {
    }

    public SpotAudioAdapter(List<AudioIntro> items) {
        this();
        this.mDataItems = items;
    }

    public void setAudioList(List<AudioIntro> items) {
        this.mDataItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_map_spot_audio,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AudioIntro item = mDataItems.get(position);
        holder.itemView.setTag(item);

        holder.title.setText(item.title);
        holder.content.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.audio_title);
            content = (TextView) itemView.findViewById(R.id.audio_content);

        }
    }
}
