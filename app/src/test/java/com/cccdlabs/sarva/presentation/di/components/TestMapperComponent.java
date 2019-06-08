package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.presentation.di.Mapper;
import com.cccdlabs.sarva.presentation.di.modules.TestMapperModule;

import dagger.Component;

@Mapper
@Component(modules = TestMapperModule.class)
public interface TestMapperComponent {

    void inject(PartnerMapper mapper);

    PartnerMapper partnerMapper();
}
