/**
 *
 */

package com.cmcc.hyapps.andyou.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * @author kuloud
 */
public class AnimUtils {
    private static final long DURATION_DEFAULT = 500L;

    public static void flipit(final View visView, final View invisView) {
        AnimUtils.flipit(visView, invisView, DURATION_DEFAULT, new AccelerateInterpolator(),
                new DecelerateInterpolator());
    }

    public static void flipit(final View visView, final View invisView, long duration,
            Interpolator accelerator,
            Interpolator decelerator) {
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visView, "rotationY", 0f, 90f);
        visToInvis.setDuration(duration);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisView, "rotationY", -90f, 0f);
        invisToVis.setDuration(duration);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visView.setVisibility(View.GONE);
                invisToVis.start();
                invisView.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
    }

    public static void fadeLeft(View view) {
        AnimUtils.fadeLeft(view, DURATION_DEFAULT, new AccelerateInterpolator());
    }

    public static void fadeLeft(View view, long duration, Interpolator accelerator) {
        ObjectAnimator translationLeft = ObjectAnimator
                .ofFloat(view, "X", 0f, 0f - view.getWidth());
        translationLeft.setDuration(duration);
        translationLeft.setInterpolator(accelerator);
        ValueAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(duration);
        animator.setInterpolator(accelerator);
        translationLeft.start();
        animator.start();
    }

    public static void scaleHeight(View view, int height) {
        AnimUtils.scaleHeight(view, height, DURATION_DEFAULT, new AccelerateInterpolator());
    }

    public static void scaleHeight(View view, int height, long duration, Interpolator accelerator) {
        ScaleHeightAnimation scalAnim = new ScaleHeightAnimation(view, height);
        scalAnim.setDuration(duration);
        scalAnim.setInterpolator(accelerator);
        view.startAnimation(scalAnim);
    }

    static class ScaleHeightAnimation extends Animation {
        private final View mView;
        private final int mStartHeight;
        private final int mDeltaHeight;

        public ScaleHeightAnimation(View view, int height) {
            mView = view;
            mStartHeight = mView.getHeight();
            mDeltaHeight = height - mStartHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            android.view.ViewGroup.LayoutParams lp = mView.getLayoutParams();
            lp.height = (int) (mStartHeight + mDeltaHeight * interpolatedTime);
            mView.setLayoutParams(lp);
        }
    }

    /**
     * Do switch without stretch view height
     * 
     * @param hideView
     * @param showView
     */
    public static void toggleBottomViews(final View hideView, final View showView,
            final int viewHeight) {
        toggleBottomViews(hideView, viewHeight, showView, viewHeight);
    }

    /**
     * Toggle bottom views by animation
     * 
     * @param hideView View to hide
     * @param hideViewHeight Begin height of hide view
     * @param showView View to display
     * @param showViewHeight End height of display view
     */
    public static void toggleBottomViews(final View hideView, final int hideViewHeight,
            final View showView,
            final int showViewHeight) {
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(hideView, "translationY", 0,
                hideViewHeight);
        hideAnimator.setInterpolator(new AccelerateInterpolator());
        hideAnimator.setDuration(DURATION_DEFAULT);
        hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideView.setVisibility(View.GONE);
                showView.setVisibility(View.VISIBLE);
            }
        });
        if (hideViewHeight > showViewHeight) {
            hideAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float height = (Float) animation.getAnimatedValue();
                    if (height <= hideViewHeight - showViewHeight) {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) hideView
                                .getLayoutParams();
                        params.height = (int) (hideViewHeight - height);
                        hideView.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) hideView
                                .getLayoutParams();
                        params.height = showViewHeight;
                        hideView.setLayoutParams(params);
                    }
                }
            });
        }

        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(showView, "translationY",
                showViewHeight, 0);
        showAnimator.setInterpolator(new DecelerateInterpolator());
        showAnimator.setDuration(DURATION_DEFAULT);
        if (hideViewHeight < showViewHeight) {
            showAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float height = (Float) animation.getAnimatedValue();
                    if (height <= showViewHeight - hideViewHeight) {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) showView
                                .getLayoutParams();
                        params.height = (int) (showViewHeight - height);
                        showView.setLayoutParams(params);
                    } else {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) showView
                                .getLayoutParams();
                        params.height = hideViewHeight;
                        showView.setLayoutParams(params);
                    }
                }
            });
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(hideAnimator);
        animatorSet.play(showAnimator).after(hideAnimator);
        animatorSet.start();
    }

    /**
     * Toggle bottom views by animation
     * 
     * @param hideView View to hide
     * @param showView View to display
     */
    public static void toggleBottomViews(final View hideView, final View showView) {
        ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(hideView, "translationY", 0,
                hideView.getHeight());
        hideAnimator.setInterpolator(new AccelerateInterpolator());
        hideAnimator.setDuration(DURATION_DEFAULT);
        hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                hideView.setVisibility(View.GONE);
                showView.setVisibility(View.VISIBLE);
            }
        });

        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(showView, "translationY",
                showView.getHeight(), 0);
        showAnimator.setInterpolator(new DecelerateInterpolator());
        showAnimator.setDuration(DURATION_DEFAULT);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(hideAnimator);
        animatorSet.play(showAnimator).after(hideAnimator);
        animatorSet.start();
    }

    public static void doScaleFadeAnim(View v) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.1f, 1.3f, 1.0f);
        scaleX.setDuration(DURATION_DEFAULT);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.1f, 1.3f, 1.0f);
        scaleY.setDuration(DURATION_DEFAULT);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0.5f, 0.2f, 1.0f);
        alpha.setDuration(DURATION_DEFAULT);
        AnimatorSet animatorSetScale = new AnimatorSet();
        animatorSetScale.setInterpolator(new LinearInterpolator());
        animatorSetScale.playTogether(scaleX, scaleY, alpha);
        animatorSetScale.start();
    }
}
