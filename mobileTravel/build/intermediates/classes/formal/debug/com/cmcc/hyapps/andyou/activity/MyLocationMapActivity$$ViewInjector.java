// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MyLocationMapActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.MyLocationMapActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428156, "field 'mMapView'");
    target.mMapView = finder.castView(view, 2131428156, "field 'mMapView'");
    view = finder.findRequiredView(source, 2131428159, "field 'mShowMyLocation'");
    target.mShowMyLocation = view;
  }

  @Override public void reset(T target) {
    target.mMapView = null;
    target.mShowMyLocation = null;
  }
}
