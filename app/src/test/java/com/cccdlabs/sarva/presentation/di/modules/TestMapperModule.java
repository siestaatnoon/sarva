package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.presentation.di.Mapper;

import dagger.Module;
import dagger.Provides;

@Module
public class TestMapperModule {
    @Provides @Mapper PartnerMapper providePartnerMapper() {
        return new PartnerMapper();
    }
}
