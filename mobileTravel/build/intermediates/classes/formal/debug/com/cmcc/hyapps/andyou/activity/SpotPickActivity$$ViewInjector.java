// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SpotPickActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.SpotPickActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427390, "field 'mSearchContent'");
    target.mSearchContent = finder.castView(view, 2131427390, "field 'mSearchContent'");
    view = finder.findRequiredView(source, 2131427672, "field 'mCategory'");
    target.mCategory = view;
  }

  @Override public void reset(T target) {
    target.mSearchContent = null;
    target.mCategory = null;
  }
}
