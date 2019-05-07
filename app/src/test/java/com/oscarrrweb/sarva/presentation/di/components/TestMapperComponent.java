package com.oscarrrweb.sarva.presentation.di.components;

import com.oscarrrweb.sarva.data.mappers.sample.GizmoMapper;
import com.oscarrrweb.sarva.data.mappers.sample.WidgetMapper;
import com.oscarrrweb.sarva.presentation.di.Mapper;
import com.oscarrrweb.sarva.presentation.di.modules.TestMapperModule;

import dagger.Component;

@Mapper
@Component(modules = TestMapperModule.class)
public interface TestMapperComponent {

    void inject(GizmoMapper mapper);
    void inject(WidgetMapper mapper);
    GizmoMapper gizmoMapper();
    WidgetMapper widgetMapper();
}
