package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.google.android.gms.nearby.messages.MessageListener;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Nearby Messages implementation to perform a broadcast to other devices to "pair" this device,
 * or save this device user on another user's device.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NearbyPartnerBroadcast extends AbstractNearbyPartnerEmitter {

    /**
     * Constructor. When passed in an {@link Activity}, notifications from the Nearby library will
     * be prompted from it for resolution of resolvable connection errors. Note that a
     * {@link com.cccdlabs.sarva.data.repository.partners.PartnerRepository} is not used since
     * this class will only publish with Nearby Messages and not subscribe and save info from
     * other devices.
     *
     * @param activity The Activity utilizing the Nearby Messaging
     */
    public NearbyPartnerBroadcast(@NonNull Activity activity) {
        super(activity, null);
    }

    /**
     * Constructor. When passed in an {@link Context}, notifications from the Nearby library will
     * be through a system notification for resolution of resolvable connection errors. Note that
     * a {@link com.cccdlabs.sarva.data.repository.partners.PartnerRepository} is not used since
     * this class will only publish with Nearby Messages and not subscribe and save info from
     * other devices.
     *
     * @param context The Android context utilizing the Nearby Messaging
     */
    public NearbyPartnerBroadcast(@NonNull Context context) {
        super(context, null);
    }

    /**
     * Overrides the parent class method to initialize a {@link Flowable} that will not subscribe
     * since this class only publishes.
     */
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
                            publish();
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

    /**
     * Overrided to perform a no op. This class will not perform a Nearby Messages subscribe.
     *
     * @param emitter The RxJava FlowableEmitter
     */
    @Override
    protected void subscribe(FlowableEmitter<PartnerResult> emitter) {}

    /**
     * Overrided to return a null {@link MessageListener}. This class will not perform a Nearby
     * Messages subscribe.
     *
     * @param emitter   The RxJava FlowableEmitter
     * @return          Null is returned
     */
    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return null;
    }

    /**
     * Returns the message type, or {@link PartnerMessage.Mode}, for publishing by this class.
     *
     * @return The Mode, or published message type, <code>Mode.PAIR</code>
     * @see PartnerMessage.Mode
     */
    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.PAIR;
    }
}
