package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.presentation.di.Mapper;
import com.cccdlabs.sarva.presentation.di.modules.TestMapperModule;

import dagger.Component;

@Mapper
@Component(modules = TestMapperModule.class)
public interface TestMapperComponent {

    void inject(GizmoMapper mapper);
    void inject(WidgetMapper mapper);
    GizmoMapper gizmoMapper();
    WidgetMapper widgetMapper();
}
