package com.oscarrrweb.sarva.presentation.di.modules;

import android.content.Context;

import com.oscarrrweb.sarva.data.executor.TestIOThread;
import com.oscarrrweb.sarva.domain.executor.ExecutorThread;
import com.oscarrrweb.sarva.domain.executor.MainThread;
import com.oscarrrweb.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.oscarrrweb.sarva.presentation.executor.TestUiThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestPresenterModule {

    private final Context context;

    public TestPresenterModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return context;
    }

    @Provides @Singleton MainThread provideMainThread() {
        return new TestUiThread();
    }

    @Provides @Singleton ExecutorThread provideExecutorThread() {
        return new TestIOThread();
    }

    @Provides @Singleton SampleDisplayUseCase provideSampleDisplayUseCase() {
        return mock(SampleDisplayUseCase.class);
    }
}
