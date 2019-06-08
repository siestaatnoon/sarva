package com.cccdlabs.sarva.presentation.di.modules;

import android.app.Activity;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerBroadcast;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerCheck;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerFind;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerSearch;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerTransmitter;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.presentation.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class P2pModule {

    public P2pModule() {}

    @Provides @PerActivity
    NearbyPartnerBroadcast provideNearbyPartnerBroadcast(Activity activity) {
        return new NearbyPartnerBroadcast(activity);
    }

    @Provides @PerActivity
    NearbyPartnerCheck provideNearbyPartnerCheck(Activity activity, PartnerRepository repository) {
        return new NearbyPartnerCheck(activity, repository);
    }

    @Provides @PerActivity
    NearbyPartnerFind provideNearbyPartnerFind(Activity activity, PartnerRepository repository) {
        return new NearbyPartnerFind(activity, repository);
    }

    @Provides @PerActivity
    NearbyPartnerSearch provideNearbyPartnerSearch(Activity activity, PartnerRepository repository) {
        return new NearbyPartnerSearch(activity, repository);
    }

    @Provides @PerActivity
    NearbyPartnerTransmitter provideNearbyPartnerTransmitter(Activity activity, PartnerRepository repository) {
        return new NearbyPartnerTransmitter(activity, repository);
    }
}
