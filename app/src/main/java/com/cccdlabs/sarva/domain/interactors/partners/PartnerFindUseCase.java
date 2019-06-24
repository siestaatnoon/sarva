package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.NearbyPartnerFind;
import com.cccdlabs.sarva.domain.interactors.partners.base.AbstractPartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices for finding and saving other partner devices to this
 * device.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerFindUseCase extends AbstractPartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerFindUseCase(@NonNull NearbyPartnerFind emitter) {
        super(emitter);
    }
}
