package com.oscarrrweb.sarva.presentation.executor;

import com.oscarrrweb.sarva.domain.executor.MainThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Implementation to provide the Android UI thread (via a {@link Scheduler}) for handling
 * normal typical processes in the <code>presentation</code> package. Annotated with
 * {@link Singleton} to denote a single instance be used by the
 * <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
@Singleton
public class UIThread implements MainThread {

    /**
     * Constructor {@link Inject} annotated for use with Dagger dependency injection.
     *
     * @see <a href="https://google.github.io/dagger/" target="_top">Dagger framework</a>
     */
    @Inject
    public UIThread() {}

    /**
     * Returns the {@link Scheduler} used for the main UI thread tasks.
     *
     * @return The Scheduler
     */
    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
