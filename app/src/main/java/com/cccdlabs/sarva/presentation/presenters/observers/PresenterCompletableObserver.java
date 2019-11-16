package com.cccdlabs.sarva.presentation.presenters.observers;

import com.cccdlabs.sarva.presentation.presenters.base.Presenter;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

/**
 * Implementation of an RxJava observer expecting only a confirmation a
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} task has been executed.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class PresenterCompletableObserver extends DisposableCompletableObserver {

    /**
     * The {@link Presenter} utilizing this observer.
     */
    private Presenter presenter;

    /**
     * Constructor.
     *
     * @param presenter     The Presenter utilizing this observer
     */
    public PresenterCompletableObserver(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    abstract public void onComplete();

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
