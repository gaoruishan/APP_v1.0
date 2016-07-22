// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MapListenDialogFragment$$ViewInjector<T extends com.cmcc.hyapps.andyou.fragment.MapListenDialogFragment> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427762, "field 'mSpotImage'");
    target.mSpotImage = finder.castView(view, 2131427762, "field 'mSpotImage'");
    view = finder.findRequiredView(source, 2131427763, "field 'mListen'");
    target.mListen = finder.castView(view, 2131427763, "field 'mListen'");
    view = finder.findRequiredView(source, 2131427764, "field 'mSpotName'");
    target.mSpotName = finder.castView(view, 2131427764, "field 'mSpotName'");
    view = finder.findRequiredView(source, 2131427765, "field 'mSpotDistance'");
    target.mSpotDistance = finder.castView(view, 2131427765, "field 'mSpotDistance'");
    view = finder.findRequiredView(source, 2131427766, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131427766, "field 'mRecyclerView'");
  }

  @Override public void reset(T target) {
    target.mSpotImage = null;
    target.mListen = null;
    target.mSpotName = null;
    target.mSpotDistance = null;
    target.mRecyclerView = null;
  }
}
