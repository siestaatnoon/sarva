package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.domain.interactors.partners.PartnerBroadcastUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class TestMainModule {

    public TestMainModule() {}

    @Provides @PerActivity PartnerBroadcastUseCase providePartnerBroadcastUseCase() {
        return Mockito.mock(PartnerBroadcastUseCase.class);
    }

    @Provides @PerActivity MainPresenter provideMainPresenter() {
        return Mockito.mock(MainPresenter.class);
    }
}
