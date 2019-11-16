package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerFindEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.base.PartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices for finding and saving other partner devices to this
 * device.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerFindUseCase extends PartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerFindUseCase(@NonNull PartnerFindEmitter emitter) {
        super(emitter);
    }
}
