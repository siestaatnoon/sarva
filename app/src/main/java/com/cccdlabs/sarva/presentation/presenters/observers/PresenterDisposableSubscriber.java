package com.cccdlabs.sarva.presentation.presenters.observers;

import android.content.Context;

import com.cccdlabs.sarva.presentation.exception.ErrorMessageFactory;
import com.cccdlabs.sarva.presentation.presenters.base.Presenter;

import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

/**
 * Implementation of an RxJava Subcriber allowing for asynchronous cancellation by an emitter
 * source.
 *
 * @author Johnny Spence
 * @version 1.0.0
 * @param <T>   Object type being emitted corresponding {@link io.reactivex.Observable} or
 *              {@link io.reactivex.Flowable}
 */
abstract public class PresenterDisposableSubscriber<T> extends DisposableSubscriber<T> {

    /**
     * The {@link Presenter} utilizing this subscriber.
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
     * @param presenter     The Presenter utilizing this subscriber
     */
    public PresenterDisposableSubscriber(Context context, Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    /**
     * Listener that returns the data from a successful UseCase call.
     *
     * @param param The data returned from UseCase call
     */
    @Override
    abstract public void onNext(T param);

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
