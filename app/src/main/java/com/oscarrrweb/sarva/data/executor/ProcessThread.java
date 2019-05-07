package com.oscarrrweb.sarva.data.executor;

import com.oscarrrweb.sarva.domain.executor.ComputationThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation to provide a large-process thread (via a {@link Scheduler}) for handling
 * files or large network transfers. Annotated with {@link Singleton} to denote a single
 * instance be used by the <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@Singleton
public class ProcessThread implements ComputationThread {

    /**
     * Constructor {@link Inject} annotated for use with Dagger dependency injection.
     *
     * @see <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>
     */
    @Inject
    public ProcessThread() {}

    /**
     * Returns the {@link Scheduler} used for large-process thread tasks.
     *
     * @return The Scheduler
     */
    @Override
    public Scheduler getScheduler() {
        return Schedulers.computation();
    }
}
