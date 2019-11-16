package com.cccdlabs.sarva.presentation.ui.behaviors;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

/**
 * Overrides the {@link CoordinatorLayout.Behavior} allowing a
 * {@link com.google.android.material.floatingactionbutton.FloatingActionButton} to move along
 * with a {@link com.google.android.material.bottomnavigation.BottomNavigationView} scrolling
 * out of view upon scrolldown. Used in conjunction with {@link BottomNavigationBehavior}.
 *
 * @author Johnny Spence
 * @version 1.0.0
 * @see BottomNavigationBehavior
 */
public class BottomNavigationFabBehavior extends CoordinatorLayout.Behavior<View> {

    /**
     * Constructor.
     *
     * @param context   The activity context
     * @param attrs     Attribute parameters to appear on the child view tag
     */
    public BottomNavigationFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Handles the case where a Snackbar is shown; FloatingActionButton will be anchored above the
     * Snackbar and move with the Snackbar + BottomNavigationView until the navigation is out of
     * view.
     *
     * @param parent
     * @param child
     * @param dependency
     * @return boolean
     */
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    /**
     * Handles the case where a Snackbar is dismissed; FloatingActionButton will be then anchored
     * above the BottomNavigationView and move with it until the navigation is out of view.
     *
     * @param parent
     * @param child
     * @param dependency
     * @return boolean
     */
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float oldTranslation = child.getTranslationY();
            float height = dependency.getHeight();
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);
            return oldTranslation != newTranslation;
        }
        return false;
    }

    /**
     * If a Snackbar is dismissed, resets the translationY on the FloatingActionButton.
     *
     * @param parent
     * @param child
     * @param dependency
     */
    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull View child,
            @NonNull View dependency) {
        child.setTranslationY(0f);
        super.onDependentViewRemoved(parent, child, dependency);
    }
}
