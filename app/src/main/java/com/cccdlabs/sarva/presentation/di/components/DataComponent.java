package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.presentation.di.modules.DataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DataModule.class)
public interface DataComponent {

    void inject(PartnerMapper mapper);

    void inject(PartnerRepository repository);

    AppDatabase appDatabase();

    PartnerRepository partnerRepository();

    PartnerMapper partnerMapper();

    PartnerDao partnerDao();
}
