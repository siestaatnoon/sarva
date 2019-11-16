package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.domain.interactors.partners.AddPartnerUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.DeletePartnerUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.GetAllPartnersUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerCheckUseCase;
import com.cccdlabs.sarva.domain.interactors.partners.SetPartnerActiveUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.TestActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.TestP2pModule;
import com.cccdlabs.sarva.presentation.di.modules.TestPartnerModule;

import dagger.Component;

@PerActivity
@Component(
        dependencies = {
                TestAppComponent.class
        },
        modules = {
                TestActivityModule.class,
                TestP2pModule.class,
                TestPartnerModule.class
        }
)
public interface TestPartnerComponent extends PartnerComponent {

    PartnerCheckUseCase partnerCheckUseCase();

    AddPartnerUseCase addPartnerUseCase();

    DeletePartnerUseCase deletePartnerUseCase();

    GetAllPartnersUseCase getAllPartnersUseCase();

    SetPartnerActiveUseCase setPartnerActiveUseCase();
}
