package com.oscarrrweb.sarva.domain.interactors.partners;

import androidx.annotation.NonNull;

import com.oscarrrweb.sarva.domain.interactors.base.AbstractUseCase;
import com.oscarrrweb.sarva.domain.model.partners.PartnerResult;
import com.oscarrrweb.sarva.domain.p2p.base.PartnerEmitter;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class PartnerSearchUseCase extends AbstractUseCase<Void, PartnerResult> {

    private PartnerEmitter emitter;

    @Inject
    public PartnerSearchUseCase(@NonNull PartnerEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public Flowable<PartnerResult> emit(Void parameter) {
        return emitter.getPartnerEmitter();
    }
}
