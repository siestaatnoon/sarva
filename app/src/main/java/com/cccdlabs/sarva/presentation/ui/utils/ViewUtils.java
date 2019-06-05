package com.cccdlabs.sarva.presentation.ui.utils;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Utility class for the <code>presentation.ui</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class ViewUtils {

    /**
     * Provides a slide-down animation for an Androir UI {@link View} if not visible or a slide-up
     * animation if a view is visible.
     *
     * @param view      The View to animate
     * @param duration  The time in ms for animation
     */
    public static void toggleSlide(final View view, int duration) {
        if (view == null) {
            return;
        }

        if (view.getVisibility() == View.VISIBLE) {
            final int initialHeight = view.getMeasuredHeight();

            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1){
                        view.setVisibility(View.GONE);
                    } else {
                        view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        view.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            animation.setDuration(duration);
            view.startAnimation(animation);
        } else {
            view.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            final int targetHeight = view.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            view.getLayoutParams().height = 1;
            view.setVisibility(View.VISIBLE);
            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    view.getLayoutParams().height = interpolatedTime == 1
                            ? WindowManager.LayoutParams.WRAP_CONTENT
                            : (int)(targetHeight * interpolatedTime);
                    view.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            animation.setDuration(duration);
            view.startAnimation(animation);
        }
    }
}
