package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.oscarrrweb.sarva.presentation.di.PerActivity;
import com.oscarrrweb.sarva.presentation.di.modules.ActivityModule;
import com.oscarrrweb.sarva.presentation.di.modules.MainModule;
import com.oscarrrweb.sarva.presentation.presenters.MainPresenter;
import com.oscarrrweb.sarva.presentation.ui.activities.MainActivity;

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
