package com.oscarrrweb.sarva.domain.interactors.base;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * An implementation of the {@link UseCase} class that defines the <code>execute(P)</code>
 * method since it would be mostly consistent across all UseCase implemented classes.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
abstract public class AbstractUseCase<P, R> implements UseCase<P, R> {

    /**
     * Constructor.
     */
    public AbstractUseCase() {}

    /**
     * To be defined in subclass, accepts a parameter from an outer application layer and
     * handles processing to return an object <code>R</code> from a RxJava {@link Single}
     * object. The processing thread, UI thread and observer for the Single can be defined in
     * the returned instance.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          <code><R></code> return type
     * @throws          Exception if an error occurs in the subclass use case
     */
    public R run(final P parameter) throws Exception {
        throw new UnsupportedOperationException("Method not used");
    }

    /**
     * Provides the default implementation which executes the implemented <code>run(P)</code>
     * method implementation. Subclasses may override this method to modify the returned
     * {@link Completable} as seen fit. Used for executing tasks only expecting that it has been
     * completed.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Completable object
     */
    @Override
    public Completable complete(final P parameter) {
        return Completable.fromCallable(new Callable<R>() {
            @Override
            public R call() throws Exception {
                return AbstractUseCase.this.run(parameter);
            }
        });
    }

    /**
     * Subclass to define use for asynchronous tasks expecting data transfer over a period of time.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Flowable object
     */
    @Override
    public Flowable<R> emit(final P parameter) {
        throw new UnsupportedOperationException("Method not used");
    }

    /**
     * Provides the default implementation which executes the implemented <code>run(P)</code>
     * method implementation. Subclasses may override this method to modify the returned
     * {@link Single} as seen fit. Used for one-time data transfer.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Single object
     */
    @Override
    public Single<R> execute(final P parameter) {
        return Single.fromCallable(new Callable<R>() {
            @Override
            public R call() throws Exception {
                return AbstractUseCase.this.run(parameter);
            }
        });
    }
}
