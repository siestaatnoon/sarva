package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.presentation.views.MainView;

import dagger.Module;

@Module
public class TestMainModule extends MainModule {

    public TestMainModule(MainView view) {
        super(view);
    }
}
