// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MarketSearchListFragment$$ViewInjector<T extends com.cmcc.hyapps.andyou.fragment.MarketSearchListFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427386, "field 'mLoadingProgress'");
    target.mLoadingProgress = finder.castView(view, 2131427386, "field 'mLoadingProgress'");
    view = finder.findRequiredView(source, 2131427656, "field 'search_content'");
    target.search_content = finder.castView(view, 2131427656, "field 'search_content'");
    view = finder.findRequiredView(source, 2131427706, "field 'mPullToRefreshView'");
    target.mPullToRefreshView = finder.castView(view, 2131427706, "field 'mPullToRefreshView'");
    view = finder.findRequiredView(source, 2131427663, "field 'search_tv'");
    target.search_tv = finder.castView(view, 2131427663, "field 'search_tv'");
    view = finder.findRequiredView(source, 2131427400, "field 'empty_hint_view'");
    target.empty_hint_view = view;
  }

  @Override public void reset(T target) {
    target.mLoadingProgress = null;
    target.search_content = null;
    target.mPullToRefreshView = null;
    target.search_tv = null;
    target.empty_hint_view = null;
  }
}
