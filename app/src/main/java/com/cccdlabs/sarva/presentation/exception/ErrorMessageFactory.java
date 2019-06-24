package com.cccdlabs.sarva.presentation.exception;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.R;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PermissionException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.PublishExpiredException;
import com.cccdlabs.sarva.data.p2p.nearby.exception.SubscribeExpiredException;
import com.cccdlabs.sarva.domain.network.base.ApiError;
import com.cccdlabs.sarva.domain.network.exception.NetworkConnectionException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryDeleteException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryInsertException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryQueryException;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryUpdateException;

/**
 * Class to generate a user-friendly error message based on {@link Exception} type.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class ErrorMessageFactory {

    /**
     * Generates an error message based on Exceptions occurring in the application. A String
     * parameter is allowed to add to the message if needed.
     *
     * @param context       Used to access the strings resource file
     * @param throwable     The error object
     * @param strParam      String parameter to include in the error message, may be null
     * @return              The user-friendly error message
     */
    public static String create(@NonNull Context context, Throwable throwable, String strParam) {
        if (throwable == null) {
            return context.getResources().getString(R.string.error_unknown);
        }

        boolean hasParam = strParam != null && !strParam.equals("");
        int resId = R.string.error_unknown;

        if (throwable instanceof NetworkConnectionException) {
            resId = R.string.error_network_connection;
        } else if (throwable instanceof PermissionException) {
            resId = R.string.error_nearby_permission;
        } else if (throwable instanceof PublishExpiredException) {
            resId = R.string.error_nearby_publishing;
        } else if (throwable instanceof SubscribeExpiredException) {
            resId = R.string.error_nearby_subscribing;
        } else if (throwable instanceof RepositoryInsertException) {
            resId = hasParam
                    ? R.string.error_respository_insert
                    : R.string.error_respository_general;
        } else if (throwable instanceof RepositoryUpdateException) {
            resId = hasParam
                    ? R.string.error_respository_update
                    : R.string.error_respository_general;
        } else if (throwable instanceof RepositoryDeleteException) {
            resId = hasParam
                    ? R.string.error_respository_delete
                    : R.string.error_respository_general;
        } else if (throwable instanceof RepositoryQueryException) {
            resId = hasParam
                    ? R.string.error_respository_query
                    : R.string.error_respository_general;
        } else if (throwable instanceof ApiError) {
            ApiError apiError = (ApiError) throwable;
            if (apiError.isAuthFailureError()) {
                resId = R.string.error_authentication;
            } else if (apiError.isNetworkError()) {
                resId = R.string.error_network_server;
            } else if (apiError.isTimeoutError()) {
                resId = R.string.error_network_timeout;
            } else if (apiError.isNoConnectionError()) {
                resId = R.string.error_network_connection;
            }
        }

        String message = context.getResources().getString(resId);
        return hasParam ? String.format(message, strParam) : message;
    }

    /**
     * Generates an error message based on Exceptions occurring in the application.
     *
     * @param context       Used to access the strings resource file
     * @param throwable     The error object
     * @return              The user-friendly error message
     */
    public static String create(@NonNull Context context, Throwable throwable) {
        return create(context, throwable, null);
    }
}
