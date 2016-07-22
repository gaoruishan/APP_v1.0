// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class CityScenicListActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.CityScenicListActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427655, "field 'serviceBtn'");
    target.serviceBtn = finder.castView(view, 2131427655, "field 'serviceBtn'");
  }

  @Override public void reset(T target) {
    target.serviceBtn = null;
  }
}
