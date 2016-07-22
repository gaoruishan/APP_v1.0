// Generated code from Butter Knife. Do not modify!
package com.cmcc.hyapps.andyou.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class LoginActivity$$ViewInjector<T extends com.cmcc.hyapps.andyou.activity.LoginActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427542, "field 'progressBar'");
    target.progressBar = finder.castView(view, 2131427542, "field 'progressBar'");
    view = finder.findRequiredView(source, 2131427901, "field 'phoneEditText' and method 'onPhoneChanged'");
    target.phoneEditText = finder.castView(view, 2131427901, "field 'phoneEditText'");
    ((android.widget.TextView) view).addTextChangedListener(
      new android.text.TextWatcher() {
        @Override public void onTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          target.onPhoneChanged(p0);
        }
        @Override public void beforeTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          
        }
        @Override public void afterTextChanged(
          android.text.Editable p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427905, "field 'sendValidCode' and method 'sendValidCode'");
    target.sendValidCode = finder.castView(view, 2131427905, "field 'sendValidCode'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sendValidCode();
        }
      });
    view = finder.findRequiredView(source, 2131427904, "field 'confirmCodeEditText' and method 'onConfirmCodeChanged'");
    target.confirmCodeEditText = finder.castView(view, 2131427904, "field 'confirmCodeEditText'");
    ((android.widget.TextView) view).addTextChangedListener(
      new android.text.TextWatcher() {
        @Override public void onTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          target.onConfirmCodeChanged(p0);
        }
        @Override public void beforeTextChanged(
          java.lang.CharSequence p0,
          int p1,
          int p2,
          int p3
        ) {
          
        }
        @Override public void afterTextChanged(
          android.text.Editable p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427544, "field 'activiteButton' and method 'login'");
    target.activiteButton = finder.castView(view, 2131427544, "field 'activiteButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.login();
        }
      });
    view = finder.findRequiredView(source, 2131427541, "method 'onBackClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onBackClick();
        }
      });
  }

  @Override public void reset(T target) {
    target.progressBar = null;
    target.phoneEditText = null;
    target.sendValidCode = null;
    target.confirmCodeEditText = null;
    target.activiteButton = null;
  }
}
