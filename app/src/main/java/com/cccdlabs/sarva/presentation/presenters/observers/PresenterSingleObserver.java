package com.cccdlabs.sarva.presentation.presenters.observers;

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
     * Constructor.
     *
     * @param presenter     The Presenter utilizing this observable
     */
    public PresenterSingleObserver(Presenter presenter) {
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
        presenter.onError(throwable);
    }
}
