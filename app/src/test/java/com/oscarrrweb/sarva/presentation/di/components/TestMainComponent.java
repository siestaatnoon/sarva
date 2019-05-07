package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.presentation.di.PerActivity;
import com.oscarrrweb.sarva.presentation.di.modules.TestActivityModule;
import com.oscarrrweb.sarva.presentation.di.modules.TestMainModule;
import com.oscarrrweb.sarva.presentation.presenters.MainPresenter;
import com.oscarrrweb.sarva.presentation.ui.adapters.SampleAdapter;

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
