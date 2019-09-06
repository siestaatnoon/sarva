package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.ActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.MainModule;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, P2pModule.class, MainModule.class})
public interface MainComponent extends ActivityComponent {

    MainPresenter mainPresenter();

    void inject(MainActivity mainActivity);

    // NOTE: Dagger tip, just add @Inject to MainPresenter constructor
    //       an you won't need this
    // void inject(MainPresenter mainPresenter);
}
