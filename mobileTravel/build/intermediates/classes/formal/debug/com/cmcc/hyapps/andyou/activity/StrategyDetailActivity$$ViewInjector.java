// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class StrategyDetailActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.StrategyDetailActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427666, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131427666, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131427482, "field 'tv_comment'");
    target.tv_comment = finder.castView(view, 2131427482, "field 'tv_comment'");
    view = finder.findRequiredView(source, 2131427481, "field 'tv_praise'");
    target.tv_praise = finder.castView(view, 2131427481, "field 'tv_praise'");
  }

  @Override public void reset(T target) {
    target.mRecyclerView = null;
    target.tv_comment = null;
    target.tv_praise = null;
  }
}
