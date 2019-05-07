package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.oscarrrweb.sarva.presentation.di.modules.TestPresenterModule;
import com.oscarrrweb.sarva.presentation.presenters.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestPresenterModule.class)
public interface TestPresenterComponent {
    void inject(MainPresenter presenter);
    SampleDisplayUseCase sampleDisplayUseCase();
}
