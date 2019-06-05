package com.cccdlabs.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class PartnerBroadcastUseCase extends AbstractUseCase<Void, PartnerResult> {

    private PartnerEmitter emitter;

    @Inject
    public PartnerBroadcastUseCase(@NonNull PartnerEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public Flowable<PartnerResult> emit(Void parameter) {
        return emitter.getPartnerEmitter();
    }
}
