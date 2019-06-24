package com.cccdlabs.sarva.domain.interactors.base;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class MockPartnerEmitter implements PartnerEmitter {

    private List<PartnerResult> mPartnerResults;
    private Throwable mError;
    private boolean hasPauseEmitterExecuted;
    private boolean hasResumeEmitterExecuted;

    public MockPartnerEmitter(@NonNull PartnerResult partnerResult) {
        mPartnerResults = new ArrayList<>();
        mPartnerResults.add(partnerResult);
    }

    public MockPartnerEmitter(@NonNull List<PartnerResult> partnerResults) {
        mPartnerResults = partnerResults;
    }

    public MockPartnerEmitter(@NonNull Throwable throwable) {
        mError = throwable;
    }

    @Override
    public Flowable<PartnerResult> getPartnerEmitter() {
        return mError == null
            ? Flowable.fromIterable(mPartnerResults)
            : Flowable.<PartnerResult>error(mError);
    }

    @Override
    public void pauseEmitter() {
        hasPauseEmitterExecuted = true;
    }

    @Override
    public void resumeEmitter() {
        hasResumeEmitterExecuted = true;
    }

    public boolean hasPauseEmitterExecuted() {
        return hasPauseEmitterExecuted;
    }

    public boolean hasResumeEmitterExecuted() {
        return hasResumeEmitterExecuted;
    }
}
