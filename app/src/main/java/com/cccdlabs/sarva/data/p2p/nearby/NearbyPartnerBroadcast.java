package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.messages.MessageListener;
import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

public class NearbyPartnerBroadcast extends AbstractNearbyPartnerEmitter {

    public NearbyPartnerBroadcast(@NonNull Activity activity) {
        super(activity, null);
    }

    public NearbyPartnerBroadcast(@NonNull Context context) {
        super(context, null);
    }

    @Override
    protected void initFlowable() {
        flowable = Flowable.create(
                new FlowableOnSubscribe<PartnerResult>() {
                    @Override
                    public void subscribe(final FlowableEmitter<PartnerResult> emitter) throws Exception {
                        try {
                            registerEmitter(emitter);
                            emitter.setCancellable(new Cancellable() {
                                @Override
                                public void cancel() throws Exception {
                                    cleanUp();
                                    emitter.onComplete(); // Up to subscriber to cancel Flowable
                                }
                            });
                            publish(PartnerMessage.Mode.PAIR);
                            emitter.onNext(new PartnerResult(true));
                        } catch (Exception e) {
                            if (!emitter.isCancelled()) {
                                emitter.onError(e);
                            }
                        }
                    }
                },
                BackpressureStrategy.MISSING
        );
    }

    @Override
    protected void subscribe(FlowableEmitter<PartnerResult> emitter) {
        // This class will not perform subscribe, so override to perform noop
    }

    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return null;
    }
}
