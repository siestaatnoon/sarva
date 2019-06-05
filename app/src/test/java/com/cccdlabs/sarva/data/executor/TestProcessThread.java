package com.cccdlabs.sarva.data.executor;

import com.cccdlabs.sarva.domain.executor.ComputationThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.TestScheduler;

@Singleton
public class TestProcessThread implements ComputationThread {

    @Inject
    public TestProcessThread() {}

    @Override
    public Scheduler getScheduler() {
        return new TestScheduler();
    }
}
