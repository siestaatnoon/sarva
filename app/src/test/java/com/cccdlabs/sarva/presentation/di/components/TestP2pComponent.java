package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.TestP2pModule;

import dagger.Component;

@PerActivity
@Component(dependencies = {TestAppComponent.class}, modules = {TestActivityModule.class, TestP2pModule.class})
public interface TestP2pComponent extends P2pComponent {
}
