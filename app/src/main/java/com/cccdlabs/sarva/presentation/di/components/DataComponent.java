package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.data.mappers.sample.DoodadMapper;
import com.cccdlabs.sarva.data.mappers.sample.GizmoMapper;
import com.cccdlabs.sarva.data.mappers.sample.WidgetMapper;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.data.repository.sample.DoodadRepository;
import com.cccdlabs.sarva.data.repository.sample.GizmoRepository;
import com.cccdlabs.sarva.data.repository.sample.WidgetRepository;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.data.storage.dao.sample.DoodadDao;
import com.cccdlabs.sarva.data.storage.dao.sample.GizmoDao;
import com.cccdlabs.sarva.data.storage.dao.sample.WidgetDao;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.presentation.di.modules.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DataModule.class)
public interface DataComponent {

    AppDatabase appDatabase();

    void inject(PartnerMapper mapper);

    void inject(PartnerRepository repository);

    PartnerRepository partnerRepository();

    PartnerMapper partnerMapper();

    PartnerDao partnerDao();

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