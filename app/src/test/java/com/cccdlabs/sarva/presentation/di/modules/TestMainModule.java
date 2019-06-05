package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.adapters.SampleAdapter;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class TestMainModule {

    public TestMainModule() {}

    @Provides @PerActivity SampleDisplayUseCase provideSampleDisplayUseCase() {
        return Mockito.mock(SampleDisplayUseCase.class);
    }

    @Provides @PerActivity MainPresenter provideMainPresenter() {
        return Mockito.mock(MainPresenter.class);
    }

    @Provides @PerActivity SampleAdapter provideSampleAdapter() {
        return Mockito.mock(SampleAdapter.class);
    }
}
