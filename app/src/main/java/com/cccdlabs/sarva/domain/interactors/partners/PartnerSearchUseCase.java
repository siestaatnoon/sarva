package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerSearchEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.base.PartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices for conducting a search for other partner devices.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerSearchUseCase extends PartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerSearchUseCase(@NonNull PartnerSearchEmitter emitter) {
        super(emitter);
    }
}