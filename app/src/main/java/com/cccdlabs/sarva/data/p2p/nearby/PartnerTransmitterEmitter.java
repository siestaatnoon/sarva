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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class PartnerTransmitterEmitter implements PartnerEmitter {

    protected final Activity mActivity;
    protected final PartnerRepository mRepository;
    protected PartnerNearbyMessagesClient mClient;

    /**
     * List of {@link Partner}s or devices found by this device. A partner is added to the list
     * on the MessageListener <code>onFound()</code> and removed from an <code>onFound()</code>
     * call.
     */
    private List<String> mPartners;

    public PartnerTransmitterEmitter(@NonNull Activity activity, @NonNull PartnerRepository repository ) {
        mActivity = activity;
        mRepository = repository;
        mPartners = new ArrayList<>();
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

                        trackPartner(partner, partner.isEmitting());
                        int size = mPartners.size();
                        if (size == 0) {
                            // No partners within range
                            // [should unpublish here]
                        } else if (size == 1) {
                            // At least one partners in range
                            // [should publish here]
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
                    .setPublishMode(PartnerMessage.Mode.PING)
                    .setDebug(BuildConfig.DEBUG)
                    .build();
        }

        return mClient;
    }

    /**
     * Tracks each partner connection found from<code> onFound()</code> and each partner
     * connection lost from <code>onLost()</code>, respectively adding to a List container and
     * removing from it.
     *
     * @param partner   The {@link Partner} to track or remove from tracking
     * @param isLost    True to add the partner, false to remove partner
     */
    protected void trackPartner(Partner partner, boolean isLost) {
        if (partner == null) {
            return;
        }

        String uuid = partner.getUuid();
        if (isLost) {
            if (uuid != null) {
                mPartners.remove(uuid);
            }
        } else if (!mPartners.contains(uuid)) {
            mPartners.add(uuid);
        }
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
