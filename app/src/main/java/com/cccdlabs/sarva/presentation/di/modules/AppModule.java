package com.cccdlabs.sarva.presentation.di.modules;

import android.content.Context;

import com.cccdlabs.sarva.App;
import com.cccdlabs.sarva.data.executor.IOThread;
import com.cccdlabs.sarva.data.executor.ProcessThread;
import com.cccdlabs.sarva.domain.executor.ComputationThread;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.presentation.executor.UIThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton MainThread provideMainThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides @Singleton ExecutorThread provideExecutorThread(IOThread ioThread) {
        return ioThread;
    }

    @Provides @Singleton ComputationThread provideComputationThread(ProcessThread processThread) {
        return processThread;
    }
}
