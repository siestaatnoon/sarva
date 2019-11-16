package com.cccdlabs.sarva.presentation.ui.behaviors;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

/**
 * Overrides the {@link CoordinatorLayout.Behavior} allowing a
 * {@link com.google.android.material.bottomnavigation.BottomNavigationView} to scroll out of view
 * on a view scrolldown and reappear upon scrolling up. Also allows a {@link Snackbar} to anchor
 * to the top of the BottomNavigationView and stay anchored until the BottomNavigationView
 * is out of view, in which the Snackbar is anchored to the screen bottom.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<View> {

    /**
     * Constructor.
     *
     * @param context   The activity context
     * @param attrs     Attribute parameters to appear on the child view tag
     */
    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Returns true if only vertical scrolling.
     *
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param axes
     * @param type
     * @return
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
            @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    /**
     * Sets the translationY of the BottomNavigationView to move to the bottom out of view by
     * the height of the initial scrolling.
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     * @param type
     * @param consumed
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
            @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
            int type, @NonNull int[] consumed) {
        float translationY = Math.max(0f, Math.min(child.getHeight(), child.getTranslationY() + dyConsumed));
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        child.setTranslationY(translationY);
    }

    /**
     * Handles the case where a Snackbar is shown; will be anchored above the BottomNavigationView
     * and move with the BottomNavigationView until the navigation is out of view.
     *
     * @param parent
     * @param child
     * @param dependency
     * @return boolean
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    /**
     * Anchors a Snackbar, if shown, to the BottomNavigationView.
     *
     * @param child     The BottomNavigationView
     * @param layout    The Snackbar layout
     */
    private void updateSnackbar(View child, Snackbar.SnackbarLayout layout) {
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) params;
            layoutParams.setAnchorId(child.getId());
            layoutParams.anchorGravity = Gravity.TOP;
            layoutParams.gravity = Gravity.TOP;
            layout.setLayoutParams(params);
        }
    }
}
