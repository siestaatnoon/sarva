package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.presentation.di.modules.TestPresenterModule;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestPresenterModule.class)
public interface TestPresenterComponent {
    void inject(MainPresenter presenter);
    SampleDisplayUseCase sampleDisplayUseCase();
}
