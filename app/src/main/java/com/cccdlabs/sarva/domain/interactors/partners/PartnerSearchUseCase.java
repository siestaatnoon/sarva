package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerSearch;
import com.cccdlabs.sarva.domain.interactors.partners.base.AbstractPartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices for conducting a search for other partner devices.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerSearchUseCase extends AbstractPartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerSearchUseCase(@NonNull NearbyPartnerSearch emitter) {
        super(emitter);
    }
}