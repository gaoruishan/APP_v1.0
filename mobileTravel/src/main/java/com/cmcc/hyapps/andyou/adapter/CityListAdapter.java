
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kuloud.android.widget.recyclerview.DividerItemDecoration;
import com.kuloud.android.widget.recyclerview.ItemClickSupport;
import com.kuloud.android.widget.recyclerview.ItemClickSupport.OnItemClickListener;
import com.cmcc.hyapps.andyou.R;
import com.cmcc.hyapps.andyou.activity.CityListActivity;
import com.cmcc.hyapps.andyou.model.Scenic;
import com.cmcc.hyapps.andyou.util.ScreenUtils;
import com.cmcc.hyapps.andyou.widget.pinyinsidebar.SortModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CityListAdapter extends RecyclerView.Adapter<ViewHolder> implements SectionIndexer {
    private static final int TYPE_SCENIC = 1;
    private static final int TYPE_CITY = 2;
    private static final int TYPE_CONTINUE = 3;

    private CityListActivity mActivity;

    private List<Scenic> recommendScenic = null;
    private List<SortModel> list = new ArrayList<SortModel>();

    public CityListAdapter(CityListActivity activity) {
        mActivity = activity;
        setHasStableIds(true);
    }

    synchronized public void setItems(List<SortModel> list) {
        if (hasRecommendScenic()) {
            SortModel recommend = this.list.get(0);
            this.list.clear();
            this.list.add(recommend);
        } else {
            this.list.clear();
        }
        this.list.addAll(list);
        this.list.add(mockFooterModel());
        notifyDataSetChanged();
    }

    private SortModel mockFooterModel() {
        SortModel model = new SortModel();
        model.setSortLetters("_");
        return model;
    }

    public List<SortModel> getItems() {
        return list;
    }

    synchronized public void setRecommendScenic(List<Scenic> recommendScenic) {
        if (hasRecommendScenic()) {
            this.recommendScenic = recommendScenic;
        } else {
            this.recommendScenic = recommendScenic;
            SortModel model = new SortModel();
            model.setSortLetters("荐");
            this.list.add(0, model);
            this.list.add(mockFooterModel());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasRecommendScenic() && (position == 0)) {
            return TYPE_SCENIC;
        } else if (position == getItemCount() - 1) {
            return TYPE_CONTINUE;
        } else {
            return TYPE_CITY;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = null;
        if (viewType == TYPE_SCENIC) {
            itemView = LayoutInflater.from(context).inflate(
                    R.layout.fragment_search_recommdation, parent, false);
            return new ScenicViewHolder(context, itemView);
        } else if (viewType == TYPE_CONTINUE) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_search_city_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_search_city_item, parent, false);
            return new CityViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        switch (getItemViewType(position)) {
            case TYPE_SCENIC:
                RecyclerView rv = ((ScenicViewHolder) vh).scenics;
                int scap = ScreenUtils.dpToPxInt(rv.getContext(), 3);
                int padding = ScreenUtils.getDimenPx(rv.getContext(),
                        R.dimen.common_margin);
                int margin = ScreenUtils.dpToPxInt(rv.getContext(), 8);
                int itemHeight = (int) (((ScreenUtils.getScreenWidth(rv.getContext()) - scap * 2)
                        - padding * 2 - margin) / 3);
                int rowCount = (recommendScenic.size() + 2) / 3;
                int height = itemHeight * rowCount + scap * (rowCount - 1)
                        + padding * 2;
                rv.getLayoutParams().height = height;
                rv.setAdapter(new SearchAdapter(recommendScenic));
                ItemClickSupport clickSupport = ItemClickSupport.addTo(rv);
                clickSupport.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        if (mActivity != null && !mActivity.isFinishing()) {
                            Scenic scenic = (Scenic) view.getTag();
                            if (scenic == null) {
                                return;
                            }
                            mActivity.onScenicSelected(scenic);
                        }
                    }
                });
                break;
            case TYPE_CITY:
                CityViewHolder cityVH = (CityViewHolder) vh;
                if (list.get(position).getCity() != null) {
                    cityVH.itemView.setTag(true);
                    cityVH.itemView.setVisibility(View.VISIBLE);
                    cityVH.city.setText(list.get(position).getCity().name);
                    attachClickListener(cityVH, cityVH.itemView, position);
                } else {
                    cityVH.itemView.setVisibility(View.GONE);
                    cityVH.itemView.setTag(false);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        String sortStr = list.get(position).getSortLetters();
        char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
        return firstChar;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ScenicViewHolder extends ViewHolder {

        RecyclerView scenics;

        public ScenicViewHolder(Context context, View itemView) {
            super(itemView);
            scenics = (RecyclerView) itemView.findViewById(R.id.hot_scenics);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
            scenics.setLayoutManager(layoutManager);
            scenics.setItemAnimator(new DefaultItemAnimator());
            int scap = ScreenUtils.getDimenPx(context, R.dimen.scenic_image_spacing);
            DividerItemDecoration decor = new DividerItemDecoration(scap, scap);
            decor.initWithRecyclerView(scenics);
            scenics.addItemDecoration(decor);
            scenics.setAdapter(new SearchAdapter());
        }
    }

    public static class CityViewHolder extends ViewHolder {

        TextView city;

        public CityViewHolder(View itemView) {
            super(itemView);
            city = (TextView) itemView;
        }
    }

    public static class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        if (hasRecommendScenic() && section == 0) {
            return 0;
        }
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    synchronized private boolean hasRecommendScenic() {
        return !(recommendScenic == null || recommendScenic.isEmpty());
    }
}
