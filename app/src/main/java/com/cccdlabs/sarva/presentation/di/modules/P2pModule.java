package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerBroadcastEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerCheckEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerFindEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerSearchEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerTransmitterEmitter;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.presentation.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class P2pModule {

    private Activity activity;
    private PartnerRepository repository;

    public P2pModule(Activity activity) {
        this(activity, null);
    }

    public P2pModule(Activity activity, PartnerRepository repository) {
        this.activity = activity;
        this.repository = repository;
    }

    @Provides @PerActivity
    PartnerBroadcastEmitter providePartnerBroadcastEmitter() {
        return new PartnerBroadcastEmitter(activity);
    }

    @Provides @PerActivity
    PartnerCheckEmitter providePartnerCheckEmitter() {
        return new PartnerCheckEmitter(activity, repository);
    }

    @Provides @PerActivity
    PartnerFindEmitter providePartnerFindEmitter() {
        return new PartnerFindEmitter(activity, repository);
    }

    @Provides @PerActivity
    PartnerSearchEmitter providePartnerSearchEmitter() {
        return new PartnerSearchEmitter(activity, repository);
    }

    @Provides @PerActivity
    PartnerTransmitterEmitter providePartnerTransmitterEmitter() {
        return new PartnerTransmitterEmitter(activity, repository);
    }
}
