package com.cccdlabs.sarva.presentation.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.cccdlabs.sarva.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Simplifies the creation of an {@link AlertDialog} in the application to a single static
 * method call.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class DialogUtils {

    /**
     * Inner class to implement the {@link DialogInterface.OnClickListener} to dismiss a dialog
     * so we don't have to create an anonymous class.
     */
    public static class DialogCloseListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /**
     * Creates and returns a simple dismissible {@link AlertDialog} with a title, message
     * and close button with a default label.
     *
     * @param context   The application context
     * @param title     The dialog title
     * @param message   The dialog message
     * @return          The AlertDialog object
     */
    public static AlertDialog alert(Context context, String title, String message) {
        return alert(context, title, message, null);
    }

    /**
     * Creates and returns a simple dismissible {@link AlertDialog} with a title, message
     * and close button with a custom label.
     *
     * @param context       The application context
     * @param title         The dialog title
     * @param message       The dialog message
     * @param closeLabel    Label to use for close button
     * @return              The AlertDialog object
     */
    public static AlertDialog alert(Context context, @NonNull String title, @NonNull String message, String closeLabel) {
        if (TextUtils.isEmpty(closeLabel)) {
            closeLabel = context.getString(R.string.close);
        }

        DialogCloseListener closeListener = new DialogCloseListener();
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(closeLabel, closeListener)
                .create();
        alertDialog.show();

        return alertDialog;
    }

    /**
     * Creates and returns an {@link AlertDialog} with a custom action along with title, message,
     * close and action button labels.
     *
     * @param context           The application context
     * @param title             The dialog title
     * @param message           The dialog message
     * @param closeLabel        Label to use for close button
     * @param actionLabel       Label to use for action button
     * @param actionListener    Custom {@link DialogInterface.OnClickListener} to perform action
     *                          if action button pressed
     * @return                  The AlertDialog object
     */
    public static AlertDialog confirm(Context context, @NonNull String title, @NonNull String message,
            String closeLabel, String actionLabel, DialogInterface.OnClickListener actionListener) {
        if (TextUtils.isEmpty(closeLabel)) {
            closeLabel = context.getString(R.string.close);
        }

        if (TextUtils.isEmpty(actionLabel)) {
            actionLabel = context.getString(R.string.ok);
        }

        DialogCloseListener closeListener = new DialogCloseListener();
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(closeLabel, closeListener)
                .setPositiveButton(actionLabel, actionListener)
                .create();
        alertDialog.show();

        return alertDialog;
    }
}
