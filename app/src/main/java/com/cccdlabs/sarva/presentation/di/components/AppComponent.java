package com.cccdlabs.sarva.presentation.di.components;

import android.content.Context;

import com.cccdlabs.sarva.data.network.retrofit.RestCaller;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
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

    Context context();

    MainThread mainThread();

    ExecutorThread executorThread();

    Lazy<ComputationThread> computationThread();

    PartnerRepository partnerRepository();
}
