// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class HotelMapActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.HotelMapActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428156, "field 'mapView'");
    target.mapView = finder.castView(view, 2131428156, "field 'mapView'");
  }

  @Override public void reset(T target) {
    target.mapView = null;
  }
}
