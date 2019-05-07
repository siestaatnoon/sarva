package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.presentation.di.modules.TestDataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestDataModule.class)
public interface TestDataComponent extends DataComponent {}
