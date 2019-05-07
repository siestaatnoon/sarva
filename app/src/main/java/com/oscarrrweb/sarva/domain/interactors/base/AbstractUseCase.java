package com.oscarrrweb.sarva.domain.interactors.base;

import java.util.concurrent.Callable;

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
    abstract public R run(final P parameter) throws Exception;

    /**
     * Provides the default implementation which executes the implemented <code>run(P)</code>
     * method implementation. Subclasses may override this method to modify the returned
     * {@link Single} as seen fit.
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
