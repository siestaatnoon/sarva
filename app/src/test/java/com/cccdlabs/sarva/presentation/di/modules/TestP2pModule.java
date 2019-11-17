package com.cccdlabs.sarva.presentation.di.modules;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerBroadcastEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerCheckEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerFindEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerSearchEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.PartnerTransmitterEmitter;
import com.cccdlabs.sarva.presentation.di.PerActivity;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class TestP2pModule {

    public TestP2pModule() {}

    @Provides @PerActivity PartnerBroadcastEmitter providePartnerBroadcastEmitter() {
        return mock(PartnerBroadcastEmitter.class);
    }

    @Provides @PerActivity PartnerCheckEmitter providePartnerCheckEmitter() {
        return mock(PartnerCheckEmitter.class);
    }

    @Provides @PerActivity PartnerFindEmitter providePartnerFindEmitter() {
        return mock(PartnerFindEmitter.class);
    }

    @Provides @PerActivity PartnerSearchEmitter providePartnerSearchEmitter() {
        return mock(PartnerSearchEmitter.class);
    }

    @Provides @PerActivity PartnerTransmitterEmitter providePartnerTransmitterEmitter() {
        return mock(PartnerTransmitterEmitter.class);
    }
}
