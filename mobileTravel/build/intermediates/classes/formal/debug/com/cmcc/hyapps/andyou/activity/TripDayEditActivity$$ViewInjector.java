// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class TripDayEditActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.TripDayEditActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427399, "field 'mRootContainer'");
    target.mRootContainer = finder.castView(view, 2131427399, "field 'mRootContainer'");
    view = finder.findRequiredView(source, 2131427543, "field 'mTitle' and method 'toggleDays'");
    target.mTitle = finder.castView(view, 2131427543, "field 'mTitle'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.toggleDays();
        }
      });
    view = finder.findRequiredView(source, 2131428006, "field 'mFinish' and method 'sendTripDay'");
    target.mFinish = finder.castView(view, 2131428006, "field 'mFinish'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sendTripDay();
        }
      });
    view = finder.findRequiredView(source, 2131427373, "field 'mTime' and method 'pickDate'");
    target.mTime = finder.castView(view, 2131427373, "field 'mTime'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.pickDate();
        }
      });
    view = finder.findRequiredView(source, 2131427480, "field 'mContent'");
    target.mContent = finder.castView(view, 2131427480, "field 'mContent'");
    view = finder.findRequiredView(source, 2131427676, "field 'mContentLength'");
    target.mContentLength = finder.castView(view, 2131427676, "field 'mContentLength'");
    view = finder.findRequiredView(source, 2131427370, "field 'mImage' and method 'popupPickPhotoMenu'");
    target.mImage = finder.castView(view, 2131427370, "field 'mImage'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.popupPickPhotoMenu();
        }
      });
    view = finder.findRequiredView(source, 2131427677, "field 'mScenicName'");
    target.mScenicName = finder.castView(view, 2131427677, "field 'mScenicName'");
    view = finder.findRequiredView(source, 2131427675, "field 'edit_ll'");
    target.edit_ll = finder.castView(view, 2131427675, "field 'edit_ll'");
    view = finder.findRequiredView(source, 2131427674, "field 'my_strategy_desc'");
    target.my_strategy_desc = finder.castView(view, 2131427674, "field 'my_strategy_desc'");
    view = finder.findRequiredView(source, 2131427541, "method 'back'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.back();
        }
      });
    view = finder.findRequiredView(source, 2131427678, "method 'pickScenic'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.pickScenic();
        }
      });
  }

  @Override public void reset(T target) {
    target.mRootContainer = null;
    target.mTitle = null;
    target.mFinish = null;
    target.mTime = null;
    target.mContent = null;
    target.mContentLength = null;
    target.mImage = null;
    target.mScenicName = null;
    target.edit_ll = null;
    target.my_strategy_desc = null;
  }
}
