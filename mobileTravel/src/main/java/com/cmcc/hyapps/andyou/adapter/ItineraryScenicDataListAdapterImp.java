
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.model.BasicScenicData;

import java.util.HashMap;
import java.util.Map;

public class ItineraryScenicDataListAdapterImp extends BasicScenicDataListAdapter {
    private Map<String, BasicScenicData> mSelected = new HashMap<String, BasicScenicData>();

    public Map<String, BasicScenicData> getSelected() {
        return mSelected;
    }

    public void setSelected(Map<String, BasicScenicData> selected) {
        this.mSelected = selected;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerary_spot,
                parent, false);
        return new VH(v);
    }

    static class VH extends RecyclerView.ViewHolder {
        private TextView name;
        private CheckBox checkbox;

        public VH(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh,
            int position) {
        VH holder = (VH) vh;
        final BasicScenicData item = mDataItems.get(position);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(mItemClickListener);

        holder.name.setText(item.name());
        if (selectedThrehold > 1) {
            holder.checkbox.setChecked(mSelected.containsKey(item.name()));
            holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mSelected.put(item.name(), item);
                    } else {
                        mSelected.remove(item.name());
                    }
                }
            });
        } else {
            holder.checkbox.setVisibility(View.GONE);
        }
    }
}
