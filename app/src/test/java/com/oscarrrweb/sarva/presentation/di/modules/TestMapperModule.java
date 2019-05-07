package com.oscarrrweb.sarva.presentation.di.modules;

import com.oscarrrweb.sarva.data.mappers.sample.DoodadMapper;
import com.oscarrrweb.sarva.data.mappers.sample.WidgetMapper;
import com.oscarrrweb.sarva.presentation.di.Mapper;

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
