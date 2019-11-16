package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.BuildConfig;
import com.cccdlabs.sarva.data.p2p.nearby.client.PartnerNearbyMessagesClient;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.p2p.base.PartnerEmitter;

import io.reactivex.Flowable;

public class PartnerBroadcastEmitter implements PartnerEmitter {

    protected final Activity mActivity;
    protected PartnerNearbyMessagesClient mClient;

    public PartnerBroadcastEmitter(@NonNull Activity activity) {
        mActivity = activity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flowable<PartnerResult> getPartnerFlowable() {
        return getClient().getPartnerFlowable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPublishing() {
        return mClient != null && mClient.isPublishing();
    }

    /**
     * This class does not subscribe to messages.
     */
    @Override
    public boolean isSubscribing() {
        return false;
    }

    protected PartnerNearbyMessagesClient getClient() {
        if (mClient == null || mClient.hasFinished()) {
            mClient = null;
            mClient = new PartnerNearbyMessagesClient.Builder(mActivity)
                    .hasPublish(true)
                    .hasSubscribe(false)
                    .setPublishMode(PartnerMessage.Mode.PING)
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
