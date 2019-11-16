package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerCheckEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.base.PartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices for conducting a confirmation of devices that are saved on
 * this device (as well as others).
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerCheckUseCase extends PartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerCheckUseCase(@NonNull PartnerCheckEmitter emitter) {
        super(emitter);
    }
}
