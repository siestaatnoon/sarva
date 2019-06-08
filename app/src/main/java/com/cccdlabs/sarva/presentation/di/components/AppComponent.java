package com.cccdlabs.sarva.presentation.di.components;

import android.content.Context;

import com.cccdlabs.sarva.data.mappers.partners.PartnerMapper;
import com.cccdlabs.sarva.data.network.retrofit.RestCaller;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.data.storage.dao.partners.PartnerDao;
import com.cccdlabs.sarva.data.storage.database.AppDatabase;
import com.cccdlabs.sarva.domain.executor.ComputationThread;
import com.cccdlabs.sarva.domain.executor.ExecutorThread;
import com.cccdlabs.sarva.domain.executor.MainThread;
import com.cccdlabs.sarva.presentation.di.modules.AppModule;
import com.cccdlabs.sarva.presentation.di.modules.DataModule;
import com.cccdlabs.sarva.presentation.ui.activities.base.BaseAppCompatActivity;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Lazy;

@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {

    void inject(BaseAppCompatActivity activity);

    void inject(RestCaller restCaller);

    void inject(PartnerMapper mapper);

    void inject(PartnerRepository repository);

    Context context();

    MainThread mainThread();

    ExecutorThread executorThread();

    Lazy<ComputationThread> computationThread();

    AppDatabase appDatabase();

    PartnerRepository partnerRepository();

    PartnerMapper partnerMapper();

    PartnerDao partnerDao();
}
