package com.cccdlabs.sarva.domain.executor.base;

import io.reactivex.Scheduler;

/**
 * Base thread abstraction via a RxJava scheduler.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface ThreadScheduler {

    /**
     * Provides the "scheduler" or thread to perform tasks within the application.
     *
     * @return The scheduler
     * @see {@link Scheduler}
     */
    Scheduler getScheduler();
}
