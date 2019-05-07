package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.presentation.di.modules.TestAppModule;
import com.oscarrrweb.sarva.presentation.di.modules.TestDataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestAppModule.class, TestDataModule.class})
public interface TestAppComponent extends AppComponent {}
