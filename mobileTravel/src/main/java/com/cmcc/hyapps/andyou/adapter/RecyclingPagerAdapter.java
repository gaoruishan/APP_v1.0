
package com.cmcc.hyapps.andyou.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kuloud on 14-8-16.
 */
public abstract class RecyclingPagerAdapter extends PagerAdapter {
    private final RecycleBin mRecycleBin;

    public RecyclingPagerAdapter() {
        this(new RecycleBin());
    }

    public RecyclingPagerAdapter(RecycleBin mRecycleBin) {
        this.mRecycleBin = mRecycleBin;
        mRecycleBin.setViewTypeCount(getViewTypeCount());
    }

    @Override
    public void notifyDataSetChanged() {
        mRecycleBin.scrapActiveViews();
        super.notifyDataSetChanged();
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        int viewType = getItemViewType(position);
        View view = mRecycleBin.getScrapView(position, viewType);
        view = getView(position, view, container);
        container.addView(view);
        return view;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        int viewType = getItemViewType(position);
        mRecycleBin.addScrapView(view, position, viewType);
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * <p>
     * Returns the number of types of Views that will be created by
     * {@link #getView}. Each type represents a set of views that can be
     * converted in {@link #getView}. If the adapter always returns the same
     * type of View for all items, this method should return 1.
     * </p>
     * 
     * @return The number of types of Views that will be created by this adapter
     */
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * Get the type of View that will be created by {@link #getView} for the
     * specified item.
     * 
     * @param position The position of the item within the adapter's data set
     *            whose view type we want.
     * @return An integer representing the type of View. Two views should share
     *         the same type if one can be converted to the other in
     *         {@link #getView}. Note: Integers must be in the range 0 to
     *         {@link #getViewTypeCount} - 1.
     */
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set. You can either create a View manually or inflate it from an XML
     * layout file. When the View is inflated, the parent View (GridView,
     * ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     * 
     * @param position The position of the item within the adapter's data set of
     *            the item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should
     *            check that this view is non-null and of an appropriate type
     *            before using. If it is not possible to convert this view to
     *            display the correct data, this method can create a new view.
     *            Heterogeneous lists can specify their number of view types, so
     *            that this View is always of the right type (see
     *            {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
     *            ).
     * @return A View corresponding to the data at the specified position.
     */
    public abstract View getView(int position, View convertView, ViewGroup container);
}
