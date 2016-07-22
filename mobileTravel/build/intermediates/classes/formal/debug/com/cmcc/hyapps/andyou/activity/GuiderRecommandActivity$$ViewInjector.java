// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class GuiderRecommandActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.GuiderRecommandActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428173, "field 'tv_recommend'");
    target.tv_recommend = finder.castView(view, 2131428173, "field 'tv_recommend'");
  }

  @Override public void reset(T target) {
    target.tv_recommend = null;
  }
}
