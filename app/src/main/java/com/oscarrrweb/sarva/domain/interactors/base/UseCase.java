package com.oscarrrweb.sarva.domain.interactors.base;

import io.reactivex.Completable;
import io.reactivex.Flowable;
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
     * @return          <code><R></code> return type
     * @throws          Exception if an error occurs in the implementing use case
     */
    R run(P parameter) throws Exception;

    /**
     * While the <code>run(P)</code> method defines the UseCase processing, this method is called
     * from an instance of the implementing UseCase class for execution which calls the
     * <code>run(P)</code> method. Used for executing tasks only expecting that it has been
     * completed.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Completable object
     */
    Completable complete(P parameter) throws Exception;

    /**
     * While the <code>run(P)</code> method defines the UseCase processing, this method is called
     * from an instance of the implementing UseCase class for execution which calls the
     * <code>run(P)</code> method. Used for asynchronous tasks expecting data transfer
     * over a period of time.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Flowable object
     */
    Flowable<R> emit(P parameter) throws Exception;

    /**
     * While the <code>run(P)</code> method defines the UseCase processing, this method is called
     * from an instance of the implementing UseCase class for execution which calls the
     * <code>run(P)</code> method. Used for one-time data transfer.
     *
     * @param parameter The generic type parameter passed to the UseCase to process
     * @return          An RxJava Single object
     */
    Single<R> execute(P parameter) throws Exception;
}