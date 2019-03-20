package com.oscarrrweb.tddboilerplate.domain.interactors.base;

import io.reactivex.Single;

/**
 * The base contract for handling data transfer between layers of the application and implemented
 * in all interactors.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface UseCase<P, R> {

    /**
     * Defined in the implemented class, accepts a parameter from an outer application layer and
     * handles processing to return an object <code>R</code> from a RxJava {@link Single}
     * object. The processing thread, UI thread and observer for the Single can be defined in the
     * returned instance.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Single object
     */
    Single<R> run(P parameter);

    /**
     * While the <code>run(P)</code> method defines the UseCase processing, this method is called
     * from an instance of the implementing UseCase class for execution which calls the
     * <code>run(P)</code> method.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Single object
     */
    Single<R> execute(P parameter);
}