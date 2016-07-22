// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class VideoRecorderActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.VideoRecorderActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427632, "method 'onTouchEvent'");
    view.setOnTouchListener(
      new android.view.View.OnTouchListener() {
        @Override public boolean onTouch(
          android.view.View p0,
          android.view.MotionEvent p1
        ) {
          return target.onTouchEvent(p1);
        }
      });
  }

  @Override public void reset(T target) {
  }
}
