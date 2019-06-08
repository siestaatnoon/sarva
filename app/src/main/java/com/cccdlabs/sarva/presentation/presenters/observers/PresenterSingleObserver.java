package com.cccdlabs.sarva.presentation.presenters.observers;

import android.content.Context;

import com.cccdlabs.sarva.presentation.exception.ErrorMessageFactory;
import com.cccdlabs.sarva.presentation.presenters.base.Presenter;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * RxJava observer class that subscribes to {@link com.cccdlabs.sarva.domain.interactors.base.UseCase}
 * calls in the <code>data</code> package.
 *
 * @param <T> The returned data object
 */
abstract public class PresenterSingleObserver<T> extends DisposableSingleObserver<T> {

    /**
     * The {@link Presenter} utilizing this observable.
     */
    private Presenter presenter;

    /**
     * The Android context for generating an error message.
     */
    private Context context;

    /**
     * Constructor.
     *
     * @param context       The Android context
     * @param presenter     The Presenter utilizing this observable
     */
    public PresenterSingleObserver(Context context, Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    /**
     * Listener that returns the data from a successful UseCase call.
     *
     * @param param The data returned from UseCase call
     */
    @Override
    abstract public void onSuccess(T param);

    /**
     * Error listener that returns a user-friendly error message to the Presenter
     * <code>onError()</code> listener.
     *
     * @param throwable The error object that occurs in the UseCase call
     */
    @Override
    public void onError(final Throwable throwable) {
        Timber.e(throwable);
        presenter.onError(getErrorMessage(throwable));
    }

    /**
     * Returns a user-friendly error message.
     *
     * @param throwable The error object that occurs in the UseCase call
     */
    private String getErrorMessage(final Throwable throwable) {
        return ErrorMessageFactory.create(context, throwable);
    }
}
