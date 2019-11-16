package com.cccdlabs.sarva.presentation.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.cccdlabs.sarva.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Simplifies the creation of a dismissable or with action {@link Snackbar} in the application
 * to a single static method call.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class SnackbarUtils {

    /**
     * Top/bottom padding adjustment of snackbar
     */
    private static final int VERTICAL_PADDING = 5;

    /**
     * Left/right padding adjustment of snackbar
     */
    private static final int HORIZONTAL_PADDING = 5;

    /**
     * Inner class to implement the {@link View.OnClickListener} to dismiss the snackbar
     * so we don't have to create an anonymous class.
     */
    public static class MessageCloseListener implements View.OnClickListener {

        private Snackbar snackbar;

        public MessageCloseListener(Snackbar snackbar) {
            this.snackbar = snackbar;
        }

        @Override
        public void onClick(View view) {
            snackbar.dismiss();
        }
    }

    /**
     * Creates, displays and returns a dismissable {@link Snackbar} for a notice or confirmation.
     *
     * @param context       The activity context
     * @param view          A {@link View} in the activity, should be contained within a
     *                      {@link CoordinatorLayout} for proper placement
     * @param message       The message to display
     * @param dismissLabel  The label text for the dismiss button
     * @return              The Snackbar
     */
    public static Snackbar notice(@NonNull Context context, @NonNull View view, @NonNull String message,
            String dismissLabel) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        MessageCloseListener listener = new MessageCloseListener(snackbar);
        snackbar.setAction(dismissLabel, listener);
        setStyle(context, snackbar, view);
        snackbar.show();
        return snackbar;
    }

    /**
     * Creates, displays and returns a {@link Snackbar} with a {@link View.OnClickListener}
     * for a custom action to perform on action button click.
     *
     * @param context           The activity context
     * @param view              A {@link View} in the activity, should be contained within a
     *                          {@link CoordinatorLayout} for proper placement
     * @param message           The message to display
     * @param actionLabel       The label text for the action button
     * @param actionListener    A View.OnClickListener implementation
     * @return                  The Snackbar
     */
    public static Snackbar action(@NonNull Context context, @NonNull View view, @NonNull String message,
            @NonNull String actionLabel, @NonNull View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionLabel, actionListener);
        setStyle(context, snackbar, view);
        snackbar.show();
        return snackbar;
    }

    /**
     * Sets a uniform style for the {@link Snackbar} to show anchored to bottom of the screen,
     * no margins, full width and colors set in resources.
     *
     * @param context   The activity context
     * @param snackbar  The snackbar to style
     * @param view      A {@link View} in the activity, should be contained within a
     *                 {@link CoordinatorLayout} for proper placement
     */
    private static void setStyle(Context context, Snackbar snackbar, View view) {
        Resources res = context.getResources();

        View sbView = snackbar.getView();
        ViewGroup.LayoutParams params = sbView.getLayoutParams();

        // Set full horizontal width, no margins and anchor to bottom if snackbar in CoordinatorLayout
        if (params instanceof ViewGroup.MarginLayoutParams) {
            if (params instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams cLayoutParams = (CoordinatorLayout.LayoutParams) params;
                cLayoutParams.gravity = Gravity.BOTTOM;
                cLayoutParams.anchorGravity = Gravity.BOTTOM;
                cLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                cLayoutParams.setMargins(0, 0, 0, 0);
                sbView.setLayoutParams(params);
            } else {
                ViewGroup.MarginLayoutParams mLayoutParams = (ViewGroup.MarginLayoutParams) params;
                mLayoutParams.setMargins(0, 0, 0, 0);
                mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                sbView.setLayoutParams(params);
            }
        }

        // Set snackbar padding
        sbView.setPadding(HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING);

        // Set background color
        sbView.setBackgroundColor(res.getColor(R.color.snackbar_bg_color));

        // Set snackbar text color
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(res.getColor(R.color.snackbar_text_color));

        // Remove button background color and set text color of it
        Button button = sbView.findViewById(com.google.android.material.R.id.snackbar_action);
        button.setBackgroundColor(res.getColor(R.color.snackbar_bg_color));
        snackbar.setActionTextColor(res.getColor(R.color.snackbar_button_color));
    }
}
