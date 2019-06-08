package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.interactors.partners.base.AbstractPartnerUseCase;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import javax.inject.Inject;

public class PartnerSearchUseCase extends AbstractPartnerUseCase {

    @Inject
    public PartnerSearchUseCase(@NonNull PartnerEmitter emitter) {
        super(emitter);
    }
}