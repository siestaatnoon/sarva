package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.ActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.MainModule;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.MainActivity;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                AppComponent.class
        },
        modules = {
                ActivityModule.class,
                MainModule.class
        }
)
public interface MainComponent extends ActivityComponent {

    SampleDisplayUseCase sampleDisplayUseCase();

    void inject(MainActivity mainActivity);

    void inject(MainPresenter mainPresenter);

    void inject(SampleDisplayUseCase useCase);
}
