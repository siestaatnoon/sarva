package com.oscarrrweb.sarva.data.executor;

import com.oscarrrweb.sarva.domain.executor.ExecutorThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation to provide an input/output thread (via a {@link Scheduler}) for handling
 * normal typical processes in the <code>data</code> package. Annotated with {@link Singleton}
 * to denote a single instance be used by the
 * <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@Singleton
public class IOThread implements ExecutorThread {

    /**
     * Constructor {@link Inject} annotated for use with Dagger dependency injection.
     *
     * @see <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>
     */
    @Inject
    public IOThread() {}

    /**
     * Returns the {@link Scheduler} used for input/output thread tasks.
     *
     * @return The Scheduler
     */
    @Override
    public Scheduler getScheduler() {
        return Schedulers.io();
    }
}
