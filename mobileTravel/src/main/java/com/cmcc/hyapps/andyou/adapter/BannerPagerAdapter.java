
package com.cmcc.hyapps.andyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.cmcc.hyapps.andyou.adapter.row.RowBannerDiscovery;
import com.cmcc.hyapps.andyou.adapter.row.RowBannerHome;
import com.cmcc.hyapps.andyou.adapter.row.RowBannerMarket;
import com.cmcc.hyapps.andyou.adapter.row.RowBannerNone;
import com.cmcc.hyapps.andyou.adapter.row.RowBannerScenic;
import com.cmcc.hyapps.andyou.adapter.row.RowBannerVideo;
import com.cmcc.hyapps.andyou.model.BannerSlide;
import com.cmcc.hyapps.andyou.model.QHHomeBanner;
import com.cmcc.hyapps.andyou.model.QHScenic;
import com.cmcc.hyapps.andyou.model.QHShopsBanner;
import com.cmcc.hyapps.andyou.model.Trip;
import com.cmcc.hyapps.andyou.model.Video;

import java.util.List;

/**
 * Created by kuloud on 14-8-16.
 */
public class BannerPagerAdapter<T> extends RecyclingPagerAdapter {
    /**
     * @author kuloud
     */
    public interface IActionCallback<T> {
        public void doAction(T data);
    }

    public static final int DEFAULT_SIZE = 1;

    private boolean mIsInfiniteLoop;

    /**
     * FIXME: walk around frequently pull to refresh, key dispatch error between
     * ScrollView and ViewPager. before scrolls ViewPager, check whether it's
     * empty before, do nothing if not.
     */
    private boolean mRemoveDefaultHolder;

    private List<T> mBannerSlideList;
    private Scene mScene = Scene.NONE;

    private Context mContext;
    private IActionCallback<T> mIActionCallback;

    /**
     * Scene type should be set here, or you should do check "instanceof" to
     * distinguish, or it should throw InvocationTargetException
     * 
     * @param context
     */
    public BannerPagerAdapter(Context context, Scene scene) {
        mContext = context;
        mIsInfiniteLoop = false;
        mScene = scene;
    }

    public enum Scene {
        NONE,
        SCNEIC,
        DISCOVERY,
        VIDEO,
        HOME,
        MARKET,
    }

    public void setBannerSlide(List<T> bannerSlides) {
        if (bannerSlides != null && !bannerSlides.isEmpty()) {
            mRemoveDefaultHolder = (getDataSize() == 0);
            mBannerSlideList = bannerSlides;
            notifyDataSetChanged();
        }
    }

    public boolean isManualScrollEnable() {
        return mRemoveDefaultHolder;
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
     * @param container
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        T bannerSilde = null;
        final int realPosition = getPosition(position);
        if (realPosition < getDataSize()) {
            bannerSilde = mBannerSlideList.get(realPosition);
        }
        View view = null;
        switch (mScene) {
            case SCNEIC:
                view = RowBannerScenic.getView(mContext, (BannerSlide) bannerSilde, convertView,
                        container,
                        (IActionCallback<BannerSlide>) mIActionCallback);
                break;
            case DISCOVERY:
                view = RowBannerDiscovery.getView(mContext, (Trip) bannerSilde, convertView,
                        container,
                        (IActionCallback<Trip>) mIActionCallback);
                break;
            case HOME:
                view = RowBannerHome.getView(mContext, (QHHomeBanner) bannerSilde, convertView,
                        container,
                        (IActionCallback<QHHomeBanner>) mIActionCallback);
                break;
            case VIDEO: {
                view = RowBannerVideo.getView(mContext, (QHScenic.QHVideo) bannerSilde, convertView,
                        container,
                        (IActionCallback<QHScenic.QHVideo>) mIActionCallback);
                break;
            }
            case MARKET:
                view = RowBannerMarket.getView(mContext, (QHShopsBanner) bannerSilde, convertView,
                        container,
                        (IActionCallback<QHShopsBanner>) mIActionCallback);
                break;
            default:
                view = RowBannerNone.getView(mContext, position, convertView, mIActionCallback);
                break;
        }
        return view;

    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return mIsInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public BannerPagerAdapter<T> setInfiniteLoop(boolean isInfiniteLoop) {
        this.mIsInfiniteLoop = isInfiniteLoop;
        return this;
    }

    @Override
    public int getCount() {
        int size = getDataSize();
        if (size == 0) {
            size = DEFAULT_SIZE;
        }
        return mIsInfiniteLoop ? Integer.MAX_VALUE : size;
    }

    /**
     * get really position
     * 
     * @param position
     * @return
     */
    private int getPosition(int position) {
        int size = getDataSize();
        if (size == 0) {
            size = DEFAULT_SIZE;
        }
        return this.mIsInfiniteLoop ? position % size : position;
    }

    private int getDataSize() {
        int size = (mBannerSlideList == null) ? 0 : mBannerSlideList.size();
        return size;
    }

    public IActionCallback<T> getActionCallback() {
        return mIActionCallback;
    }

    public BannerPagerAdapter<T> setActionCallback(IActionCallback<T> actionCallback) {
        this.mIActionCallback = actionCallback;
        return this;
    }
}
