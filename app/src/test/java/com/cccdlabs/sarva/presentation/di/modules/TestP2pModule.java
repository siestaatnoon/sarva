package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerBroadcast;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerCheck;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerFind;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerSearch;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerTransmitter;
import com.cccdlabs.sarva.presentation.di.PerActivity;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestP2pModule {

    public TestP2pModule() {}

    @Provides @PerActivity NearbyPartnerBroadcast provideNearbyPartnerBroadcast() {
        return mock(NearbyPartnerBroadcast.class);
    }

    @Provides @PerActivity NearbyPartnerCheck provideNearbyPartnerCheck() {
        return mock(NearbyPartnerCheck.class);
    }

    @Provides @PerActivity NearbyPartnerFind provideNearbyPartnerFind() {
        return mock(NearbyPartnerFind.class);
    }

    @Provides @PerActivity NearbyPartnerSearch provideNearbyPartnerSearch() {
        return mock(NearbyPartnerSearch.class);
    }

    @Provides @PerActivity NearbyPartnerTransmitter provideNearbyPartnerTransmitter() {
        return mock(NearbyPartnerTransmitter.class);
    }
}
