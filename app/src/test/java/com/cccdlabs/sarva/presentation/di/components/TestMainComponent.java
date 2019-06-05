package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.TestMainModule;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.ui.adapters.SampleAdapter;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                TestAppComponent.class
        },
        modules = {
                TestActivityModule.class,
                TestMainModule.class
        }
)
public interface TestMainComponent extends MainComponent {

    MainPresenter mainPresenter();

    SampleAdapter sampleAdapter();
}
