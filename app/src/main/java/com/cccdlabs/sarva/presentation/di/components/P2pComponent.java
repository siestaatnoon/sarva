package com.cccdlabs.sarva.presentation.di.components;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerBroadcast;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerCheck;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerFind;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerSearch;
import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerTransmitter;
import com.cccdlabs.sarva.presentation.di.PerActivity;
import com.cccdlabs.sarva.presentation.di.modules.ActivityModule;
import com.cccdlabs.sarva.presentation.di.modules.P2pModule;

import dagger.Component;

@PerActivity
@Component(dependencies = {AppComponent.class}, modules = {ActivityModule.class, P2pModule.class})
public interface P2pComponent {

    NearbyPartnerBroadcast nearbyPartnerBroadcast();

    NearbyPartnerCheck nearbyPartnerCheck();

    NearbyPartnerFind nearbyPartnerFind();

    NearbyPartnerSearch nearbyPartnerSearch();

    NearbyPartnerTransmitter nearbyPartnerTransmitter();
}
