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
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableEmitter;

/**
 * Nearby Messages implementation to notify other devices that this device is "here" within
 * proximity, including sending approximate distance and signal strength. Also receives messages
 * from other devices, emitting approximate distance and signal strength for those that are
 * within proximity .
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NearbyPartnerTransmitter extends AbstractNearbyPartnerEmitter {

    /**
     * List of {@link Partner}s or devices found by this device. A partner is added to the list
     * on the MessageListener <code>onFound()</code> and removed from an <code>onFound()</code>
     * call.
     */
    private List<String> mPartners;

    /**
     * A {@link MessageListener} that subscribes to Partner {@link Message}s (from other device
     * users saved on this device) and, when at least one is received, will begin publishing to
     * to notify other devices that this device is within range.
     * <p>
     * Note that emitting flag of the <code>Partner</code> is set to true when the user device
     * is discovered by <code>onFound()</code> and is set to false when user device communication
     * is lost from <code>onLost()</code>.
     * <p>
     * To conserve resources, <code>publish()</code> is called only after the first device Message
     * is received from <code>onFound()</code> and <code>unpublish()</code> is called after all
     * devices have lost connection from <code>onLost()</code>.
     */
    class PartnerTransceiverMessageListener extends PartnerMessageListener {

        /**
         * Constructor.
         *
         * @param emitter The RxJava {@link FlowableEmitter} to emit partner data to subscribers
         *                in the app
         */
        PartnerTransceiverMessageListener(FlowableEmitter<PartnerResult> emitter) {
            super(emitter, PartnerMessage.Mode.SEARCH);
        }

        /**
         * Waits for at least one partner message received from another device to begin
         * publishing a message to other devices, essentially pinging devices within range.
         * All partners found are recorded so upon the first found, publishing is triggered and
         * upon the last partner lost, publishing is stopped to conserve battery.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits
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

            if (mPartners.size() == 0) {
                // Another device within range, begin publishing
                publish();
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setEmitting(true); // Set the partner to true since within range
            trackPartner(partner, false);
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Creates a {@link Partner} object for the user, sets the emitting flag to false and emits
         * it through a {@link PartnerResult}. Removes the partner from tracking and, if no partners
         * left to receive a message from this device, publishing stops.
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

            Partner partner = NearbyUtils.toPartnerModel(message);
            trackPartner(partner, true);
            if (mPartners.size() == 0) {
                // if no partners within range, unpublish
                unpublish();
            }

            partner.setEmitting(false); // Set the partner to false since out of range
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Emits a partner result when the distance between a remote partner and this device changes.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits it
         * through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message   The Nearby message received from a user device
         * @param distance  The {@link Distance} object containing approximate distance in
         *                  meters and accuracy of measurement
         */
        @Override
        public void onDistanceChanged(Message message, Distance distance) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setDistance(distance.getMeters());
            partner.setAccuracy(distance.getAccuracy());
            partner.setEmitting(true); // Set the partner to true since within range
            trackPartner(partner, false);
            emitter.onNext(new PartnerResult(partner));
        }

        /**
         * Emits a partner result when the Bluetooth BLE signal between a remote partner and this
         * device changes.
         * <p>
         * Creates a {@link Partner} object from the user, sets the emitting flag to true and emits
         * it through a {@link PartnerResult}.
         * <p>
         * Errors occurring within this call are passed via a <code>PartnerResult</code> and emitted
         * keeping the subscription flow active.
         *
         * @param message   The Nearby message received from a user device
         * @param bleSignal The {@link BleSignal} object containing RSSI value and TX power from
         *                  remote user device
         */
        @Override
        public void onBleSignalChanged(Message message, BleSignal bleSignal) {
            if (checkInvalidState(message)) {
                // error already emitted in
                // Observable so exit
                return;
            }

            Partner partner = NearbyUtils.toPartnerModel(message);
            partner.setRssi(bleSignal.getRssi());
            partner.setTxPower(bleSignal.getTxPower());
            partner.setEmitting(true); // Set the partner to true since within range
            trackPartner(partner, false);
            emitter.onNext(new PartnerResult(partner));
        }
    }


    /**
     * {@inheritDoc}
     */
    public NearbyPartnerTransmitter(@NonNull Activity activity, @NonNull PartnerRepository repository) {
        super(activity, repository);
        mPartners = new ArrayList<>();
        enablePublishOnStartEmitter(false);
    }

    /**
     * {@inheritDoc}
     */
    public NearbyPartnerTransmitter(@NonNull Context context, @NonNull PartnerRepository repository) {
        super(context, repository);
        mPartners = new ArrayList<>();
        enablePublishOnStartEmitter(false);
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
            ? new PartnerTransceiverMessageListener(emitter)
            : messageListener;
    }

    /**
     * Message type, or {@link PartnerMessage.Mode}, this class uses for Nearby publishing.
     *
     * @return  The Mode, published message type, <code>Mode.PING</code>
     * @see     PartnerMessage.Mode
     */
    @Override
    protected PartnerMessage.Mode getPublishMode() {
        return PartnerMessage.Mode.PING;
    }

    /**
     * Overrides parent method to clear Partner list storage.
     */
    @Override
    protected void cleanUp() {
        super.cleanUp();
        if (mPartners != null) {
            mPartners.clear();
            mPartners = null;
        }
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
}
