package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.presentation.views.base.PartnerEmitterView;

import dagger.Module;

@Module
public class TestPartnerModule extends PartnerModule {

    public TestPartnerModule(PartnerEmitterView view) {
        super(view);
    }
}
