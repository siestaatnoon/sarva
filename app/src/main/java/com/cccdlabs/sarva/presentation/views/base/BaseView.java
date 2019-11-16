package com.cccdlabs.sarva.presentation.views.base;

import android.content.Context;

/**
 * Contract implemented by Activities for interaction with the presenters of this package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface BaseView {

    /**
     * Get a {@link Context}.
     */
    Context context();

    /**
     * Show an error message
     *
     * @param throwable The exception thrown.
     */
    void showError(Throwable throwable);

    /**
     * Show a view with a progress bar indicating a loading process.
     */
    void showLoading();

    /**
     * Hide a loading view.
     */
    void hideLoading();

    /**
     * Show a retry view in case of an error when retrieving data.
     */
    void showRetry();

    /**
     * Hide a retry view shown if there was an error when retrieving data.
     */
    void hideRetry();
}
