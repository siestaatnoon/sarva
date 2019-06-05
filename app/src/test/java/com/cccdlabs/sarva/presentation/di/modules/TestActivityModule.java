package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;

import dagger.Module;

@Module
public class TestActivityModule extends ActivityModule {

    public TestActivityModule(Activity activity) {
        super(activity);
    }
}
