package com.cccdlabs.sarva.domain.interactors.partners.base;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.interactors.base.AbstractUseCase;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import io.reactivex.Flowable;

abstract public class AbstractPartnerUseCase extends AbstractUseCase<Void, PartnerResult> {

    protected PartnerEmitter emitter;

    public AbstractPartnerUseCase(@NonNull PartnerEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public Flowable<PartnerResult> emit(Void parameter) {
        return emitter.getPartnerEmitter();
    }

    public void pauseEmitterSource() {
        emitter.pauseEmitter();
    }

    public void resumeEmitterSource() {
        emitter.resumeEmitter();
    }
}
