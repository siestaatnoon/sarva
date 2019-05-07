package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.data.mappers.sample.GizmoMapper;
import com.oscarrrweb.sarva.data.repository.sample.GizmoRepository;
import com.oscarrrweb.sarva.domain.interactors.sample.SampleDisplayUseCase;
import com.oscarrrweb.sarva.presentation.di.modules.TestUseCaseModule;

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
