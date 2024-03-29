package com.cccdlabs.sarva.presentation.executor;

import com.cccdlabs.sarva.domain.executor.MainThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;

@Singleton
public class TestUiThread implements MainThread {

    @Inject
    public TestUiThread() {}

    @Override
    public Scheduler getScheduler() {
        return new TestScheduler();
    }
}
