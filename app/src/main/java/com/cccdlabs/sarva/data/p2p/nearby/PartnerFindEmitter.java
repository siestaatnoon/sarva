package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.BuildConfig;
import com.cccdlabs.sarva.data.p2p.nearby.client.PartnerNearbyMessagesClient;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class PartnerFindEmitter implements PartnerEmitter {

    protected final Activity mActivity;
    protected final PartnerRepository mRepository;
    protected PartnerNearbyMessagesClient mClient;

    public PartnerFindEmitter(@NonNull Activity activity, @NonNull PartnerRepository repository ) {
        mActivity = activity;
        mRepository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flowable<PartnerResult> getPartnerFlowable() {
        return getClient().getPartnerFlowable().map(
                new Function<PartnerResult, PartnerResult>() {
                    @Override
                    public PartnerResult apply(PartnerResult partnerResult) throws Exception {
                        Partner partner = partnerResult.getPartner();
                        if (partner == null) {
                            // Throwable or PartnerResult.Status object passed through
                            return partnerResult;
                        }

                        if (partner.isEmitting()) {
                            // Partner just found so sync in db
                            partner = mRepository.sync(partner);
                        }
                        return new PartnerResult(partner);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublishing() {
        return mClient != null && mClient.isPublishing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSubscribing() {
        return mClient != null && mClient.isSubscribing();
    }

    protected PartnerNearbyMessagesClient getClient() {
        if (mClient == null || mClient.hasFinished()) {
            mClient = null;
            mClient = new PartnerNearbyMessagesClient.Builder(mActivity)
                    .hasPublish(true)
                    .hasSubscribe(true)
                    .setPublishMode(PartnerMessage.Mode.PAIR)
                    .setDebug(BuildConfig.DEBUG)
                    .build();
        }

        return mClient;
    }

    @Override
    public void startEmitter() {}

    @Override
    public void pauseEmitter() {}

    @Override
    public void resetEmitter() {}

    @Override
    public void resumeEmitter() {}
}
