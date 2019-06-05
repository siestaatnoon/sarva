package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = TestAppComponent.class, modules = TestActivityModule.class)
public interface TestActivityComponent extends ActivityComponent {}
