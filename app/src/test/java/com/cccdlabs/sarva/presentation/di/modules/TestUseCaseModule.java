package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.data.repository.sample.WidgetRepository;
import com.cccdlabs.sarva.data.storage.dao.sample.GizmoDao;
import com.cccdlabs.sarva.data.storage.dao.sample.WidgetDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestUseCaseModule {

    public TestUseCaseModule() {}

    @Provides @Singleton GizmoRepository provideGizmoRepository() {
        return mock(GizmoRepository.class);
    }

    @Provides @Singleton WidgetRepository provideWidgetRepository() {
        return mock(WidgetRepository.class);
    }

    @Provides @Singleton GizmoMapper provideGizmoMapper() {
        return mock(GizmoMapper.class);
    }

    @Provides @Singleton WidgetMapper provideWidgetMapper() {
        return mock(WidgetMapper.class);
    }

    @Provides @Singleton GizmoDao provideGizmoDao() {
        return mock(GizmoDao.class);
    }

    @Provides @Singleton WidgetDao provideWidgetDao() {
        return mock(WidgetDao.class);
    }
}
