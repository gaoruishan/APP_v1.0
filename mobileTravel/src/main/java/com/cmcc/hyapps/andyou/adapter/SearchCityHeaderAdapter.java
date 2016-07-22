
package com.cmcc.hyapps.andyou.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SortModel;

import java.util.List;
import java.util.Locale;

public class SearchCityHeaderAdapter implements
        StickyHeadersAdapter<SearchCityHeaderAdapter.ViewHolder> {
    private List<SortModel> list = null;

    public SearchCityHeaderAdapter() {
    }

    public SearchCityHeaderAdapter(List<SortModel> list) {
        this();
        this.list = list;
    }

    public void setItems(List<SortModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_search_city_header, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {
        CharSequence letter = list.get(position).getSortLetters().toUpperCase(Locale.CHINESE).subSequence(0, 1);
        // TODO KISS
        if ("荐".equals(letter) || "_".equals(letter)) {
            headerViewHolder.itemView.setVisibility(View.GONE);
        } else {
            headerViewHolder.itemView.setVisibility(View.VISIBLE);
            headerViewHolder.letter.setText(letter);
        }
    }

    @Override
    public long getHeaderId(int position) {
        String sortStr = list.get(position).getSortLetters();
        char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
        return firstChar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView letter;

        public ViewHolder(View itemView) {
            super(itemView);
            letter = (TextView) itemView;
        }
    }

}
