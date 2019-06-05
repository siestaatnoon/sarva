package com.cccdlabs.sarva.presentation.di.modules;

import android.content.Context;

import com.cccdlabs.sarva.data.executor.TestIOThread;
import com.cccdlabs.sarva.data.executor.TestProcessThread;
import com.cccdlabs.sarva.domain.executor.ComputationThread;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.presentation.executor.TestUiThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TestAppModule {

    private final Context context;

    public TestAppModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return context;
    }

    @Provides @Singleton MainThread provideMainThread(TestUiThread uiThread) {
        return uiThread;
    }

    @Provides @Singleton ExecutorThread provideExecutionThread(TestIOThread ioThread) {
        return ioThread;
    }

    @Provides @Singleton ComputationThread provideComputationThread(TestProcessThread processThread) {
        return processThread;
    }
}
