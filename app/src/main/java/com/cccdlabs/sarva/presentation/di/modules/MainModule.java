package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerBroadcast;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.interactors.partners.PartnerBroadcastUseCase;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.presenters.MainPresenter;
import com.cccdlabs.sarva.presentation.views.MainView;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    public MainModule() {}

    @Provides @PerActivity PartnerBroadcastUseCase providePartnerBroadcastUseCase(NearbyPartnerBroadcast nearby) {
        return new PartnerBroadcastUseCase(nearby);
    }

    @Provides @PerActivity MainPresenter provideMainPresenter(PartnerRepository repository, Activity activity) {
        return new MainPresenter(repository, (MainView) activity);
    }
}
