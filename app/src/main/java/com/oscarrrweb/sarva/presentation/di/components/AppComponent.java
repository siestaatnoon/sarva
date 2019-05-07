package com.oscarrrweb.sarva.presentation.di.components;

import android.content.Context;

import com.oscarrrweb.sarva.data.mappers.sample.DoodadMapper;
import com.oscarrrweb.sarva.data.mappers.sample.GizmoMapper;
import com.oscarrrweb.sarva.data.mappers.sample.WidgetMapper;
import com.oscarrrweb.sarva.data.network.retrofit.RestCaller;
import com.oscarrrweb.sarva.data.repository.sample.DoodadRepository;
import com.oscarrrweb.sarva.data.repository.sample.GizmoRepository;
import com.oscarrrweb.sarva.data.repository.sample.WidgetRepository;
import com.oscarrrweb.sarva.data.storage.dao.sample.DoodadDao;
import com.oscarrrweb.sarva.data.storage.dao.sample.GizmoDao;
import com.oscarrrweb.sarva.data.storage.dao.sample.WidgetDao;
import com.oscarrrweb.sarva.data.storage.database.AppDatabase;
import com.oscarrrweb.sarva.domain.executor.ComputationThread;
import com.oscarrrweb.sarva.domain.executor.ExecutorThread;
import com.oscarrrweb.sarva.domain.executor.MainThread;
import com.oscarrrweb.sarva.presentation.di.modules.AppModule;
import com.oscarrrweb.sarva.presentation.di.modules.DataModule;
import com.oscarrrweb.sarva.presentation.ui.activities.base.BaseAppCompatActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Lazy;

@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {

    void inject(BaseAppCompatActivity activity);

    void inject(RestCaller restCaller);

    Context context();

    MainThread mainThread();

    ExecutorThread executorThread();

    Lazy<ComputationThread> computationThread();

    AppDatabase appDatabase();

    /* SAMPLE USAGE BELOW */

    void inject(WidgetMapper mapper);
    void inject(WidgetRepository repository);
    void inject(GizmoMapper mapper);
    void inject(GizmoRepository repository);
    void inject(DoodadMapper mapper);
    void inject(DoodadRepository repository);

    GizmoRepository gizmoRepository();
    GizmoMapper gizmoMapper();
    GizmoDao gizmoDao();

    WidgetRepository widgetRepository();
    WidgetMapper widgetMapper();
    WidgetDao widgetDao();

    DoodadRepository doodadRepository();
    DoodadMapper doodadMapper();
    DoodadDao doodadDao();
}
