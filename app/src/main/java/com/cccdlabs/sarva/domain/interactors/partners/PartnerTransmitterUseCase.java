package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.PartnerTransmitterEmitter;
import com.cccdlabs.sarva.domain.interactors.partners.base.PartnerUseCase;

import javax.inject.Inject;

/**
 * {@link com.cccdlabs.sarva.domain.interactors.base.UseCase} to broadcast and receive P2P
 * info to and from other devices by emitting to other devices that this device is
 * within proximity in search mode.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerTransmitterUseCase extends PartnerUseCase {

    /**
     * {@inheritDoc}
     */
    @Inject
    public PartnerTransmitterUseCase(@NonNull PartnerTransmitterEmitter emitter) {
        super(emitter);
    }
}
