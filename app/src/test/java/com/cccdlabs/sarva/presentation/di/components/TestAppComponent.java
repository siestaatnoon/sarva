package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.modules.TestAppModule;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestAppModule.class, TestDataModule.class})
public interface TestAppComponent extends AppComponent {}
