package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.cccdlabs.sarva.presentation.di.modules.TestUseCaseModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestUseCaseModule.class)
public interface TestUseCaseComponent {
    void inject(SampleDisplayUseCase useCase);
    void inject(GizmoRepository repository);
    void inject(GizmoMapper mapper);

    GizmoRepository gizmoRepository();
    GizmoMapper gizmoMapper();
}
