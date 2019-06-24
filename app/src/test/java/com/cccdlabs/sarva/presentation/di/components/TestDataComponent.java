package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.presentation.di.modules.TestDataModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = TestDataModule.class)
public interface TestDataComponent extends DataComponent {

    AppDatabase appDatabase();

    PartnerDao partnerDao();
}
