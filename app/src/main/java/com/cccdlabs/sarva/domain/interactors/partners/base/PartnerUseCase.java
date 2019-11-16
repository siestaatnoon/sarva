package com.cccdlabs.sarva.domain.interactors.partners.base;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import io.reactivex.Flowable;

public class PartnerUseCase extends AbstractUseCase<Void, PartnerResult> {

    protected PartnerEmitter mEmitter;

    /**
     * {@inheritDoc}
     */
    public PartnerUseCase(@NonNull PartnerEmitter emitter) {
        mEmitter = emitter;
    }

    public Flowable<PartnerResult> emit(Void v) {
        return mEmitter.getPartnerFlowable();
    }
}
