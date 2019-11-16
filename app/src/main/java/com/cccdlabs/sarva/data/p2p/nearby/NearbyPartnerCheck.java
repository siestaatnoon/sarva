package com.cccdlabs.sarva.data.p2p.nearby;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.cccdlabs.sarva.data.p2p.nearby.base.AbstractNearbyPartnerEmitter;
import com.cccdlabs.sarva.data.p2p.nearby.utils.NearbyUtils;
import com.cccdlabs.sarva.data.repository.partners.PartnerRepository;
import com.cccdlabs.sarva.domain.model.partners.Partner;
import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;
import com.cccdlabs.sarva.domain.model.partners.PartnerResult;
import com.cccdlabs.sarva.domain.repository.exception.RepositoryException;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import io.reactivex.FlowableEmitter;

/**
 * Nearby Messages implementation to notify other devices that this device is "here" within
 * proximity, plus receive messages from other devices that they are within proximity.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NearbyPartnerCheck extends AbstractNearbyPartnerEmitter {

    /**
     * A {@link MessageListener} that verifies from received {@link Message}s that a user
     * device has been saved on this device, then emits that user info to presenter layer
     * subscribers in a {@link Partner} object (via a {@link PartnerResult}).
     * <p>
     * Note that emitting flag of the <code>Partner</code> is set to true when the user device
     * is discovered by <code>onFound()</code> and is set to false when user device communication
     * is lost from <code>onLost()</code>.
     */
    class PartnerCheckMessageListener extends PartnerMessageListener {

        /**
         * Constructor.
         *
         * @param emitter The RxJava {@link FlowableEmitter} to emit partner data to subscribers
         *                in the app
         */
        PartnerCheckMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.CHECK, false);
        }

        /**
         * Verifies that a device user from the {@link Message} received has previously been saved,
         * creates a {@link Partner} object for the user, sets the emitting flag to true and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onFound(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner model = NearbyUtils.toPartnerModel(message);
            try {
                model = repository.sync(model);
                repository.setActive(model.getUuid());
                model.setEmitting(true);
            } catch (RepositoryException e) {
                continueWithError(e);
                return;
            }

            emitter.onNext(new PartnerResult(model));
        }

        /**
         * Verifies that a device user from the {@link Message} received has previously been saved,
         * creates a {@link Partner} object for the user, sets the emitting flag to false and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message The Nearby message received from a user device
         */
        @Override
        public void onLost(Message message) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner model = NearbyUtils.toPartnerModel(message);
            model.setEmitting(false);
            emitter.onNext(new PartnerResult(model));
        }
    }

    /**
     * Constructor. When passed in an {@link Activity}, notifications from the Nearby library will be
     * prompted from it for resolution of resolvable connection errors.
     * <p>
     * Note that <code>super.publish()</code> is called here to start publishing.
     *
     * @param activity      The Activity utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public NearbyPartnerCheck(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
    }

    /**
     * Constructor. When passed in an {@link Context}, notifications from the Nearby library will
     * be through a system notification for resolution of resolvable connection errors.
     * <p>
     * Note that <code>super.publish()</code> is called here to start publishing.
     *
     * @param context       The Android context utilizing the Nearby Messaging
     * @param repository    The PartnerRepository for database functions
     */
    public NearbyPartnerCheck(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
    }

    /**
     * Returns the MessageListener handling Nearby {@link Message} received in this class. Acts
     * as a sort of singleton returning a new instance if one hasn't already been created or was
     * previously destroyed.
     *
     * @param emitter   The RxJava {@link FlowableEmitter} to emit data from messages received
     *                  from Nearby subscriptions
     * @return          The MessageListener
     */
    @Override
    protected MessageListener getMessageListener(FlowableEmitter<PartnerResult> emitter) {
        return messageListener == null
            ? new PartnerCheckMessageListener(emitter)
            : messageListener;
    }

    /**
     * Message type, or {@link PartnerMessage.Mode}, this class uses for Nearby publishing.
     *
     * @return  The Mode, or published message type, <code>Mode.CHECK</code>
     * @see     PartnerMessage.Mode
     */
    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.CHECK;
    }
}
