package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.ActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;
import com.cccdlabs.sarva.presentation.di.modules.PartnerModule;
import com.cccdlabs.sarva.presentation.presenters.partners.PartnerCheckPresenter;
import com.cccdlabs.sarva.presentation.ui.activities.partners.PartnerCheckActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, P2pModule.class, PartnerModule.class})
public interface PartnerComponent {

    PartnerCheckPresenter partnerCheckPresenter();

    void inject(PartnerCheckActivity activity);
}
