package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.data.mappers.sample.DoodadMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.presentation.di.Mapper;

import dagger.Module;
import dagger.Provides;

@Module
public class TestMapperModule {

    @Provides @Mapper WidgetMapper provideWidgetMapper() {
        return new WidgetMapper();
    }

    @Provides @Mapper DoodadMapper provideDoodadMapper() {
        return new DoodadMapper();
    }
}
